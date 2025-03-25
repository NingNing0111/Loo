declare namespace API {
  type AnalysisDataVO = {
    jvmMaxMemory?: number;
    jvmTotalMemory?: number;
    jvmUsableMemory?: number;
    jvmFreeMemory?: number;
    cpuUsage?: number;
    systemLoad?: number;
    cpuCores?: number;
    threadCount?: number;
    gcCount?: number;
    gcTime?: number;
    diskTotal?: number;
    diskFree?: number;
    registerTime?: string;
  };

  type analysisParams = {
    serverName: string;
    timeType: string;
  };

  type AuthVO = {
    username?: string;
    password?: string;
  };

  type BaseResponseAnalysisDataVO = {
    code?: number;
    data?: AnalysisDataVO;
    message?: string;
  };

  type BaseResponseListAnalysisDataVO = {
    code?: number;
    data?: AnalysisDataVO[];
    message?: string;
  };

  type BaseResponseListServerClientDO = {
    code?: number;
    data?: ServerClientDO[];
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

  type BaseResponseListSimpleServerVO = {
    code?: number;
    data?: SimpleServerVO[];
    message?: string;
  };

  type BaseResponseLoginUserVO = {
    code?: number;
    data?: LoginUserVO;
    message?: string;
  };

  type BaseResponseLong = {
    code?: number;
    data?: number;
    message?: string;
  };

  type BaseResponsePageServerInfoVO = {
    code?: number;
    data?: PageServerInfoVO;
    message?: string;
  };

  type BaseResponsePageUserVO = {
    code?: number;
    data?: PageUserVO;
    message?: string;
  };

  type BaseResponsePageVisitorConfigVO = {
    code?: number;
    data?: PageVisitorConfigVO;
    message?: string;
  };

  type BaseResponseString = {
    code?: number;
    data?: string;
    message?: string;
  };

  type deleteVisitorConfigParams = {
    serverName: string;
  };

  type historyListParams = {
    arg0: ServerInfoVO;
  };

  type lastSystemDataParams = {
    serverName: string;
  };

  type listParams = {
    arg0: UserVO;
  };

  type LoginUserVO = {
    id?: number;
    username?: string;
    role?: string;
    token?: string;
  };

  type offlineClientParams = {
    clientId: number;
  };

  type OrderItem = {
    column?: string;
    asc?: boolean;
  };

  type PageBaseVO = {
    page?: number;
    pageSize?: number;
  };

  type PageServerInfoVO = {
    records?: ServerInfoVO[];
    total?: number;
    size?: number;
    current?: number;
    orders?: OrderItem[];
    optimizeCountSql?: PageServerInfoVO;
    searchCount?: PageServerInfoVO;
    optimizeJoinOfCountSql?: boolean;
    maxLimit?: number;
    countId?: string;
    pages?: number;
  };

  type PageUserVO = {
    records?: UserVO[];
    total?: number;
    size?: number;
    current?: number;
    orders?: OrderItem[];
    optimizeCountSql?: PageUserVO;
    searchCount?: PageUserVO;
    optimizeJoinOfCountSql?: boolean;
    maxLimit?: number;
    countId?: string;
    pages?: number;
  };

  type PageVisitorConfigVO = {
    records?: VisitorConfigVO[];
    total?: number;
    size?: number;
    current?: number;
    orders?: OrderItem[];
    optimizeCountSql?: PageVisitorConfigVO;
    searchCount?: PageVisitorConfigVO;
    optimizeJoinOfCountSql?: boolean;
    maxLimit?: number;
    countId?: string;
    pages?: number;
  };

  type ServerClientDO = {
    createTime?: string;
    updateTime?: string;
    deleted?: boolean;
    id?: number;
    serverId?: string;
    clientHost?: string;
    clientPort?: number;
    licenseKey?: string;
    isLive?: boolean;
  };

  type serverClientListParams = {
    serverId: string;
  };

  type ServerInfoVO = {
    page?: number;
    pageSize?: number;
    id?: string;
    serverName?: string;
    osName?: string;
    osArch?: string;
    osVersion?: string;
    serverHost?: string;
    serverPort?: number;
    registerTime?: string;
    liveClientCnt?: number;
    isLive?: boolean;
  };

  type ServerSystemInfoDO = {
    createTime?: string;
    updateTime?: string;
    deleted?: boolean;
    id?: string;
    serverId?: string;
    jvmMaxMemory?: number;
    jvmTotalMemory?: number;
    jvmUsableMemory?: number;
    jvmFreeMemory?: number;
    cpuUsage?: number;
    systemLoad?: number;
    cpuCores?: number;
    threadCount?: number;
    gcCount?: number;
    gcTime?: number;
    diskTotal?: number;
    diskFree?: number;
    registerTime?: string;
  };

  type serverSystemInfoListParams = {
    arg0: ServerSystemReqVO;
  };

  type ServerSystemReqVO = {
    serverId?: string;
  };

  type SimpleServerVO = {
    serverName?: string;
    label?: string;
    value?: string;
  };

  type UserVO = {
    page?: number;
    pageSize?: number;
    id?: number;
    username?: string;
    password?: string;
    role?: string;
  };

  type visitorConfigListParams = {
    serverName: string;
    arg1: PageBaseVO;
  };

  type VisitorConfigVO = {
    type?: number;
    serverName?: string;
    blackList?: string[];
    whiteList?: string[];
  };
}
