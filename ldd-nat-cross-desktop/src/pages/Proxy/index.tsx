import { ClientConfig, startApp, stopApp } from '@/command/client';
import { ReactComponent as SvgNetworkConnected } from '@/icons/proxy/NetworkConnected.svg';
import { ReactComponent as SvgNetworkDisconnect } from '@/icons/proxy/NetworkDisconnect.svg';
import { ReactComponent as SvgNetworkTest } from '@/icons/proxy/NetworkTest.svg';

import { pageProxyConfig, pageServerConfig } from '@/command/config';
import SelectConfigForm from '@/components/SelectConfigForm';
import { EMPTY_SERVER_INFO } from '@/models/proxy';
import {
  BasePageParam,
  CommandResult,
  DEFAULT_PAGE_PARAM,
  LocalProxyConfig,
  ServerConfig,
} from '@/models/types';
import { formatSeconds2HMS } from '@/utils/time';
import { EyeInvisibleOutlined, EyeOutlined } from '@ant-design/icons';
import {
  PageContainer,
  ProCard,
  ProColumns,
  ProDescriptions,
  ProTable,
} from '@ant-design/pro-components';
import { listen } from '@tauri-apps/api/event';
import { useModel } from '@umijs/max';
import { Button, Empty, Flex, message, Switch, Tag } from 'antd';
import { useEffect, useState } from 'react';
import './index.less';

const serverColumns: ProColumns<ServerConfig>[] = [
  {
    title: '序号',
    dataIndex: 'index',
    valueType: 'index',
  },
  {
    title: '主机名',
    dataIndex: 'serverHost',
  },
  {
    title: '端口',
    dataIndex: 'serverPort',
    render: (data) => <Tag color="success">{JSON.stringify(data)}</Tag>,
  },
  {
    title: '接入密码',
    dataIndex: 'password',
    valueType: 'password',
  },
];

const localProxyColumns: ProColumns<LocalProxyConfig>[] = [
  {
    title: '序号',
    dataIndex: 'index',
    valueType: 'index',
  },
  {
    title: '代理主机',
    dataIndex: 'host',
  },
  {
    title: '代理端口',
    dataIndex: 'port',
    render: (data) => <Tag color="success">{JSON.stringify(data)}</Tag>,
  },
  {
    title: '映射端口',
    dataIndex: 'openPort',
    render: (data) => <Tag color="warning">{JSON.stringify(data)}</Tag>,
  },
  {
    title: '代理协议',
    dataIndex: 'protocol',
    valueEnum: {
      tcp: <Tag color="success">TCP</Tag>,
      udp: <Tag color="warning">UDP</Tag>,
    },
  },
];
let timer: ReturnType<typeof setInterval> | null = null;

const ProxyPage: React.FC = () => {
  const [messageApi, contextHolder] = message.useMessage();
  const {
    isStart,
    setIsStart,
    runSeconds,
    setRunSeconds,
    serverConfig,
    setServerConfig,
    proxies,
    setProxies,
  } = useModel('proxy');

  const [visible, setVisible] = useState(false);

  const [serverConfigList, setServerConfigList] = useState<ServerConfig[]>([]);
  const [proxyConfigList, setProxyConfigList] = useState<LocalProxyConfig[]>(
    [],
  );
  const [serverPageParam, setServerPageParam] =
    useState<BasePageParam>(DEFAULT_PAGE_PARAM);
  const [proxyPageParam, setProxyPageParam] =
    useState<BasePageParam>(DEFAULT_PAGE_PARAM);

  const [serverTotal, setServerTotal] = useState(0);
  const [proxyTotal, setProxyTotal] = useState(0);
  const [isServerLoading, setServerLoading] = useState(false);
  const [isProxyLoading, setProxyLoading] = useState(false);

  const loadServerConfig = async () => {
    setServerLoading(true);
    let res = await pageServerConfig(serverPageParam);
    if (res.code === 0) {
      let pageRes = res.data;
      setServerConfigList(pageRes.records);
      setServerTotal(pageRes.total);
    }
    setServerLoading(false);
  };

  const loadProxyConfig = async () => {
    setProxyLoading(true);
    let res = await pageProxyConfig(proxyPageParam);
    if (res.code === 0) {
      let pageRes = res.data;
      setProxyConfigList(pageRes.records);
      setProxyTotal(pageRes.total);
    }
    setProxyLoading(false);
  };

  const toggleVisible = () => {
    setVisible((prev) => !prev);
  };

  const toggleTimer = () => {
    timer = setInterval(() => {
      setRunSeconds((prevSeconds) => {
        return prevSeconds + 1;
      });
    }, 1000);
  };

  const unToggleTimer = () => {
    if (timer !== null) {
      clearInterval(timer);
      setRunSeconds(0);
    }
  };

  useEffect(() => {
    loadServerConfig();
    loadProxyConfig();
    listen<CommandResult>('app_err_handler', (event) => {
      if (event.payload.code !== 0) {
        messageApi.error({
          content: event.payload.err,
        });
        setIsStart(false);
        unToggleTimer();
      }
    });
  }, []);

  const onSelectServerConfig = async (value: ServerConfig[]) => {
    console.log(value);
    if (value.length === 1) {
      setServerConfig(value[0]);
      // 修改全局
    } else {
      setServerConfig(EMPTY_SERVER_INFO);
    }
  };

  const onSelectProxyConfig = async (value: LocalProxyConfig[]) => {
    setProxies(value);
  };

  const start = async () => {
    let app_config: ClientConfig = {
      ...serverConfig,
      proxies,
    };
    // let status = 0;
    let res = await startApp(app_config);
    if (res.code === 0) {
      setIsStart(true);
    }
  };

  const stop = async () => {
    let res = await stopApp();
    if (res.code === 0) {
      messageApi.open({
        type: 'success',
        content: res.msg,
      });
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
      // 开始计时
      toggleTimer();
    } else {
      unToggleTimer();
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
                <Button
                  icon={<SvgNetworkTest />}
                  color="primary"
                  variant="filled"
                  size="small"
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
              <SelectConfigForm
                label="接入服务"
                placeholder="请选择一个接入服务"
                maxCount={1}
                data={serverConfigList}
                keyHost="serverHost"
                keyPort="serverPort"
                triggerName="切换服务"
                onFinish={onSelectServerConfig}
                initialValue={serverConfig.id ? [serverConfig.id] : []}
              />
            }
          >
            <ProDescriptions column={1}>
              <ProDescriptions.Item label="主机名">
                {serverConfig.id ? serverConfig.serverHost : '-'}
              </ProDescriptions.Item>
              <ProDescriptions.Item label="接入端口">
                {serverConfig.id ? serverConfig.serverPort : '-'}
              </ProDescriptions.Item>
              <ProDescriptions.Item label="连接密码">
                {serverConfig.id ? (
                  <span>
                    {visible && serverConfig.id
                      ? serverConfig.password
                      : '******'}
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
                  </span>
                ) : (
                  '-'
                )}
              </ProDescriptions.Item>
              <>
                {isStart ? (
                  <ProDescriptions.Item label="运行时长" valueType="text">
                    <Tag color="success">{formatSeconds2HMS(runSeconds)}</Tag>
                  </ProDescriptions.Item>
                ) : (
                  <ProDescriptions.Item label="运行时长">
                    <Tag color="warning">未连接</Tag>
                  </ProDescriptions.Item>
                )}
              </>
            </ProDescriptions>
          </ProCard>
          <ProCard
            title="配置详情"
            headerBordered
            extra={
              <SelectConfigForm
                label="代理配置"
                placeholder="请选择代理配置"
                data={proxyConfigList}
                keyHost="host"
                keyPort="port"
                triggerName="选择代理配置"
                onFinish={onSelectProxyConfig}
                initialValue={proxies.map((item) => item.id)}
              />
            }
          >
            {proxies.length > 0 ? (
              <>
                {proxies.map((item) => (
                  <ProCard
                    collapsible
                    defaultCollapsed
                    bordered
                    headerBordered
                    title={item.label}
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
                        {serverConfig.serverHost
                          ? serverConfig.serverHost + ':' + item.openPort
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
              loading={isServerLoading}
              dataSource={serverConfigList}
              rowKey="id"
              search={false}
              pagination={{
                total: serverTotal,
                pageSize: serverPageParam.pageSize,
                current: serverPageParam.page,
                align: 'center',
                onChange: async (page, pageSize) => {
                  serverPageParam.page = page;
                  serverPageParam.pageSize = pageSize;
                  setServerPageParam(serverPageParam);
                  await loadServerConfig();
                },
              }}
              columns={serverColumns}
              toolBarRender={false}
            />
          </ProCard>
          <ProCard
            title="本地代理配置信息"
            headerBordered
            bordered
            className="content-children-card"
          >
            <ProTable
              toolBarRender={false}
              dataSource={proxyConfigList}
              rowKey="id"
              loading={isProxyLoading}
              search={false}
              pagination={{
                pageSize: proxyPageParam.pageSize,
                current: proxyPageParam.page,
                total: proxyTotal,
                onChange: async (page, pageSize) => {
                  proxyPageParam.page = page;
                  proxyPageParam.pageSize = pageSize;
                  setProxyPageParam(proxyPageParam);
                  await loadProxyConfig();
                },
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
