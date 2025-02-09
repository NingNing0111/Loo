import { CommandResult } from '@/models/types';
import { invoke } from '@tauri-apps/api/core';

export interface ConnectLog {
  id?: number;
  serverId: number;
  proxyIds: number[];
  operation: 0 | 1;
  status: number;
  connectedTime: number;
}

export const addConnectLog = async (log: ConnectLog) => {
  console.log('log======>', log);

  return await invoke('add_connect_log', { log });
};

export const pageLogs = async (
  page: number,
  pageSize: number,
): Promise<CommandResult> => {
  return await invoke('page_connect_log', { page, pageSize });
};
