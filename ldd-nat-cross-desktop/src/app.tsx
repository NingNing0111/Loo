// 运行时配置

import { GithubFilled, QuestionCircleTwoTone } from '@ant-design/icons';
import { RunTimeLayoutConfig } from '@umijs/max';
import { Space } from 'antd';
import { lastAppConfig } from './command/client';
import { getSettingInfo, updateSettingInfo } from './command/setting';
import OutlinkButton from './components/OutlinkButton/OutlinkButton';
import SettingForm from './components/SettingForm';
import { GITHUB_REPORISTORY } from './constants';
import { EMPTY_SERVER_INFO } from './models/proxy';
import { LocalProxyConfig, ServerConfig, SettingInfo } from './models/types';

// 全局初始化数据配置，用于 Layout 用户信息和权限初始化
// 更多信息见文档：https://umijs.org/docs/api/runtime-config#getinitialstate
export async function getInitialState(): Promise<{
  setting: SettingInfo;
  lastServerConfig: ServerConfig;
  lastProxies: LocalProxyConfig[];
}> {
  let lastServerConfig: ServerConfig = EMPTY_SERVER_INFO;
  let lastProxies: LocalProxyConfig[] = [];
  let settingInfo: SettingInfo = {
    theme: 'dark',
    language: 'zh',
    compact: false,
  };

  let settingInfoRes = await getSettingInfo();
  let lastAppConfigRes = await lastAppConfig();

  if (lastAppConfigRes.code === 0) {
    if (lastAppConfigRes.data && lastAppConfigRes.data.server) {
      lastServerConfig = lastAppConfigRes.data.server;
    }
    if (lastAppConfigRes.data && lastAppConfigRes.data.proxies) {
      lastProxies = lastAppConfigRes.data.proxies;
    }
  }
  if (settingInfoRes.code === 0) {
    if (settingInfoRes.data) {
      settingInfo = settingInfoRes.data;
    }
  }

  return { setting: settingInfo, lastServerConfig, lastProxies };
}

export const layout: RunTimeLayoutConfig = (initial) => {
  const { initialState, setInitialState } = initial;
  const updateSetting = async (formData: SettingInfo) => {
    await updateSettingInfo(formData);
    if (
      initialState &&
      initialState.setting.compact &&
      initialState.setting.compact !== formData.compact
    ) {
      window.location.reload();
    }
    if (initialState?.setting) {
      initialState.setting = { ...formData };
      setInitialState(initialState);
    }
  };

  return {
    title: 'Loo',
    logo: 'https://img.alicdn.com/tfs/TB1YHEpwUT1gK0jSZFhXXaAtVXa-28-27.svg',
    menu: {
      locale: false,
      type: 'group',
      collapsedShowTitle: true,
    },
    fixSiderbar: true,
    collapsed: false,
    collapsedButtonRender: false,
    siderWidth: 200,
    actionsRender: () => {
      return [
        <Space key="bottom" align="center">
          <SettingForm
            title="应用设置"
            onFinish={updateSetting}
            initialValues={initialState?.setting}
            operationList={[
              {
                title: '清理日志',
                description: '清理指定时间内的日志',
                action: (row: any) => {
                  console.log(row);
                },
                btnName: '清理',
              },
            ]}
          />
          <OutlinkButton
            href={`${GITHUB_REPORISTORY}/issues`}
            icon={<QuestionCircleTwoTone />}
          />
          <OutlinkButton href={GITHUB_REPORISTORY} icon={<GithubFilled />} />
        </Space>,
      ];
    },
  };
};
