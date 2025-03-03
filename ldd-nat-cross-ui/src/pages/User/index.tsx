import OperationConfirm from '@/components/OperationConfirm';
import { deleteUsingPost, list } from '@/services/userController';
import { PlusOutlined } from '@ant-design/icons';
import {
  PageContainer,
  ProCard,
  ProColumns,
  ProTable,
} from '@ant-design/pro-components';
import { Button, message, Tag } from 'antd';
import { useEffect, useState } from 'react';

interface PageParam {
  page: number;
  pageSize: number;
  total: number;
}

const PageUser = () => {
  const [messageApi, contextHolder] = message.useMessage();
  const [pageParam, setPageParam] = useState<PageParam>({
    page: 1,
    pageSize: 10,
    total: 0,
  });
  const [loading, setLoading] = useState<boolean>(false);
  const [userList, setUserList] = useState<API.UserVO[]>([]);

  const loadUserList = async (param: API.UserVO) => {
    setLoading(true);
    let res = await list({ ...param } as any);
    setUserList(res.records as API.UserVO[]);
    setPageParam({
      ...pageParam,
      total: res.total,
    });
    setLoading(false);
  };

  const delUser = async (userVO: API.UserVO) => {
    setLoading(true);
    let res = await deleteUsingPost(userVO);
    if (res.code === 0) {
      messageApi.success('删除成功');
      loadUserList({});
    } else {
      messageApi.error('删除失败');
    }
    setLoading(false);
  };

  const userListColumns: ProColumns<API.UserVO>[] = [
    {
      title: '序号',
      dataIndex: 'index',
      valueType: 'index',
    },
    {
      title: '用户名',
      dataIndex: 'username',
      valueType: 'text',
    },
    {
      title: '角色',
      dataIndex: 'role',
      render: (text) => <Tag color="success">{text}</Tag>,
    },
    {
      title: '操作',
      valueType: 'option',
      align: 'center',
      width: 200,
      render: (_, record) => [
        <OperationConfirm
          key="delServer"
          title="删除警告"
          description="你确定要删除这条用户信息吗？"
          btnName="删除"
          size="small"
          type="primary"
          danger
          onConfirm={async () => await delUser(record)}
        />,
      ],
    },
  ];

  useEffect(() => {
    loadUserList({});
  }, []);

  return (
    <>
      {contextHolder}{' '}
      <PageContainer ghost title="用户管理">
        <ProCard
          headerBordered
          bordered
          extra={
            <Button type="primary" icon={<PlusOutlined />}>
              添加用户
            </Button>
          }
        >
          <ProTable
            loading={loading}
            pagination={{
              pageSize: pageParam.pageSize,
              current: pageParam.page,
              total: pageParam.total,
              align: 'center',
            }}
            dataSource={userList}
            columns={userListColumns}
            onSubmit={(param) => {
              loadUserList(param as API.UserVO);
            }}
          ></ProTable>
        </ProCard>
      </PageContainer>
    </>
  );
};

export default PageUser;
