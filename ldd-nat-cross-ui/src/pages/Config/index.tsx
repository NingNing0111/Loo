import AddConfigBox from '@/components/AddConfigBox';
import OperationConfirm from '@/components/OperationConfirm';
import { simpleList } from '@/services/serverInfoController';
import {
  addVisitorConfig,
  deleteVisitorConfig,
  visitorConfigList,
} from '@/services/visitorConfigController';
import { PlusCircleOutlined, RedoOutlined } from '@ant-design/icons';
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

const PageConfig = () => {
  const [messageApi, contextHolder] = message.useMessage();
  const [configList, setConfigList] = useState<API.VisitorConfigVO[]>([]);
  const [loading, setLoading] = useState(false);
  const [pageParam, setPageParam] = useState<PageParam>({
    page: 1,
    pageSize: 10,
    total: 0,
  });
  const [serverOptions, setServerOptions] = useState<
    { label: string; value: string }[]
  >([]);

  const loadConfigList = async (serverName: string) => {
    setLoading(true);
    try {
      let res = await visitorConfigList({ ...pageParam, serverName } as any);

      if (res) {
        setConfigList(res.records);
      }
    } catch (e) {
      messageApi.error('' + e);
    } finally {
      setLoading(false);
    }
  };

  const loadServerOptions = async () => {
    let res = await simpleList();
    setServerOptions(res);
  };

  const addConfig = async (formData: {
    serverName: string;
    blackListStr: string;
    whiteListStr: string;
  }) => {
    let blackList: string[] = [];
    let whiteList: string[] = [];
    if (formData.blackListStr) {
      blackList = formData.blackListStr.split(',');
    }
    if (formData.whiteListStr) {
      whiteList = formData.whiteListStr.split(',');
    }
    let data: API.VisitorConfigVO = {
      ...formData,
      blackList,
      whiteList,
    };
    try {
      let res = await addVisitorConfig(data);
      if (res) {
        messageApi.success('添加成功');
      }
    } catch (e) {
      messageApi.error('' + e);
    } finally {
      loadConfigList('');
    }
  };

  const deleteConfig = async (data: API.VisitorConfigVO) => {
    try {
      let res = await deleteVisitorConfig({
        serverName: data.serverName ?? '',
      });
      if (res) {
        messageApi.success('删除成功');
      }
    } catch (e) {
      messageApi.error('' + e);
    } finally {
      loadConfigList('');
    }
  };

  const configColumns: ProColumns<API.VisitorConfigVO>[] = [
    {
      title: '序号',
      dataIndex: 'index',
      valueType: 'index',
    },
    {
      title: '服务名称',
      dataIndex: 'serverName',
      valueType: 'text',
    },
    {
      title: '黑名单',
      dataIndex: 'blackList',
      render: (_, record) => {
        return (
          <>
            {record.blackList && record.blackList.length > 0 ? (
              record.blackList.map((item, index) => (
                <Tag key={index} color="warning">
                  {item}
                </Tag>
              ))
            ) : (
              <span>暂无数据</span>
            )}
          </>
        );
      },
    },
    {
      title: '白名单',
      dataIndex: 'whiteList',
      render: (_, record) => {
        return (
          <>
            {record.whiteList && record.whiteList.length > 0 ? (
              record.whiteList.map((item, index) => (
                <Tag key={index} color="success">
                  {item}
                </Tag>
              ))
            ) : (
              <span>暂无数据</span>
            )}
          </>
        );
      },
    },
    {
      title: '操作',
      valueType: 'option',
      align: 'center',
      render: (_, record) => [
        <OperationConfirm
          key="delConfig"
          title="删除配置"
          description="你确定要将该配置删除吗？"
          btnName="删除"
          size="small"
          type="primary"
          danger
          onConfirm={async () => {
            await deleteConfig(record);
          }}
        />,
        <Button key="editConfig" type="primary" size="small">
          编辑
        </Button>,
      ],
    },
  ];

  useEffect(() => {
    loadConfigList('');
  }, []);

  return (
    <>
      {contextHolder}
      <PageContainer title="" ghost>
        <ProCard
          extra={
            <>
              <Button
                style={{ marginRight: 10 }}
                icon={<RedoOutlined />}
                onClick={async () => {
                  await loadConfigList('');
                }}
              >
                刷新
              </Button>
              <AddConfigBox
                title="新增配置"
                btnName="新增"
                onFinish={addConfig}
                trigger={loadServerOptions}
                serverOptions={serverOptions}
                btnIcon={<PlusCircleOutlined />}
              />
            </>
          }
          bordered
          headerBordered
        >
          <ProTable
            loading={loading}
            dataSource={configList}
            columns={configColumns}
            search={false}
            options={false}
            pagination={{
              pageSize: pageParam.pageSize,
              current: pageParam.page,
              total: pageParam.total,
              align: 'center',
              onChange: (page, pageSize) => {
                setPageParam({ ...pageParam, page, pageSize });
              },
            }}
            onSubmit={(param) => {
              loadConfigList(param.serverName);
            }}
          ></ProTable>
        </ProCard>
      </PageContainer>
    </>
  );
};

export default PageConfig;
