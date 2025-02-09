import { CommandResult, SettingInfo } from '@/models/types';
import { invoke } from '@tauri-apps/api/core';

export const getSettingInfo = async (): Promise<CommandResult> => {
  return await invoke('get_setting', {});
};

export const updateSettingInfo = async (
  settingInfo: SettingInfo,
): Promise<CommandResult> => {
  return await invoke('update_setting', { settingInfo });
};

export const resetSetting = async (): Promise<CommandResult> => {
  return await invoke('reset_setting', {});
};
