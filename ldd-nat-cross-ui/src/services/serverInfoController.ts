// @ts-ignore
/* eslint-disable */
import { request } from 'umi';

/** 此处后端没有提供注释 GET /server/historyList */
export async function historyList(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.historyListParams,
  options?: { [key: string]: any },
) {
  return request<API.BaseResponse>(`/api/server/historyList`, {
    method: 'GET',
    params: {
      ...params,
      arg0: undefined,
      ...params['arg0'],
    },
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 GET /server/list */
export async function serverList(options?: { [key: string]: any }) {
  return request<API.BaseResponseListServerInfoVO>(`/api/server/list`, {
    method: 'GET',
    ...(options || {}),
  });
}
