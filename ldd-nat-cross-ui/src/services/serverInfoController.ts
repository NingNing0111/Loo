// @ts-ignore
/* eslint-disable */
import { request } from 'umi';

/** 此处后端没有提供注释 GET /server/list */
export async function list1(options?: { [key: string]: any }) {
  return request<API.BaseResponseListServerInfoVO>(`/api/server/list`, {
    method: 'GET',
    ...(options || {}),
  });
}
