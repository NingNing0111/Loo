import { historyList } from '@/services/serverInfoController';
import {
  PageContainer,
  ProCard,
  ProColumns,
  ProTable,
} from '@ant-design/pro-components';
import { useNavigate, useParams } from '@umijs/max';
import { Button, message, Tag } from 'antd';
import { useEffect, useState } from 'react';

interface PageParam {
  page: number;
  pageSize: number;
  total: number;
}

const ServerInfoHistory = () => {
  const navigate = useNavigate();
  const [messageApi, contextHolder] = message.useMessage();
  const param = useParams();
  const [pageParam, setPageParam] = useState<PageParam>({
    page: 1,
    pageSize: 10,
    total: 0,
  });

  const [loading, setLoading] = useState<boolean>(false);
  const [serverInfoList, setServerInfoList] = useState<API.ServerInfoVO[]>([]);
  const serverInfoColumns: ProColumns<API.ServerInfoVO>[] = [
    {
      title: '序号',
      dataIndex: 'index',
      valueType: 'index',
      width: 80,
    },
    {
      title: '主机名',
      dataIndex: 'hostname',
      valueType: 'text',
    },

    {
      title: 'OS 名称',
      dataIndex: 'osName',
      valueType: 'text',
      search: false,
    },
    {
      title: 'OS 架构',
      dataIndex: 'osArch',
      valueType: 'text',
      search: false,
    },
    {
      title: 'OS 版本',
      dataIndex: 'osVersion',
      render: (text) => <Tag color="success">{text}</Tag>,
    },
    {
      title: '注册时间',
      dataIndex: 'registerTime',
      valueType: 'dateTime',
      search: false,
    },
  ];

  const loadHistoryServerData = async () => {
    setLoading(true);
    const res = await historyList({ ...param, ...pageParam } as any);
    setServerInfoList(res.records as API.ServerInfoVO[]);
    setPageParam({ ...pageParam, total: res.total });
    setLoading(false);
  };

  useEffect(() => {
    loadHistoryServerData();
  }, []);

  return (
    <>
      {contextHolder}
      <PageContainer ghost>
        <ProCard
          title={'服务名称：' + param.serverName}
          headerBordered
          bordered
          extra={
            <>
              <Button
                style={{
                  marginRight: 10,
                }}
                color="danger"
                onClick={() => {
                  loadHistoryServerData();
                }}
              >
                刷新
              </Button>

              <Button
                type="primary"
                onClick={() => {
                  navigate(-1);
                }}
              >
                返回
              </Button>
            </>
          }
        >
          <ProTable
            search={false}
            loading={loading}
            pagination={{
              total: pageParam.total,
              pageSize: pageParam.pageSize,
              current: pageParam.page,
              align: 'center',
              onChange: (page, pageSize) => {
                pageParam.page = page;
                pageParam.pageSize = pageSize;
                setPageParam(pageParam);
                loadHistoryServerData();
              },
            }}
            columns={serverInfoColumns}
            dataSource={serverInfoList}
          ></ProTable>
        </ProCard>
      </PageContainer>
    </>
  );
};

export default ServerInfoHistory;
