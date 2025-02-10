import {
  addProxyConfig,
  addServerConfig,
  delProxyConfig,
  delServerConfig,
  pageProxyConfig,
  pageServerConfig,
  ping,
  updateProxyConfig,
  updateServerConfig,
} from '@/command/config';
import AddConfigForm from '@/components/AddConfigForm';
import EditConfigForm from '@/components/EditConfigForm';
import OperationConfirm from '@/components/OperationConfirm';
import {
  BasePageParam,
  DEFAULT_PAGE_PARAM,
  LocalProxyConfig,
  ServerConfig,
} from '@/models/types';
import { formatTimestamp } from '@/utils/time';
import {
  DownloadOutlined,
  PlusCircleFilled,
  ReloadOutlined,
} from '@ant-design/icons';
import {
  PageContainer,
  ProCard,
  ProColumns,
  ProFormUploadDragger,
  ProTable,
} from '@ant-design/pro-components';
import { Button, message, Space, Tag } from 'antd';
import { useEffect, useState } from 'react';

const ConfigPage: React.FC = () => {
  const [messageApi, contextHolder] = message.useMessage();
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

  const delServer = async (id: number | undefined) => {
    let res = await delServerConfig(id);
    if (res.code === 0) {
      messageApi.open({
        type: 'success',
        content: res.msg,
      });
      await loadServerConfig();
    }
  };

  const delProxy = async (id: number | undefined) => {
    let res = await delProxyConfig(id);
    if (res.code === 0) {
      messageApi.open({
        type: 'success',
        content: res.msg,
      });
      await loadProxyConfig();
    }
  };

  const toPing = async (host: string, port: number, protocol: string) => {
    let res = await ping(host, port, protocol === 'udp' ? 'udp' : 'tcp');
    if (res.code === 0) {
      messageApi.success({
        content: res.msg,
      });
    } else {
      messageApi.error({
        content: res.err,
      });
    }
  };

  const editServer = async (serverConfig: ServerConfig) => {
    let res = await updateServerConfig(serverConfig);
    if (res.code === 0) {
      messageApi.open({
        type: 'success',
        content: res.msg,
      });
      await loadServerConfig();
    }
  };

  const editProxy = async (proxyConfig: LocalProxyConfig) => {
    let res = await updateProxyConfig(proxyConfig);
    if (res.code === 0) {
      messageApi.open({
        type: 'success',
        content: res.msg,
      });
      await loadProxyConfig();
    }
    console.log(proxyConfig);
  };

  const serverConfigColumns: ProColumns<ServerConfig>[] = [
    {
      title: '序号',
      dataIndex: 'index',
      valueType: 'index',
      width: 80,
    },
    {
      title: '服务主机名',
      dataIndex: 'serverHost',
      valueType: 'text',
      copyable: true,
    },
    {
      title: '接入端口',
      dataIndex: 'serverPort',
      render: (data) => <Tag color="success">{JSON.stringify(data)}</Tag>,
    },
    {
      title: '接入密码',
      dataIndex: 'password',
      valueType: 'password',
      copyable: true,
    },
    {
      title: '创建时间',
      dataIndex: 'createTime',
      render: (data: any) => <span>{formatTimestamp(data * 1000)}</span>,
    },
    {
      title: '操作',
      valueType: 'option',
      align: 'center',
      render: (_, record) => [
        <EditConfigForm
          key="editServer"
          type="server"
          btnName="编辑"
          title="编辑服务配置"
          keyHost="serverHost"
          keyPort="serverPort"
          onFinish={async (formData: ServerConfig) =>
            await editServer({ ...formData, id: record.id })
          }
          initialValues={record}
        />,
        <OperationConfirm
          key="delServer"
          title="删除警告"
          description="你确定要删除这条配置信息吗？"
          btnName="删除"
          size="small"
          type="primary"
          danger
          onConfirm={async () => await delServer(record.id)}
        />,
        <Button
          key="testServer"
          variant="solid"
          color="green"
          size="small"
          onClick={async () =>
            await toPing(record.serverHost, record.serverPort, 'tcp')
          }
        >
          测试
        </Button>,
      ],
      width: 100,
    },
  ];

  const proxyConfigColumns: ProColumns<LocalProxyConfig>[] = [
    {
      title: '序号',
      dataIndex: 'index',
      valueType: 'index',
      width: 80,
    },
    {
      title: '代理主机名',
      dataIndex: 'host',
      valueType: 'text',
      copyable: true,
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
    {
      title: '创建时间',
      dataIndex: 'createTime',
      render: (data: any) => <span>{formatTimestamp(data * 1000)}</span>,
    },
    {
      title: '操作',
      valueType: 'option',
      align: 'center',
      render: (_, record) => [
        <EditConfigForm
          key="editProxy"
          type="proxy"
          btnName="编辑"
          title="编辑代理配置"
          keyHost="host"
          keyPort="port"
          initialValues={record}
          onFinish={async (formData: LocalProxyConfig) =>
            await editProxy({ ...formData, id: record.id })
          }
        />,
        <OperationConfirm
          key="delProxy"
          title="删除警告"
          description="你确定要删除这条配置信息吗？"
          btnName="删除"
          size="small"
          type="primary"
          danger
          onConfirm={async () => await delProxy(record.id)}
        />,
        <Button
          key="testProxy"
          variant="solid"
          color="green"
          size="small"
          onClick={async () =>
            await toPing(record.host, record.port, record.protocol)
          }
        >
          测试
        </Button>,
      ],
      width: 100,
    },
  ];

  const onFinishServerConfig = async (values: ServerConfig) => {
    let res = await addServerConfig(values);
    if (res.code === 0) {
      messageApi.open({
        type: 'success',
        content: res.msg,
      });
      await loadServerConfig();
    } else {
      messageApi.open({
        type: 'error',
        content: res.err,
      });
    }
  };

  const onFinishProxyConfig = async (values: any) => {
    let res = await addProxyConfig(values);
    if (res.code === 0) {
      messageApi.open({
        type: 'success',
        content: res.msg,
      });
      await loadProxyConfig();
    } else {
      messageApi.open({
        type: 'error',
        content: res.err,
      });
    }
  };

  useEffect(() => {
    loadServerConfig();
    loadProxyConfig();
  }, []);

  return (
    <>
      {contextHolder}
      <PageContainer ghost>
        <ProCard
          title="配置文件导入"
          bordered
          headerBordered
          tooltip="请参考配置文件模板"
          extra={
            <Button type="primary" shape="round" icon={<DownloadOutlined />}>
              模板
            </Button>
          }
        >
          <ProFormUploadDragger
            max={4}
            name="dragger"
            description="仅支持单次上传"
          />
        </ProCard>

        <ProCard
          title="服务端配置"
          bordered
          headerBordered
          extra={
            <Space>
              <Button
                icon={<ReloadOutlined />}
                color="orange"
                onClick={async () => {
                  await loadServerConfig();
                }}
              >
                刷新
              </Button>
              <AddConfigForm
                type="server"
                title="服务端配置"
                btnName="服务端配置"
                btnIcon={<PlusCircleFilled />}
                onFinish={onFinishServerConfig}
              />
            </Space>
          }
        >
          <ProTable
            columns={serverConfigColumns}
            dataSource={serverConfigList}
            search={false}
            options={false}
            loading={isServerLoading}
            pagination={{
              pageSize: serverPageParam.pageSize,
              current: serverPageParam.page,
              align: 'center',
              onChange: (page, pageSize) => {
                serverPageParam.page = page;
                serverPageParam.pageSize = pageSize;
                setServerPageParam(serverPageParam);
                loadServerConfig();
              },
              total: serverTotal,
            }}
          />
        </ProCard>
        <ProCard
          title="本地代理配置"
          bordered
          headerBordered
          extra={
            <Space>
              <Button
                icon={<ReloadOutlined />}
                color="orange"
                onClick={async () => {
                  await loadProxyConfig();
                }}
              >
                刷新
              </Button>
              <AddConfigForm
                type="proxy"
                title="代理配置"
                btnIcon={<PlusCircleFilled />}
                btnName="代理配置"
                onFinish={onFinishProxyConfig}
                initProxy={[
                  {
                    host: 'localhost',
                    port: 3306,
                    protocol: 'tcp',
                    openPort: 9011,
                  },
                ]}
              />
            </Space>
          }
        >
          <ProTable
            columns={proxyConfigColumns}
            dataSource={proxyConfigList}
            search={false}
            options={false}
            loading={isProxyLoading}
            pagination={{
              pageSize: proxyPageParam.pageSize,
              current: proxyPageParam.page,
              align: 'center',
              onChange: (page, pageSize) => {
                proxyPageParam.page = page;
                proxyPageParam.pageSize = pageSize;
                setProxyPageParam(proxyPageParam);
                loadProxyConfig();
              },
              total: proxyTotal,
            }}
          />
        </ProCard>
      </PageContainer>
    </>
  );
};

export default ConfigPage;
