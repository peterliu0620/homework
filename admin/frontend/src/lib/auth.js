const TOKEN_KEY = 'admin_token';
const PROFILE_KEY = 'admin_profile';

export function getAuthToken() {
  return localStorage.getItem(TOKEN_KEY) || '';
}

export function hasAuthToken() {
  return Boolean(getAuthToken());
}

export function getAdminProfile() {
  const raw = localStorage.getItem(PROFILE_KEY);

  if (!raw) {
    return null;
  }

  try {
    return JSON.parse(raw);
  } catch (error) {
    clearAuth();
    return null;
  }
}

export function setAuthSession(payload) {
  localStorage.setItem(TOKEN_KEY, payload.token);
  localStorage.setItem(
    PROFILE_KEY,
    JSON.stringify({
      id: payload.id,
      username: payload.username,
      nickname: payload.nickname
    })
  );
}

export function clearAuth() {
  localStorage.removeItem(TOKEN_KEY);
  localStorage.removeItem(PROFILE_KEY);
}
