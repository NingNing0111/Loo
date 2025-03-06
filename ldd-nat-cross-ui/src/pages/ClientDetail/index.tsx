import OperationConfirm from '@/components/OperationConfirm';
import {
  offlineClient,
  serverClientList,
} from '@/services/serverClientController';
import { RedoOutlined } from '@ant-design/icons';
import {
  PageContainer,
  ProCard,
  ProColumns,
  ProTable,
} from '@ant-design/pro-components';
import { useNavigate, useParams } from '@umijs/max';
import { Button, message } from 'antd';
import { useEffect, useState } from 'react';

const PageClientDetail = () => {
  const param = useParams();
  const navigator = useNavigate();
  const [messageApi, contextHolder] = message.useMessage();
  const [loading, setLoading] = useState<boolean>(false);
  const [clientList, setClientList] = useState<API.ServerClientDO[]>([]);

  const loadClientList = async () => {
    setLoading(true);
    try {
      const res = await serverClientList({ ...param } as any);
      if (res) {
        setClientList(res);
      } else {
        setClientList([]);
      }
      console.log(res);
    } catch (e) {
      setClientList([]);
      messageApi.error('获取客户端列表失败');
      console.log(e);
    } finally {
      setLoading(false);
    }
  };

  const offline = async (data: API.ServerClientDO) => {
    let res = await offlineClient({ clientId: data.id } as any);
    if (res) {
      messageApi.success('下线成功!');
    } else {
      messageApi.error(res.message);
    }
    loadClientList();
  };

  const clientListColumns: ProColumns<API.ServerClientDO>[] = [
    {
      title: '序号',
      dataIndex: 'index',
      valueType: 'index',
    },
    {
      title: '主机名',
      dataIndex: 'clientHost',
      valueType: 'text',
    },

    {
      title: '端口',
      dataIndex: 'clientPort',
      valueType: 'text',
    },
    {
      title: '授权码',
      dataIndex: 'licenseKey',
      valueType: 'password',
    },
    {
      title: '注册时间',
      dataIndex: 'createTime',
      valueType: 'dateTime',
    },
    {
      title: '操作',
      valueType: 'option',
      align: 'center',
      render: (_, record) => [
        <OperationConfirm
          key="delServer"
          title="下线警告"
          description="你确定要将该客户端强制下线吗？"
          btnName="强制下线"
          size="small"
          type="primary"
          danger
          onConfirm={async () => await offline(record)}
        />,
      ],
    },
  ];

  useEffect(() => {
    loadClientList();
  }, []);

  return (
    <>
      {contextHolder}
      <PageContainer
        loading={loading}
        title="客户端详情"
        extra={<Button onClick={() => navigator(-1)}> 返回 </Button>}
      >
        <ProCard
          title="在线列表"
          bordered
          headerBordered
          extra={
            <Button
              type="primary"
              icon={<RedoOutlined />}
              onClick={() => loadClientList()}
            >
              刷新
            </Button>
          }
        >
          <ProTable
            showHeader
            options={false}
            dataSource={clientList}
            search={false}
            columns={clientListColumns}
          ></ProTable>
        </ProCard>
      </PageContainer>
    </>
  );
};

export default PageClientDetail;
