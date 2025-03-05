// @ts-ignore
/* eslint-disable */
import { request } from 'umi';

/** 此处后端没有提供注释 GET /client/list */
export async function serverClientList(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.serverClientListParams,
  options?: { [key: string]: any },
) {
  return request<API.BaseResponseListServerClientDO>(`/api/client/list`, {
    method: 'GET',
    params: {
      ...params,
    },
    ...(options || {}),
  });
}
