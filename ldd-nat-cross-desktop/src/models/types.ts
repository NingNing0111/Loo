export interface ServerConfig {
  id: string;
  host: string;
  port: number;
  password: string;
  createTime: string | null;
}

export interface LocalProxyConfig {
  id: string;
  host: string;
  port: number;
  protocol: string;
  openPort: number;
  createTime: string | null;
}

export interface CommandResult {
  code: number;
  msg: string;
  err: string;
}
