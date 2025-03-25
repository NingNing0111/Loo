import { CommandResult, LocalProxyConfig } from '@/models/types';
import { invoke } from '@tauri-apps/api/core';

export interface ClientConfig {
  serverHost: string;
  serverPort: number;
  password: string;
  proxies: LocalProxyConfig[];
}

export const startApp = async (
  clientConfig: ClientConfig,
): Promise<CommandResult> => {
  return await invoke('start_app', { config: clientConfig });
};

export const stopApp = async (): Promise<CommandResult> => {
  return await invoke('stop_app');
};

export const lastAppConfig = async (): Promise<CommandResult> => {
  return await invoke('last_config');
};
