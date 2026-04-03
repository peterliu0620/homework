<template>
	<a-layout class="admin-shell">
		<a-layout-sider
			v-model:collapsed="collapsed"
			:width="244"
			class="admin-sider"
			breakpoint="lg"
			collapsible
		>
			<div class="brand-block">
				<div class="brand-mark">A</div>
				<div v-if="!collapsed">
					<div class="brand-title">Admin Console</div>
					<div class="brand-subtitle">业务管理中心</div>
				</div>
			</div>

			<a-menu
				:selected-keys="selectedKeys"
				theme="dark"
				mode="inline"
				class="nav-menu"
			>
				<a-menu-item key="/dashboard" @click="router.push('/dashboard')">
					<template #icon>
						<dashboard-outlined />
					</template>
					概览
				</a-menu-item>
				<a-menu-item key="/app-users" @click="router.push('/app-users')">
					<template #icon>
						<user-outlined />
					</template>
					App 用户
				</a-menu-item>
			</a-menu>
		</a-layout-sider>

		<a-layout>
			<a-layout-header class="admin-header">
				<div class="header-copy">
					<div class="header-title">{{ pageTitle }}</div>
					<div class="header-desc">让日常管理动作更顺手一点</div>
				</div>

				<div class="header-actions">
					<div class="profile-chip">
						<div class="profile-name">{{ profileName }}</div>
						<div class="profile-role">{{ profile?.username || 'admin' }}</div>
					</div>
					<a-button type="primary" ghost @click="handleLogout">退出登录</a-button>
				</div>
			</a-layout-header>

			<a-layout-content class="admin-content">
				<router-view />
			</a-layout-content>
		</a-layout>
	</a-layout>
</template>

<script setup>
import { computed, ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { DashboardOutlined, UserOutlined } from '@ant-design/icons-vue';
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
		return 'App 用户管理';
	}

	return '系统概览';
});

const profileName = computed(() => profile?.nickname || '管理员');

function handleLogout() {
	clearAuth();
	router.replace('/login');
}
</script>
