declare namespace API {
  type AuthVO = {
    username?: string;
    password?: string;
  };

  type BaseResponse = {
    code?: number;
    data?: Record<string, any>;
    message?: string;
  };

  type BaseResponseListServerInfoVO = {
    code?: number;
    data?: ServerInfoVO[];
    message?: string;
  };

  type BaseResponseListServerSystemInfoDO = {
    code?: number;
    data?: ServerSystemInfoDO[];
    message?: string;
  };

  type BaseResponseLoginUserVO = {
    code?: number;
    data?: LoginUserVO;
    message?: string;
  };

  type historyListParams = {
    arg0: ServerInfoVO;
    arg1: number;
    arg2: number;
  };

  type LoginUserVO = {
    id?: number;
    username?: string;
    role?: string;
    token?: string;
  };

  type ServerInfoVO = {
    id?: string;
    serverId?: string;
    serverName?: string;
    osName?: string;
    osArch?: string;
    osVersion?: string;
    registerTime?: string;
    ip?: string;
    hostname?: string;
    port?: number;
    isLive?: boolean;
  };

  type ServerSystemInfoDO = {
    id?: string;
    serverId?: string;
    maxMemory?: number;
    totalMemory?: number;
    usableMemory?: number;
    freeMemory?: number;
    registerTime?: string;
  };

  type serverSystemInfoListParams = {
    arg0: ServerSystemReqVO;
  };

  type ServerSystemReqVO = {
    serverId?: string;
  };
}
