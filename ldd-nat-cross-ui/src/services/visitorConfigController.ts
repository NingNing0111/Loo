// @ts-ignore
/* eslint-disable */
import { request } from 'umi';

/** 此处后端没有提供注释 POST /visitor/add */
export async function addVisitorConfig(
  body: API.VisitorConfigVO,
  options?: { [key: string]: any },
) {
  return request<API.BaseResponseLong>(`/api/visitor/add`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 POST /visitor/delete */
export async function deleteVisitorConfig(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.deleteVisitorConfigParams,
  options?: { [key: string]: any },
) {
  return request<API.BaseResponseLong>(`/api/visitor/delete`, {
    method: 'POST',
    params: {
      ...params,
    },
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 GET /visitor/list */
export async function visitorConfigList(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.visitorConfigListParams,
  options?: { [key: string]: any },
) {
  return request<API.BaseResponsePageVisitorConfigVO>(`/api/visitor/list`, {
    method: 'GET',
    params: {
      ...params,
      arg1: undefined,
      ...params['arg1'],
    },
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 POST /visitor/update */
export async function updateVisitorConfig(
  body: API.VisitorConfigVO,
  options?: { [key: string]: any },
) {
  return request<API.BaseResponseLong>(`/api/visitor/update`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}
