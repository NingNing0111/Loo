import { CommandResult } from '@/models/types';
import { invoke } from '@tauri-apps/api/core';

export const getHomeInfo = async (): Promise<CommandResult> => {
  return await invoke('count_info', {});
};
