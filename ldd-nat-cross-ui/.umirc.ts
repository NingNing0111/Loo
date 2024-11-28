import { defineConfig } from '@umijs/max';

export default defineConfig({
  antd: {
    dark: true,
  },
  access: {},
  model: {},
  initialState: {},
  request: {
    dataField: 'data',
  },
  layout: {
    title: 'ldd-nat-cross-admin',
    locale: false,
    contentWidth: 'Fixed',
  },
  routes: [
    {
      path: '/',
      redirect: '/home',
    },
    {
      name: '系统面板',
      path: '/home',
      component: './Home',
      access: 'canAccess',
    },
    {
      path: '/login',
      component: './Login',
      name: '登录',
      hideInMenu: true,
      // 不展示菜单顶栏
      menuHeaderRender: false,
      // 不展示顶栏
      headerRender: false,
      // 不展示页脚
      footerRender: false,
      // 不展示菜单
      menuRender: false,
    },
  ],
  npmClient: 'pnpm',
  proxy: {
    '/api': {
      target: 'http://localhost:7989',
      changeOrigin: true,
      // pathRewrite: {
      //   '^/api': '',
      // },
    },
  },
  presets: ['umi-presets-pro'],
  openAPI: [
    {
      requestLibPath: "import { request } from 'umi'",
      schemaPath: 'http://localhost:7989/api/v3/api-docs/default', // openapi 接口地址
      mock: false,
      apiPrefix() {
        return "'/api'";
      },
    },
  ],
});
