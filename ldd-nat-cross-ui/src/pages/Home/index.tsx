import { serverList } from '@/services/serverInfoController';
import { ProjectOutlined, RedoOutlined } from '@ant-design/icons';
import {
  PageContainer,
  ProCard,
  ProDescriptions,
} from '@ant-design/pro-components';
import { Button } from 'antd';
import { useEffect, useState } from 'react';
import './index.less';

const HomePage: React.FC = () => {
  const [servers, setServers] = useState<API.ServerInfoVO[]>([]);
  const [loading, setLoading] = useState<boolean>(false);

  const loadServers = async () => {
    setLoading(true);
    try {
      const res = await serverList();
      if (res) {
        setServers(res);
      }
    } catch (e) {
      setServers([]);
    } finally {
      setLoading(false);
    }
  };
  useEffect(() => {
    loadServers();
  }, []);

  const serverDetail = () => {
    console.log('detail...');
  };

  return (
    <PageContainer
      ghost
      className="container"
      title="服务在线情况"
      extra={[
        <Button
          type="primary"
          key="refresh"
          onClick={loadServers}
          icon={<RedoOutlined />}
        >
          刷新
        </Button>,
      ]}
      loading={loading}
    >
      {servers.map((item) => (
        <ProCard
          className="card"
          key={item.id}
          bordered
          extra={
            <Button icon={<ProjectOutlined />} onClick={serverDetail}>
              查看详情
            </Button>
          }
        >
          <ProDescriptions column={3}>
            <ProDescriptions.Item label="服务名称">
              {item.serverName}
            </ProDescriptions.Item>
            <ProDescriptions.Item
              label="注册时间"
              fieldProps={{
                format: 'YYYY-MM-DD HH:mm:ss',
              }}
              valueType="dateTime"
            >
              {item.registerTime}
            </ProDescriptions.Item>
            <ProDescriptions.Item
              label="在线状态"
              valueEnum={{
                true: {
                  text: '在线',
                  status: 'Success',
                },
                false: {
                  text: '离线',
                  status: 'Error',
                },
              }}
            >
              {item.isLive}
            </ProDescriptions.Item>
            <ProDescriptions.Item label="接入IP" copyable>
              {item.ip}
            </ProDescriptions.Item>
            <ProDescriptions.Item label="端口">
              {item.port}
            </ProDescriptions.Item>
            <ProDescriptions.Item label="接入主机" copyable>
              {item.hostname}
            </ProDescriptions.Item>
            <ProDescriptions.Item label="操作系统名称">
              {item.osName}
            </ProDescriptions.Item>
            <ProDescriptions.Item label="操作系统版本">
              {item.osVersion}
            </ProDescriptions.Item>
            <ProDescriptions.Item label="操作系统架构">
              {item.osArch}
            </ProDescriptions.Item>
          </ProDescriptions>
        </ProCard>
      ))}
    </PageContainer>
  );
};

export default HomePage;
