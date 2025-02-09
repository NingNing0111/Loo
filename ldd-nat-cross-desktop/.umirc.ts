import { defineConfig } from '@umijs/max';

export default defineConfig({
  access: {},
  model: {},
  initialState: {},
  request: {},
  layout: {},
  antd: {
    configProvider: {},
    compact: false,
  },
  routes: [
    {
      path: '/',
      redirect: '/home',
    },
    {
      name: '首页',
      path: '/home',
      component: './Home',
      icon: 'HomeOutlined',
    },
    {
      name: '内网穿透',
      path: '/proxy',
      component: './Proxy',
      icon: 'GlobalOutlined',
    },
    {
      name: '配置管理',
      path: '/config',
      component: './Config',
      icon: 'SettingOutlined',
    },
  ],
  npmClient: 'npm',
  vite: {
    clearScreen: false,
    server: {
      strictPort: true,
      port: 8000,
    },
    envPrefix: ['VITE_', 'TAURI_ENV_*'],
  },
});
