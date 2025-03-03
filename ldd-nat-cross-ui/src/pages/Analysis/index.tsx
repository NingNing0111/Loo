import { serverList } from '@/services/serverInfoController';
import { PageContainer } from '@ant-design/pro-components';
import { Button, Empty } from 'antd';
import { useEffect, useState } from 'react';
import AnalysisDetail from './Detail';

const PageAnalysis = () => {
  const [servers, setServers] = useState<API.ServerInfoVO[]>([]);
  const [loading, setLoading] = useState<boolean>(false);

  const loadServers = async () => {
    setLoading(true);
    try {
      const res = await serverList();
      if (res) {
        setServers(res.filter((item: API.ServerInfoVO) => item.isLive));
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

  return (
    <>
      <PageContainer
        loading={loading}
        extra={
          servers.length === 0 && (
            <Button
              type="primary"
              onClick={() => {
                loadServers();
              }}
            >
              刷新
            </Button>
          )
        }
      >
        {servers.length === 0 ? (
          <Empty description="暂无在线的服务" />
        ) : (
          servers.map((item) => {
            return (
              <AnalysisDetail
                serverName={item.serverName ?? ''}
                key={item.serverName}
              />
            );
          })
        )}
      </PageContainer>
    </>
  );
};

export default PageAnalysis;
