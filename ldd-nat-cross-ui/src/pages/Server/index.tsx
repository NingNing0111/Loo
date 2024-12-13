import { useParams } from '@umijs/max';

const ServerInfoHistory = () => {
  const param = useParams();

  return <h1>{param.serverName}</h1>;
};

export default ServerInfoHistory;
