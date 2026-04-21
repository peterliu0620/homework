<template>
	<a-layout class="admin-shell">
		<div class="shell-glow shell-glow-primary"></div>
		<div class="shell-glow shell-glow-secondary"></div>

		<a-layout-sider
			v-model:collapsed="collapsed"
			:width="272"
			class="admin-sider"
			breakpoint="lg"
			collapsible
		>
			<div class="brand-block">
				<div class="brand-mark">VG</div>
				<div v-if="!collapsed" class="brand-copy">
					<div class="brand-title">视障守护</div>
					<div class="brand-subtitle">视障守护管理台</div>
				</div>
			</div>

			<div v-if="!collapsed" class="sider-panel">
				<div class="panel-kicker">当前页面</div>
				<div class="panel-title">{{ pageTitle }}</div>
				<div class="panel-meta">集中处理当前页面的管理任务与数据操作。</div>
			</div>

			<div v-if="!collapsed" class="nav-section-label">总览</div>
			<a-menu :selected-keys="selectedKeys" theme="light" mode="inline" class="nav-menu">
				<a-menu-item key="/dashboard" @click="router.push('/dashboard')">
					<template #icon>
						<dashboard-outlined />
					</template>
					概览
				</a-menu-item>
			</a-menu>

			<div v-if="!collapsed" class="nav-section-label">核心模块</div>
			<a-menu :selected-keys="selectedKeys" theme="light" mode="inline" class="nav-menu nav-menu-compact">
				<a-menu-item key="/app-users" @click="router.push('/app-users')">
					<template #icon>
						<user-outlined />
					</template>
					应用用户
				</a-menu-item>
				<a-menu-item key="/family-bindings" @click="router.push('/family-bindings')">
					<template #icon>
						<team-outlined />
					</template>
					家属绑定
				</a-menu-item>
				<a-menu-item key="/shared-logs" @click="router.push('/shared-logs')">
					<template #icon>
						<file-search-outlined />
					</template>
					共享日志
				</a-menu-item>
				<a-menu-item key="/medicine-profiles" @click="router.push('/medicine-profiles')">
					<template #icon>
						<medicine-box-outlined />
					</template>
					药品档案
				</a-menu-item>
			</a-menu>

			<div v-if="!collapsed" class="sider-summary">
				<div class="summary-title">工作台摘要</div>
				<div class="summary-value">{{ pageTitle }}</div>
				<div class="summary-meta">统一处理用户、家属、药品和日志协同，服务视障守护闭环。</div>
			</div>
		</a-layout-sider>

		<a-layout class="admin-main">
			<a-layout-header class="admin-header">
				<div class="header-copy">
					<div class="header-kicker">管理工作台</div>
					<div class="header-title">{{ pageTitle }}</div>
					<div class="header-desc">{{ pageDescription }}</div>
				</div>

				<div class="header-actions">
					<div class="header-chip-list">
						<div class="header-chip">
							<span class="chip-dot chip-dot-live"></span>
							核心管理模块
						</div>
						<div class="header-chip">
							<span class="chip-dot"></span>
							视障守护后台
						</div>
					</div>
					<div class="profile-chip">
						<div class="profile-label">当前账号</div>
						<div class="profile-name">{{ profileName }}</div>
						<div class="profile-role">{{ profile?.username || 'admin' }}</div>
					</div>
					<a-button type="primary" class="logout-btn" @click="handleLogout">退出登录</a-button>
				</div>
			</a-layout-header>

			<a-layout-content class="admin-content">
				<router-view />
			</a-layout-content>
		</a-layout>
	</a-layout>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { DashboardOutlined, FileSearchOutlined, MedicineBoxOutlined, TeamOutlined, UserOutlined } from '@ant-design/icons-vue';
import { clearAuth, getAdminProfile } from '@/lib/auth';

const route = useRoute();
const router = useRouter();
const collapsed = ref(false);
const profile = getAdminProfile();

const selectedKeys = computed(() => [route.path]);

const pageTitle = computed(() => {
	if (route.path.startsWith('/login')) {
		return '管理员登录';
	}

	if (route.path.startsWith('/app-users')) {
		return '应用用户管理';
	}
	if (route.path.startsWith('/family-bindings')) {
		return '家属绑定管理';
	}
	if (route.path.startsWith('/shared-logs')) {
		return '共享日志摘要';
	}
	if (route.path.startsWith('/medicine-profiles')) {
		return '药品档案管理';
	}

	return '系统概览';
});

const pageDescription = computed(() => {
	if (route.path.startsWith('/app-users')) {
		return '维护应用用户账户、状态与基础资料。';
	}
	if (route.path.startsWith('/family-bindings')) {
		return '配置家属与视障用户的协同关系。';
	}
	if (route.path.startsWith('/shared-logs')) {
		return '查看关键生活日志与安全预警摘要。';
	}
	if (route.path.startsWith('/medicine-profiles')) {
		return '预录药品档案，支撑扫描优先播报与强预警。';
	}
	return '围绕视障辅助、家属协同与药品安全进行统一管理。';
});

const profileName = computed(() => profile?.nickname || '管理员');

function handleLogout() {
	clearAuth();
	router.replace('/login');
}
</script>

<style scoped>
.admin-shell {
	position: relative;
	min-height: 100vh;
	background:
		radial-gradient(960px 640px at 0% 0%, rgba(105, 170, 255, 0.22), transparent 60%),
		radial-gradient(720px 520px at 100% 0%, rgba(120, 210, 255, 0.24), transparent 55%),
		radial-gradient(880px 680px at 50% 100%, rgba(212, 228, 255, 0.58), transparent 60%),
		linear-gradient(180deg, #f6f9ff 0%, #edf4ff 42%, #f8fbff 100%);
	overflow: hidden;
}

.shell-glow {
	position: absolute;
	border-radius: 999px;
	filter: blur(80px);
	pointer-events: none;
	opacity: 0.6;
}

.shell-glow-primary {
	top: -100px;
	right: -80px;
	width: 360px;
	height: 360px;
	background: rgba(85, 151, 255, 0.26);
}

.shell-glow-secondary {
	left: -140px;
	bottom: 120px;
	width: 420px;
	height: 420px;
	background: rgba(146, 188, 255, 0.22);
}

.admin-sider {
	position: relative;
	z-index: 1;
	margin: 20px 0 20px 20px;
	padding: 18px 14px;
	border: 1px solid rgba(255, 255, 255, 0.72);
	border-radius: 30px;
	background: linear-gradient(180deg, rgba(255, 255, 255, 0.68), rgba(245, 249, 255, 0.5));
	backdrop-filter: blur(24px) saturate(140%);
	box-shadow: 0 24px 70px rgba(46, 91, 145, 0.14);
}

:deep(.admin-sider .ant-layout-sider-children) {
	display: flex;
	flex-direction: column;
	height: 100%;
}

:deep(.admin-sider .ant-layout-sider-trigger) {
	margin: 10px 8px 0;
	border-radius: 16px;
	background: rgba(255, 255, 255, 0.52);
	color: #255fbd;
}

.brand-block {
	display: flex;
	align-items: center;
	gap: 14px;
	padding: 8px 10px 18px;
}

.brand-mark {
	display: grid;
	place-items: center;
	width: 46px;
	height: 46px;
	border-radius: 16px;
	font-size: 15px;
	font-weight: 700;
	letter-spacing: 0.08em;
	color: #11448d;
	background: linear-gradient(135deg, rgba(255, 255, 255, 0.92), rgba(216, 232, 255, 0.94));
	border: 1px solid rgba(255, 255, 255, 0.8);
	box-shadow: inset 0 1px 0 rgba(255, 255, 255, 0.9), 0 18px 30px rgba(72, 118, 178, 0.16);
}

.brand-title {
	font-family: var(--font-display);
	font-size: 16px;
	font-weight: 680;
	letter-spacing: -0.01em;
	color: #183153;
}

.brand-subtitle {
	margin-top: 4px;
	font-size: 12px;
	letter-spacing: 0.08em;
	text-transform: uppercase;
	font-weight: 560;
	color: rgba(63, 94, 135, 0.76);
}

.sider-panel,
.sider-summary {
	margin: 10px 8px 18px;
	padding: 18px;
	border-radius: 24px;
	background: linear-gradient(180deg, rgba(255, 255, 255, 0.62), rgba(245, 249, 255, 0.44));
	border: 1px solid rgba(255, 255, 255, 0.78);
	box-shadow: inset 0 1px 0 rgba(255, 255, 255, 0.9);
}

.panel-kicker,
.summary-title,
.nav-section-label,
.header-kicker,
.profile-label {
	font-size: 11px;
	font-weight: 600;
	letter-spacing: 0.16em;
	text-transform: uppercase;
	color: rgba(70, 102, 144, 0.74);
}

.panel-title,
.summary-value {
	margin-top: 10px;
	font-family: var(--font-display);
	font-size: 20px;
	font-weight: 690;
	letter-spacing: -0.02em;
	line-height: 1.2;
	color: #143055;
}

.panel-meta,
.summary-meta,
.header-desc,
.profile-role {
	margin-top: 8px;
	font-size: 13px;
	line-height: 1.7;
	font-weight: 450;
	color: rgba(69, 90, 118, 0.82);
}

.nav-section-label {
	margin: 0 14px 10px;
}

.nav-menu {
	margin-bottom: 12px;
	background: transparent;
	border-inline-end: none;
}

:deep(.nav-menu .ant-menu-item) {
	height: 48px;
	margin: 6px 0;
	border-radius: 16px;
	color: rgba(29, 58, 99, 0.88);
	font-size: 14px;
	font-weight: 560;
	transition: background-color 0.22s ease, box-shadow 0.22s ease, transform 0.22s ease;
}

:deep(.nav-menu .ant-menu-item:hover) {
	color: #0f4ea8;
	background: rgba(255, 255, 255, 0.58);
	transform: translateY(-1px);
}

:deep(.nav-menu .ant-menu-item-selected) {
	background: linear-gradient(135deg, rgba(255, 255, 255, 0.78), rgba(222, 236, 255, 0.94));
	color: #0e4ba4;
	box-shadow: inset 0 1px 0 rgba(255, 255, 255, 0.94), 0 12px 24px rgba(112, 156, 217, 0.18);
}

.admin-main {
	position: relative;
	z-index: 1;
	background: transparent;
}

.admin-header {
	display: flex;
	align-items: flex-start;
	justify-content: space-between;
	gap: 18px;
	height: auto;
	margin: 20px 20px 0;
	padding: 24px 28px;
	border: 1px solid rgba(255, 255, 255, 0.76);
	border-radius: 30px;
	background: linear-gradient(160deg, rgba(255, 255, 255, 0.62), rgba(244, 249, 255, 0.48));
	backdrop-filter: blur(22px) saturate(140%);
	box-shadow: 0 24px 60px rgba(46, 91, 145, 0.12);
}

.header-title {
	margin-top: 8px;
	font-family: var(--font-display);
	font-size: 34px;
	font-weight: 720;
	letter-spacing: -0.035em;
	line-height: 1.12;
	color: #183153;
}

.header-actions {
	display: flex;
	align-items: center;
	flex-wrap: wrap;
	justify-content: flex-end;
	gap: 12px;
}

.header-chip-list {
	display: flex;
	flex-wrap: wrap;
	gap: 10px;
}

.header-chip {
	display: inline-flex;
	align-items: center;
	gap: 8px;
	padding: 8px 12px;
	border-radius: 999px;
	background: rgba(255, 255, 255, 0.62);
	border: 1px solid rgba(255, 255, 255, 0.78);
	color: #244264;
	font-size: 12px;
	font-weight: 560;
	box-shadow: inset 0 1px 0 rgba(255, 255, 255, 0.92);
}

.chip-dot {
	width: 8px;
	height: 8px;
	border-radius: 999px;
	background: #4c8dff;
	box-shadow: 0 0 0 4px rgba(76, 141, 255, 0.14);
}

.chip-dot-live {
	background: #2280ff;
	box-shadow: 0 0 0 4px rgba(34, 128, 255, 0.16);
}

.profile-chip {
	min-width: 0;
	padding: 8px 12px;
	border-radius: 14px;
	background: rgba(255, 255, 255, 0.66);
	border: 1px solid rgba(255, 255, 255, 0.8);
	display: flex;
	flex-direction: column;
	gap: 2px;
	box-shadow: inset 0 1px 0 rgba(255, 255, 255, 0.92);
}

.profile-name {
	margin-top: 0;
	font-family: var(--font-display);
	font-size: 13px;
	font-weight: 650;
	color: #173355;
	line-height: 1.2;
}

.profile-label,
.profile-role {
	font-size: 11px;
	line-height: 1.2;
}

.logout-btn {
	height: 40px;
	padding-inline: 18px;
	border-radius: 14px;
	border: 1px solid rgba(255, 255, 255, 0.88);
	background: linear-gradient(135deg, #2a7cff, #1663d6);
	box-shadow: inset 0 1px 0 rgba(255, 255, 255, 0.35), 0 16px 32px rgba(42, 124, 255, 0.22);
}

.admin-content {
	padding: 20px;
	background: transparent;
}

@media (max-width: 992px) {
	.admin-sider {
		margin-right: 0;
	}

	.admin-header {
		flex-direction: column;
	}

	.header-actions {
		width: 100%;
		justify-content: flex-start;
	}
}
</style>
