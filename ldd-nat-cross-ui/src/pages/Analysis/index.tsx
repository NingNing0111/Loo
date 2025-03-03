import { serverList } from '@/services/serverInfoController';
import { PageContainer } from '@ant-design/pro-components';
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
  return (
    <>
      <PageContainer loading={loading}>
        {servers.map((item) => {
          return (
            <AnalysisDetail
              serverName={item.serverName ?? ''}
              key={item.serverName}
            />
          );
        })}
      </PageContainer>
    </>
  );
};

export default PageAnalysis;
