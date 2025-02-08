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
