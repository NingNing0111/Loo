import {
  BasePageParam,
  CommandResult,
  LocalProxyConfig,
  ServerConfig,
} from '@/models/types';
import { invoke } from '@tauri-apps/api/core';

/**
 * 添加服务端配置信息
 * @param serverConfig 服务端配置
 * @returns
 */
export const addServerConfig = async (
  serverConfig: ServerConfig,
): Promise<CommandResult> => {
  return await invoke('add_server_config', {
    serverConfig,
  });
};

/**
 * 分页查询服务端配置信息
 * @param page 第几页
 * @param pageSize 每页多少条记录
 * @returns
 */
export const pageServerConfig = async (
  pageParam: BasePageParam,
): Promise<CommandResult> => {
  return await invoke('page_server_config', { ...pageParam });
};

/**
 * 批量添加代理配置信息
 * @param proxies 代理配置信息
 * @returns
 */
export const addProxyConfig = async (
  proxies: Array<LocalProxyConfig>,
): Promise<CommandResult> => {
  return await invoke('add_proxy_config_batch', {
    data: proxies,
  });
};

/**
 * 分页查询代理配置信息
 * @param pageParam 分页参数
 * @returns
 */
export const pageProxyConfig = async (
  pageParam: BasePageParam,
): Promise<CommandResult> => {
  return await invoke('page_proxy_config', { ...pageParam });
};

/**
 * 删除服务端配置信息
 * @param id 服务端配置id
 * @returns
 */
export const delServerConfig = async (
  id: number | undefined,
): Promise<CommandResult> => {
  return await invoke('del_server_config', { id });
};

/**
 * 删除代理配置信息
 * @param id 代理配置id
 * @returns
 */
export const delProxyConfig = async (
  id: number | undefined,
): Promise<CommandResult> => {
  return await invoke('del_proxy_config', { id });
};

/**
 * 网络连接状态 ping命令
 * @param host
 * @param port
 * @param protocol
 * @returns
 */
export const ping = async (
  host: string,
  port: number,
  protocol: 'tcp' | 'udp',
): Promise<CommandResult> => {
  return await invoke('ping', { host, port, protocol });
};

export const updateServerConfig = async (
  serverConfig: ServerConfig,
): Promise<CommandResult> => {
  return await invoke('update_server_config', { serverConfig });
};

export const updateProxyConfig = async (
  proxyConfig: LocalProxyConfig,
): Promise<CommandResult> => {
  return await invoke('update_proxy_config', { proxyConfig });
};
