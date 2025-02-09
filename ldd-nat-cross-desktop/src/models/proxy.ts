import { useState } from 'react';
import { LocalProxyConfig, ServerConfig } from './types';

export const EMPTY_SERVER_INFO: ServerConfig = {
  serverHost: '-',
  serverPort: -1,
  password: '',
};

const useProxy = () => {
  const [isStart, setIsStart] = useState<boolean>(false);
  const [runSeconds, setRunSeconds] = useState<number>(0);
  const [serverConfig, setServerConfig] =
    useState<ServerConfig>(EMPTY_SERVER_INFO);
  const [proxies, setProxies] = useState<LocalProxyConfig[]>([]);

  return {
    isStart,
    setIsStart,
    runSeconds,
    setRunSeconds,
    serverConfig,
    setServerConfig,
    proxies,
    setProxies,
  };
};

export default useProxy;
