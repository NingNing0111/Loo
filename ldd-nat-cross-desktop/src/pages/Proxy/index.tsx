import { ClientConfig, startApp, stopApp } from '@/command/client';
import { ReactComponent as SvgNetworkConnected } from '@/icons/proxy/NetworkConnected.svg';
import { ReactComponent as SvgNetworkDisconnect } from '@/icons/proxy/NetworkDisconnect.svg';
import { ReactComponent as SvgNetworkTest } from '@/icons/proxy/NetworkTest.svg';

import { pageProxyConfig, pageServerConfig } from '@/command/config';
import SelectConfigForm from '@/components/SelectConfigForm';
import {
  BasePageParam,
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
import { Button, Empty, Flex, message, Switch, Tag, Tooltip } from 'antd';
import { useEffect, useState } from 'react';
import './index.less';

const EMPTY_SERVER_INFO: ServerConfig = {
  serverHost: '-',
  serverPort: -1,
  password: '',
};

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
  const [isStart, setIsStart] = useState(false);
  const [visible, setVisible] = useState(false);
  const [curServerInfo, setCurServerInfo] =
    useState<ServerConfig>(EMPTY_SERVER_INFO);
  const [curProxyConfigList, setCurLocalProxyConfigList] = useState<
    LocalProxyConfig[]
  >([]);

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
  const [runSeconds, setRunSeconds] = useState(0);

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

  useEffect(() => {
    loadServerConfig();
    loadProxyConfig();
  }, []);

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

  const onSelectServerConfig = async (value: ServerConfig[]) => {
    setCurServerInfo(value[0]);
  };

  const onSelectProxyConfig = async (value: LocalProxyConfig[]) => {
    setCurLocalProxyConfigList(value);
  };

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
      // 开始计时
      toggleTimer();
    } else {
      if (timer !== null) {
        clearInterval(timer);
        setRunSeconds(0);
      }
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
              />
            }
          >
            <ProDescriptions column={1}>
              <ProDescriptions.Item label="主机名">
                {curServerInfo.id ? curServerInfo.serverHost : '-'}
              </ProDescriptions.Item>
              <ProDescriptions.Item label="接入端口">
                {curServerInfo.id ? curServerInfo.serverPort : '-'}
              </ProDescriptions.Item>
              <ProDescriptions.Item label="连接密码">
                {curServerInfo.id ? (
                  <span>
                    {visible && curServerInfo.id
                      ? curServerInfo.password
                      : '******'}
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
                  '-'
                )}
              </ProDescriptions.Item>
              <>
                {isStart ? (
                  <ProDescriptions.Item
                    label="运行时长"
                    valueType="text"
                    contentStyle={{
                      color: isStart ? '#18bd18' : 'black',
                      fontSize: '16px',
                    }}
                  >
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
              />
            }
          >
            {curProxyConfigList.length > 0 ? (
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
