import http from '@/api/http';

export function fetchAppUsers(keyword) {
  return http.get('/admin/app-users', {
    params: keyword ? { keyword } : {}
  });
}

export function createAppUser(payload) {
  return http.post('/admin/app-users', payload);
}

export function updateAppUser(id, payload) {
  return http.put(`/admin/app-users/${id}`, payload);
}

export function deleteAppUser(id) {
  return http.delete(`/admin/app-users/${id}`);
}
