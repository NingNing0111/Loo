// 运行时配置
import { history } from '@umijs/max';
import { message } from 'antd';
import type { RequestConfig } from 'umi';
import { userInfo } from './services/authController';
// 全局初始化数据配置，用于 Layout 用户信息和权限初始化
// 更多信息见文档：https://umijs.org/docs/api/runtime-config#getinitialstate
export async function getInitialState(): Promise<API.LoginUserVO> {
  const res = await userInfo();
  if (res) {
    return res;
  } else {
    history.push('/login');
  }
  return {};
}

export const layout = () => {
  return {
    logo: 'https://img.alicdn.com/tfs/TB1YHEpwUT1gK0jSZFhXXaAtVXa-28-27.svg',
    menu: {
      locale: false,
    },
  };
};

// 请求拦截
// 与后端约定的响应数据格式
interface ResponseStructure {
  code: number;
  data: any;
  message: string;
}
export const request: RequestConfig = {
  // other axios options you want
  errorConfig: {
    errorHandler() {},
    errorThrower() {},
  },
  requestInterceptors: [
    (url: any, options: any) => {
      const jwt = localStorage.getItem('jwt');
      if (jwt) {
        options.headers.Authorization = `Bearer ${jwt}`;
      }
      return { url, options };
    },
  ],
  responseInterceptors: [
    (response: any) => {
      let resp: ResponseStructure = response.data;
      if (resp.code !== 0) {
        message.error(resp.message);
      }
      return resp;
    },
  ],
};
