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

/** 此处后端没有提供注释 POST /client/offline/${param0} */
export async function offlineClient(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.offlineClientParams,
  options?: { [key: string]: any },
) {
  const { clientId: param0, ...queryParams } = params;
  return request<API.BaseResponseString>(`/api/client/offline/${param0}`, {
    method: 'POST',
    params: { ...queryParams },
    ...(options || {}),
  });
}
