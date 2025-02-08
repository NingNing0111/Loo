import { invoke } from '@tauri-apps/api/core';

export interface ConnectLog {
  server_addr: string;
  proxy_addr: string;
  visitor_addr: string | null;
  status: number;
  connected_time: number;
  disconnected_time: number;
}

export interface ConnectLogDO {
  id: number;
  server_addr: string;
  proxy_addr: string;
  visitor_addr: string | null;
  status: number; // 0: 失败 1: 成功
  connected_time: number;
  disconnected_time: number;
  created_time: number;
}

export const newConnectLog = async (log: ConnectLog) => {
  return await invoke('add_connect_log', { log });
};

export const pageLogs = async (page: number, pageSize: number) => {
  return await invoke('page_connect_log', { page, pageSize });
};

export const updateConnectLog = async (log: ConnectLogDO) => {
  return await invoke('update_connect_log', { log });
};
