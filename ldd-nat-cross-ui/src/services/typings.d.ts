declare namespace API {
  type AuthVO = {
    username?: string;
    password?: string;
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

  type listParams = {
    arg0: ServerSystemReqVO;
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

  type ServerSystemReqVO = {
    serverId?: string;
  };
}
