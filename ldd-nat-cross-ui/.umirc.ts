import { defineConfig } from '@umijs/max';

export default defineConfig({
  antd: {
    // dark: true,
  },
  access: {},
  model: {},
  initialState: {},
  request: {
    dataField: 'data',
  },
  layout: {
    title: 'Loo-admin',
    locale: false,
    contentWidth: 'Fixed',
  },
  routes: [
    {
      path: '/login',
      component: './Login',
      name: '登录',
      hideInMenu: true,
    },
    {
      path: '/',
      redirect: '/home',
    },
    {
      name: '服务列表',
      path: '/home',
      component: './Home',
      access: 'canAccess',
    },
    {
      name: '服务详情',
      path: '/server/:serverName',
      component: './Server',
      hideInMenu: true,
      access: 'canAccess',
    },
    {
      name: '用户管理',
      path: '/user',
      component: './User',
      access: 'canAccess',
    },
    {
      name: '服务监控',
      path: '/serverAnalysis',
      component: './Analysis',
      access: 'canAccess',
    },
    {
      name: '系统设置',
      path: '/setting',
      component: './Config',
      access: 'canAccess',
    },
  ],
  npmClient: 'pnpm',
  proxy: {
    '/api': {
      target: 'http://localhost:7989',
      changeOrigin: true,
      secure: false,
    },
  },
  presets: ['umi-presets-pro'],
  openAPI: [
    {
      requestLibPath: "import { request } from 'umi'",
      schemaPath: 'http://loo.mnzdna.xyz/api/v3/api-docs/default', // openapi 接口地址
      mock: false,
      apiPrefix() {
        return "'/api'";
      },
    },
  ],
});
