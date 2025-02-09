export interface ServerConfig {
  id?: number;
  serverHost: string;
  serverPort: number;
  password: string;
  createTime?: number;
}

export interface LocalProxyConfig {
  id?: number;
  host: string;
  port: number;
  protocol: string;
  openPort: number;
  createTime?: number;
}

export interface CommandResult {
  code: number;
  msg: string;
  err: string;
  data: any;
}

export interface HomeCntInfo {
  serverCnt: number;
  proxyCnt: number;
  successedCnt: number;
  failedCnt: number;
}

export interface BasePageParam {
  page: number;
  pageSize: number;
}

export const DEFAULT_PAGE_PARAM: BasePageParam = {
  page: 1,
  pageSize: 5,
};

export interface SettingInfo {
  theme: 'dark' | 'light'; // 主题
  language: string; // 语言
  compact: boolean; // 紧凑模式
}
