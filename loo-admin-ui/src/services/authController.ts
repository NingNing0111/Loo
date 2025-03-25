// @ts-ignore
/* eslint-disable */
import { request } from 'umi';

/** 此处后端没有提供注释 POST /auth */
export async function auth(body: API.AuthVO, options?: { [key: string]: any }) {
  return request<API.BaseResponseLoginUserVO>(`/api/auth`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 GET /userInfo */
export async function userInfo(options?: { [key: string]: any }) {
  return request<API.BaseResponseLoginUserVO>(`/api/userInfo`, {
    method: 'GET',
    ...(options || {}),
  });
}
