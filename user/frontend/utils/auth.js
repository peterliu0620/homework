export const USER_ROLE_VISION = 'vision';
export const USER_ROLE_FAMILY = 'family';

export const AUTH_STORAGE_KEY = 'auth_user';

export const ROLE_OPTIONS = [
	{
		label: '视障人士',
		value: USER_ROLE_VISION,
		desc: '登录后进入单次智能视觉识别页面。'
	},
	{
		label: '家人',
		value: USER_ROLE_FAMILY,
		desc: '登录后查看识别记录、风险提醒并登记照护信息。'
	}
];

function normalizeRole(role) {
	return role === USER_ROLE_FAMILY ? USER_ROLE_FAMILY : USER_ROLE_VISION;
}

function normalizeUser(user) {
	if (!user || typeof user !== 'object') {
		return null;
	}
	return {
		...user,
		role: normalizeRole(user.role)
	};
}

export function isFamilyRole(role) {
	return normalizeRole(role) === USER_ROLE_FAMILY;
}

export function isVisionRole(role) {
	return normalizeRole(role) === USER_ROLE_VISION;
}

export function getRoleLabel(role) {
	return isFamilyRole(role) ? '家人' : '视障人士';
}

export function getDefaultRouteByRole(role) {
	return isFamilyRole(role) ? '/pages/family-center/family-center' : '/pages/index/index';
}

export function getAuthUser() {
	const stored = uni.getStorageSync(AUTH_STORAGE_KEY);
	return normalizeUser(stored) || {};
}

export function saveAuthUser(user) {
	const normalized = normalizeUser(user) || {};
	uni.setStorageSync(AUTH_STORAGE_KEY, normalized);
	return normalized;
}

export function clearAuthUser() {
	uni.removeStorageSync(AUTH_STORAGE_KEY);
}
