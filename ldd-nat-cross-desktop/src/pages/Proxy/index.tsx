import { ReactComponent as SvgNetworkConnected } from '@/icons/proxy/NetworkConnected.svg';
import { ReactComponent as SvgNetworkDisconnect } from '@/icons/proxy/NetworkDisconnect.svg';
import { ReactComponent as SvgNetworkTest } from '@/icons/proxy/NetworkTest.svg';
import { CommandResult, LocalProxyConfig, ServerConfig } from '@/models/types';
import {
  PageContainer,
  ProCard,
  ProDescriptions,
  ProTable,
} from '@ant-design/pro-components';
import { invoke } from '@tauri-apps/api/core';
import { Button, Flex, message, Switch, Tag } from 'antd';
import dayjs from 'dayjs';
import { useState } from 'react';
import './index.less';

const ProxyPage: React.FC = () => {
  const [messageApi, contextHolder] = message.useMessage();

  const [isStart, setIsStart] = useState(false);

  const serverConfigList: ServerConfig[] = [
    {
      id: '1',
      host: 'localhost',
      port: 8864,
      password: '123123',
      createTime: '2021-08-01 12:00:00',
    },
    {
      id: '2',
      host: 'localhost',
      port: 8864,
      password: '123123',
      createTime: '2021-08-01 12:00:00',
    },
    {
      id: '3',
      host: 'localhost',
      port: 8864,
      password: '123123',
      createTime: '2021-08-01 12:00:00',
    },
    {
      id: '4',
      host: 'localhost',
      port: 8864,
      password: '123123',
      createTime: '2021-08-01 12:00:00',
    },
    {
      id: '5',
      host: 'localhost',
      port: 8864,
      password: '123123',
      createTime: '2021-08-01 12:00:00',
    },
    {
      id: '6',
      host: 'localhost',
      port: 8864,
      password: '123123',
      createTime: '2021-08-01 12:00:00',
    },
  ];
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

  const localProxyConfigList: LocalProxyConfig[] = [
    {
      id: '1',
      host: 'localhost',
      port: 3306,
      openPort: 9011,
      protocol: 'tcp',
      createTime: '2021-08-01 12:00:00',
    },
    {
      id: '2',
      host: 'localhost',
      port: 3306,
      openPort: 9011,
      protocol: 'tcp',
      createTime: '2021-08-01 12:00:00',
    },
    {
      id: '3',
      host: 'localhost',
      port: 3306,
      openPort: 9011,
      protocol: 'tcp',
      createTime: '2021-08-01 12:00:00',
    },
    {
      id: '4',
      host: 'localhost',
      port: 3306,
      openPort: 9011,
      protocol: 'tcp',
      createTime: '2021-08-01 12:00:00',
    },
    {
      id: '5',
      host: 'localhost',
      port: 3306,
      openPort: 9011,
      protocol: 'tcp',
      createTime: '2021-08-01 12:00:00',
    },
    {
      id: '6',
      host: 'localhost',
      port: 3306,
      openPort: 9011,
      protocol: 'tcp',
      createTime: '2021-08-01 12:00:00',
    },
    {
      id: '7',
      host: 'localhost',
      port: 3306,
      openPort: 9011,
      protocol: 'tcp',
      createTime: '2021-08-01 12:00:00',
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

  const onApp = async () => {
    let commandResult: CommandResult;
    const config = {
      proxies: [
        {
          host: 'localhost',
          port: 3306,
          openPort: 9011,
          protocol: 'tcp',
        },
      ],
      serverHost: 'localhost',
      serverPort: 8964,
      password: '123456',
    };
    if (!isStart) {
      commandResult = await invoke('start_app', { config });
    } else {
      commandResult = await invoke('stop_app');
    }
    if (commandResult.code === 0) {
      setIsStart(!isStart);
      messageApi.open({
        type: 'success',
        content: commandResult.msg,
      });
    } else {
      setIsStart(false);
      messageApi.open({
        type: 'error',
        content: commandResult.err,
      });
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
              <div>
                <Button
                  icon={<SvgNetworkTest />}
                  color="primary"
                  variant="filled"
                >
                  连接测试
                </Button>
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

              <ProDescriptions.Item label="主机名" copyable>
                localhost
              </ProDescriptions.Item>
              <ProDescriptions.Item label="接入端口" copyable>
                8964
              </ProDescriptions.Item>
            </ProDescriptions>
          </ProCard>
          <ProCard
            title="配置详情"
            headerBordered
            extra={
              <Button color="primary" variant="filled">
                切换配置
              </Button>
            }
          >
            <ProCard
              collapsible
              defaultCollapsed
              bordered
              headerBordered
              title="localhost:3306"
            >
              <ProDescriptions column={1}>
                <ProDescriptions.Item label="穿透协议">
                  <Tag color="success">TCP</Tag>
                </ProDescriptions.Item>
                <ProDescriptions.Item label="代理目标主机名">
                  localhost
                </ProDescriptions.Item>
                <ProDescriptions.Item label="代理目标端口">
                  <Tag color="processing">3306</Tag>
                </ProDescriptions.Item>
                <ProDescriptions.Item label="服务端映射端口">
                  <Tag color="warning">9011</Tag>
                </ProDescriptions.Item>
                <ProDescriptions.Item label="访问地址" copyable>
                  localhost:9011
                </ProDescriptions.Item>
              </ProDescriptions>
            </ProCard>
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
