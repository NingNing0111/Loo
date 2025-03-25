# Rust 客户端重构

## 核心分析

- 客户端与服务端建立连接；
- 客户端发送认证请求：
  - 认证通过进入下一步；
  - 认证不通过退出程序；
- 客户端发送代理请求；
- 客户端接收到 Connect 时，建立本地代理目标的连接并存储连接信息（channelId，connect）
  - 建立成功，向服务端发送 Connect 消息；
  - 建立失败，向服务端发送 Disconnect 消息；
- 客户端接收到 Transfer 时，根据 ChannelId 获取对应的 connect，将数据发送给代理目标；
  - 监听来自代理目标的响应数据，将响应数据写回给服务端

## Channel 设计

&emsp;主要涉及三个 channel:

- `s_channel`: 客户端向服务端写回数据时用到的 channel；
- `r_channel`: 客户端从服务端读取数据时用到的 channel；
- `p_channel`: 本地代理请求时需要与目标程序建立网络连接，此时需要创建一个 process 任务。`p_channel` 是用于与 process 任务进行通信；

## Task 设计

&emsp;主要涉及三大部分共四个任务,搭配 channel 实现：

- 消费者：负责向服务端写入数据的任务：

```rust
    tokio::spawn(async move {
        while let Some(msg) = s_rx.recv().await {
            info!("send to server: {:?}", msg);
            let mut w_msg = BytesMut::with_capacity(1024 * 8);
            msg.encode_length_delimited(&mut w_msg).unwrap();
            writer.write_all(&w_msg).await.unwrap();
        }
    });
```

- 生产者：负责从服务端读出数据的任务：

```rust
    tokio::spawn(async move {
        loop {
            let mut buffer = [0; 1024 * 8];
            let n = reader.read(&mut buffer).await.unwrap();
            let mut read_buf: BytesMut = BytesMut::with_capacity(1024 * 8);
            read_buf.extend_from_slice(&buffer[..n]);
            let server_rsp = TransferDataMessage::decode_length_delimited(read_buf).unwrap();
            info!("response from server: {:?}", server_rsp);
            r_tx.send(server_rsp).await.unwrap();
        }
    });
```

- 针对代理任务的消费者：向目标代理程序写入数据的任务;

```rust
    // 任务2：负责从上层接收数据并写入目标服务
    tokio::spawn(async move {
        while let Some(data) = rx.recv().await {
            if let Err(e) = target_write.write_all(&data).await {
                log::error!("写入目标连接数据失败: {:?}", e);
                break;
            }
        }
    });
```

- 针对代理任务的生产者：从目标代理程序读出数据的任务;

```rust
    tokio::spawn(async move {
        let mut buffer = [0u8; 1024 * 8];
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
```
