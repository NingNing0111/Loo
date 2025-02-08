import { ClientConfig, startApp, stopApp } from '@/command/client';
import { ReactComponent as SvgNetworkConnected } from '@/icons/proxy/NetworkConnected.svg';
import { ReactComponent as SvgNetworkDisconnect } from '@/icons/proxy/NetworkDisconnect.svg';
import { LocalProxyConfig, ServerConfig } from '@/models/types';
import { EyeInvisibleOutlined, EyeOutlined } from '@ant-design/icons';
import {
  PageContainer,
  ProCard,
  ProDescriptions,
  ProTable,
} from '@ant-design/pro-components';
import { Button, Empty, Flex, message, Switch, Tag, Tooltip } from 'antd';
import dayjs from 'dayjs';
import { useState } from 'react';
import './index.less';

const EMPTY_SERVER_INFO: ServerConfig = {
  id: -1,
  serverHost: '-',
  serverPort: -1,
  password: '',
};

const ProxyPage: React.FC = () => {
  const [messageApi, contextHolder] = message.useMessage();
  const [isStart, setIsStart] = useState(false);
  const [visible, setVisible] = useState(false);
  const [curServerInfo, setCurServerInfo] =
    useState<ServerConfig>(EMPTY_SERVER_INFO);
  const [curProxyConfigList, setCurLocalProxyConfigList] = useState<
    LocalProxyConfig[]
  >([]);
  const [serverConfigList, setServerConfigList] = useState<ServerConfig[]>([]);
  const [localProxyConfigList, setLocalProxyConfigList] = useState<
    LocalProxyConfig[]
  >([]);

  const toggleVisible = () => {
    setVisible((prev) => !prev);
  };

  const serverColumns = [
    {
      title: 'ID',
      dataIndex: 'id',
    },
    {
      title: '主机名',
      dataIndex: 'host',
    },
    {
      title: '端口',
      dataIndex: 'port',
    },
    {
      title: '认证密码',
      dataIndex: 'password',
    },
    {
      title: '创建时间',
      dataIndex: 'createTime',
    },
  ];

  const localProxyColumns = [
    {
      title: 'ID',
      dataIndex: 'id',
    },
    {
      title: '主机名',
      dataIndex: 'host',
    },
    {
      title: '端口',
      dataIndex: 'port',
    },
    {
      title: '开放端口',
      dataIndex: 'openPort',
    },
    {
      title: '协议',
      dataIndex: 'protocol',
    },
    {
      title: '创建时间',
      dataIndex: 'createTime',
    },
  ];

  const start = async () => {
    let app_config: ClientConfig = {
      ...curServerInfo,
      proxies: curProxyConfigList,
    };
    let res = await startApp(app_config);
    if (res.code === 0) {
      setIsStart(true);
      messageApi.open({
        type: 'success',
        content: res.msg,
      });
      // 插入连接日志
    } else {
      setIsStart(false);
      console.log(res.err);

      messageApi.open({
        type: 'error',
        content: res.err,
      });
    }
  };

  const stop = async () => {
    let res = await stopApp();
    if (res.code === 0) {
      messageApi.open({
        type: 'success',
        content: res.msg,
      });
      // 插入连接日志
    } else {
      messageApi.open({
        type: 'error',
        content: res.err,
      });
    }
    setIsStart(false);
  };

  const onApp = async () => {
    if (!isStart) {
      await start();
    } else {
      await stop();
    }
  };

  return (
    <>
      {contextHolder}
      <PageContainer ghost>
        <ProCard split="vertical" bordered className="content-card ">
          <ProCard
            colSpan="30%"
            extra={
              <div style={{ height: 30 }}>
                {/* <Button
                  icon={<SvgNetworkTest />}
                  color="primary"
                  variant="filled"
                >
                  连接测试
                </Button> */}
              </div>
            }
          >
            <Flex gap="middle" vertical justify="center" align="center">
              {isStart ? (
                <SvgNetworkConnected className="network-icon" />
              ) : (
                <SvgNetworkDisconnect className="network-icon" />
              )}
              <Flex justify="center" align="center">
                <Switch
                  onChange={onApp}
                  checkedChildren="开启"
                  unCheckedChildren="关闭"
                  value={isStart}
                />
              </Flex>
            </Flex>
          </ProCard>
          <ProCard
            title="连接信息"
            headerBordered
            extra={
              <Button color="primary" variant="filled">
                切换服务
              </Button>
            }
          >
            <ProDescriptions column={1}>
              <ProDescriptions.Item label="主机名">
                {isStart ? (
                  <Tag color="success">{curServerInfo.serverHost}</Tag>
                ) : (
                  <Tag color="warning">未连接</Tag>
                )}
              </ProDescriptions.Item>
              <ProDescriptions.Item label="接入端口">
                {isStart ? (
                  <Tag color="success">{curServerInfo.serverPort}</Tag>
                ) : (
                  <Tag color="warning">未连接</Tag>
                )}
              </ProDescriptions.Item>
              <ProDescriptions.Item label="连接密码">
                {isStart ? (
                  <span>
                    {visible ? curServerInfo.password : '******'}
                    <Tooltip title={visible ? '隐藏密码' : '显示密码'}>
                      {visible ? (
                        <EyeInvisibleOutlined
                          onClick={toggleVisible}
                          style={{ marginLeft: 8, cursor: 'pointer' }}
                        />
                      ) : (
                        <EyeOutlined
                          onClick={toggleVisible}
                          style={{ marginLeft: 8, cursor: 'pointer' }}
                        />
                      )}
                    </Tooltip>
                  </span>
                ) : (
                  <Tag color="warning">未连接</Tag>
                )}
              </ProDescriptions.Item>
              <ProDescriptions.Item
                label="运行时长"
                fieldProps={{
                  format: 'HH:mm:ss',
                }}
                valueType="dateTime"
                contentStyle={{
                  color: isStart ? '#18bd18' : 'black',
                  fontSize: '16px',
                }}
              >
                {isStart ? dayjs().valueOf() : 0}
              </ProDescriptions.Item>
            </ProDescriptions>
          </ProCard>
          <ProCard
            title="配置详情"
            headerBordered
            extra={
              <Button color="primary" variant="filled">
                选择代理配置
              </Button>
            }
          >
            {isStart ? (
              <>
                {curProxyConfigList.map((item) => (
                  <ProCard
                    collapsible
                    defaultCollapsed
                    bordered
                    headerBordered
                    title="localhost:3306"
                    key={item.id}
                  >
                    <ProDescriptions column={1}>
                      <ProDescriptions.Item label="穿透协议">
                        {item.protocol === 'tcp' ? (
                          <Tag color="success">TCP</Tag>
                        ) : (
                          <Tag color="warning">UDP</Tag>
                        )}
                      </ProDescriptions.Item>
                      <ProDescriptions.Item label="代理目标主机名">
                        {item.host}
                      </ProDescriptions.Item>
                      <ProDescriptions.Item label="代理目标端口">
                        <Tag color="processing">{item.port}</Tag>
                      </ProDescriptions.Item>
                      <ProDescriptions.Item label="服务端映射端口">
                        <Tag color="warning">{item.openPort}</Tag>
                      </ProDescriptions.Item>
                      <ProDescriptions.Item label="访问地址" copyable>
                        {curServerInfo.serverHost
                          ? curServerInfo.serverHost + ':' + item.openPort
                          : '-'}
                      </ProDescriptions.Item>
                    </ProDescriptions>
                  </ProCard>
                ))}
              </>
            ) : (
              <Empty />
            )}
          </ProCard>
        </ProCard>

        <ProCard
          split="vertical"
          className="content-card"
          gutter={[{ xs: 8, sm: 8, md: 16, lg: 24, xl: 32 }, 16]}
        >
          <ProCard
            title="服务端配置信息"
            headerBordered
            bordered
            className="content-children-card"
          >
            <ProTable
              dataSource={serverConfigList}
              rowKey="id"
              search={false}
              pagination={{
                showQuickJumper: true,
                pageSize: 5,
              }}
              columns={serverColumns}
            />
          </ProCard>
          <ProCard
            title="本地代理配置信息"
            headerBordered
            bordered
            className="content-children-card"
          >
            <ProTable
              dataSource={localProxyConfigList}
              rowKey="id"
              search={false}
              pagination={{
                showQuickJumper: true,
                pageSize: 5,
              }}
              columns={localProxyColumns}
            />
          </ProCard>
        </ProCard>
      </PageContainer>
    </>
  );
};

export default ProxyPage;
