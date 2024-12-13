// @ts-ignore
/* eslint-disable */
import { request } from 'umi';

/** 此处后端没有提供注释 GET /serverSystem/list */
export async function serverSystemInfoList(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.serverSystemInfoListParams,
  options?: { [key: string]: any },
) {
  return request<API.BaseResponseListServerSystemInfoDO>(
    `/api/serverSystem/list`,
    {
      method: 'GET',
      params: {
        ...params,
        arg0: undefined,
        ...params['arg0'],
      },
      ...(options || {}),
    },
  );
}
