import axios from 'axios';
import { message } from 'ant-design-vue';
import { clearAuth, getAuthToken } from '@/lib/auth';

let redirected = false;
const envBaseUrl = (import.meta.env.VITE_API_BASE_URL || '').trim();
const baseURL = envBaseUrl ? envBaseUrl.replace(/\/+$/, '') : '/api';

const http = axios.create({
  baseURL,
  timeout: 10000
});

http.interceptors.request.use((config) => {
  const token = getAuthToken();

  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }

  return config;
});

http.interceptors.response.use(
  (response) => {
    const body = response.data;

    if (body && typeof body.success === 'boolean') {
      if (body.success) {
        return body.data;
      }

      return Promise.reject(new Error(body.message || '请求失败'));
    }

    return body;
  },
  (error) => {
    const messageText = error.response?.data?.message || error.message || '网络异常，请稍后重试';

    if (/未登录|过期|失效/.test(messageText)) {
      clearAuth();

      if (!redirected && window.location.pathname !== '/login') {
        redirected = true;
        message.warning('登录状态已失效，请重新登录');
        window.location.href = '/login';
        window.setTimeout(() => {
          redirected = false;
        }, 800);
      }
    }

    return Promise.reject(new Error(messageText));
  }
);

export default http;
