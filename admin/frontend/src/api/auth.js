import http from './http';

export function login(payload) {
  return http.post('/admin/auth/login', payload);
}
