import { useState } from 'react';
import { SettingInfo } from './types';

const useSetting = () => {
  const [settingInfo, setSettingInfo] = useState<SettingInfo>();
  return {
    settingInfo,
    setSettingInfo,
  };
};

export default useSetting;
