use bytes::Bytes;
use tokio::{
    io::{AsyncReadExt, AsyncWriteExt},
    net::TcpStream,
    sync::{broadcast, mpsc},
};

use crate::{
    client::LocalManager,
    common::constants::{LICENSE_KEY, VISITOR_ID},
    core::{cmd_type::CmdType, transfer_message::TransferDataMessage},
    helper::message::{
        build_auth_message, build_connect_message, build_disconnect_message,
        build_open_server_message, build_transfer_message,
    },
    model::{proxy::ProxyConfig, ClientConfig},
};

const FRAME_SIZE: usize = 1024 * 8;

pub async fn client_handler(
    s_tx: mpsc::Sender<TransferDataMessage>,
    mut r_rx: mpsc::Receiver<TransferDataMessage>,
    mut shutdown_rx: broadcast::Receiver<()>,
    local_manager: LocalManager,
    config: ClientConfig,
) -> Result<(), Box<dyn std::error::Error>> {
    // 构建认证消息 并发送给服务端
    let auth_message = build_auth_message(&config.get_password());
    s_tx.clone().send(auth_message).await.unwrap();

    tokio::spawn(async move {
        loop {
            tokio::select! {
                Some(server_rsp) = r_rx.recv() => {
                    let license_key = server_rsp
                        .meta_data
                        .as_ref()
                        .unwrap()
                        .meta_data
                        .get(LICENSE_KEY)
                        .unwrap()
                        .clone();
                    let cmd_type = server_rsp.cmd_type();

                    match cmd_type {
                        CmdType::AuthOk => {
                            let proxys = config.get_proxies();
                            for proxy_config in proxys {
                                let open_server_msg = build_open_server_message(&proxy_config, license_key.clone());
                                s_tx.clone()
                                    .send(open_server_msg)
                                    .await
                                    .expect("send fail!");
                            }
                        }
                        CmdType::AuthErr => {
                            log::error!("服务端认证失败!");
                            break;
                        }
                        CmdType::Connect => {
                            // 创建一个新的 channel 用于与 process 任务通信
                            let (p_tx, p_rx) = mpsc::channel::<Bytes>(32);

                            let meta_data = server_rsp.meta_data.as_ref().unwrap().meta_data.clone();
                            let proxy_config = ProxyConfig::from_map(meta_data.clone()).unwrap();
                            let visitor_id = meta_data.get(VISITOR_ID).unwrap().clone();
                            let target_addr = format!("{}:{}", proxy_config.host(), proxy_config.port());
                            local_manager
                                .put_sender(visitor_id.clone(), p_tx)
                                .await;

                            local_proxy_handler(
                                proxy_config,
                                license_key.clone(),
                                visitor_id.clone(),
                                p_rx,
                                target_addr.as_str(),
                                s_tx.clone(),
                            )
                            .await
                            .expect("process fail!");
                        }
                        CmdType::Disconnect => {
                            let meta_data = server_rsp.meta_data.as_ref().unwrap().meta_data.clone();
                            let visitor_id = meta_data.get(VISITOR_ID).unwrap().clone();
                            log::error!("收到 disconnect 消息，visitor_id: {}", visitor_id);
                            // 移除并关闭对应的 sender，通知 process 内部任务退出
                            local_manager.remove_sender(&visitor_id).await;
                        }
                        CmdType::Transfer => {
                            let meta_data = server_rsp.meta_data.as_ref().unwrap().meta_data.clone();
                            let visitor_id = meta_data.get(VISITOR_ID).unwrap().clone();
                            let sender = local_manager
                                .get_sender(&visitor_id)
                                .await
                                .unwrap();
                            let data = server_rsp.data.clone();
                            sender.send(Bytes::from(data)).await.unwrap();
                        }
                        _ => {
                            log::error!("未知指令类型");
                            break;
                        }
                    };
                },
                _ = shutdown_rx.recv() => {
                    log::info!("停止 [Client Core Handler] Task...");
                    break;
                }
            };
        }
    });

    Ok(())
}

/// 本地代理处理
async fn local_proxy_handler(
    proxy_config: ProxyConfig,
    license_key: String,
    visitor_id: String,
    mut rx: mpsc::Receiver<Bytes>,
    target_addr: &str,
    s_tx: mpsc::Sender<TransferDataMessage>,
) -> Result<(), Box<dyn std::error::Error>> {
    // 建立与目标服务的 TCP 连接，并拆分为读写半部
    let target_connect = match TcpStream::connect(target_addr).await {
        Ok(stream) => stream,
        Err(e) => {
            log::error!("连接目标服务失败: {:?}", e);
            // 发送disconnect
            let disconnect_msg = build_disconnect_message(license_key.clone(), visitor_id.clone());
            s_tx.send(disconnect_msg).await?;
            return Err(e.into());
        }
    };
    let (mut target_read, mut target_write) = target_connect.into_split();

    // 先发送连接建立消息给服务端
    let connect_msg = build_connect_message(proxy_config, license_key.clone(), visitor_id.clone());
    s_tx.send(connect_msg).await?;

    // 任务1：负责从目标服务读取数据，并构造 transfer 消息转发给服务端
    let s_tx_clone = s_tx.clone();
    let visitor_id_clone = visitor_id.clone();

    tokio::spawn(async move {
        let mut buffer = [0u8; FRAME_SIZE];
        loop {
            let n = match target_read.read(&mut buffer).await {
                Ok(n) if n == 0 => break, // 连接关闭
                Ok(n) => n,
                Err(e) => {
                    log::error!("从目标连接读取数据失败: {:?}", e);
                    break;
                }
            };
            let data = Bytes::copy_from_slice(&buffer[..n]);
            let transfer_msg = build_transfer_message(
                data.to_vec(),
                visitor_id_clone.clone(),
                license_key.clone(),
            );
            if let Err(e) = s_tx_clone.send(transfer_msg).await {
                log::error!("发送转发消息失败: {:?}", e);
                break;
            }
        }
    });

    // 任务2：负责从上层接收数据并写入目标服务
    tokio::spawn(async move {
        while let Some(data) = rx.recv().await {
            if let Err(e) = target_write.write_all(&data).await {
                log::error!("写入目标连接数据失败: {:?}", e);
                break;
            }
        }
    });

    Ok(())
}
