<template>
	<div class="dashboard-page">
		<section class="hero-panel">
			<div>
				<div class="panel-kicker">TODAY'S CONTROL ROOM</div>
				<h2>{{ greetingTitle }}</h2>
				<p>
					当前后台已简化为单管理员模式，保留 `admin` 账号统一登录，后续可以继续接入商品、订单、内容等业务模块。
				</p>
				<div class="toolbar-insights">
					<div class="insight-pill">
						<span class="insight-dot active"></span>
						统一管理入口
					</div>
					<div class="insight-pill">
						<span class="insight-dot warm"></span>
						JWT 鉴权
					</div>
					<div class="insight-pill">
						<span class="insight-dot muted"></span>
						默认管理员初始化
					</div>
				</div>
			</div>
			<div class="hero-stats">
				<div class="stat-card highlight">
					<div class="stat-label">管理员账号</div>
					<div class="stat-value">{{ loading ? '--' : stats.adminName }}</div>
				</div>
				<div class="stat-card">
					<div class="stat-label">默认密码</div>
					<div class="stat-value">{{ loading ? '--' : stats.defaultPassword }}</div>
				</div>
			</div>
		</section>

		<section class="grid-panels">
			<a-card :bordered="false" class="surface-card">
				<template #title>当前模式说明</template>
				<a-skeleton active :loading="loading" :paragraph="{ rows: 4 }">
					<div class="recent-list">
						<div
							v-for="item in highlights"
							:key="item.title"
							class="recent-item"
						>
							<div>
								<div class="recent-name">{{ item.title }}</div>
								<div class="recent-meta">{{ item.description }}</div>
							</div>
							<a-tag color="blue">
								{{ item.tag }}
							</a-tag>
						</div>
					</div>
				</a-skeleton>
			</a-card>

			<a-card :bordered="false" class="surface-card">
				<template #title>当前管理员</template>
				<div class="profile-summary">
					<div class="profile-plate">
						<div class="profile-plate-name">{{ profile?.nickname || '管理员' }}</div>
						<div class="profile-plate-meta">{{ profile?.username || 'admin' }}</div>
					</div>
					<div class="summary-row">
						<span>当前可用模块</span>
						<strong>概览、管理员登录</strong>
					</div>
					<div class="summary-row">
						<span>后端接口前缀</span>
						<strong>/api/admin/*</strong>
					</div>
					<div class="summary-row">
						<span>鉴权方式</span>
						<strong>JWT Bearer Token</strong>
					</div>
				</div>
			</a-card>
		</section>

		<section class="metrics-grid">
			<div class="metric-panel accent">
				<div class="metric-label">当前身份</div>
				<div class="metric-value">{{ profile?.nickname || '管理员' }}</div>
				<div class="metric-meta">统一使用单管理员模型，适合课程作业快速演示</div>
			</div>
			<div class="metric-panel">
				<div class="metric-label">后台端口</div>
				<div class="metric-value">8081</div>
				<div class="metric-meta">管理端接口统一通过 `/api/admin/*` 暴露</div>
			</div>
			<div class="metric-panel">
				<div class="metric-label">下一步建议</div>
				<div class="metric-value">用户管理</div>
				<div class="metric-meta">现在可以继续扩展订单、内容、商品等后台模块</div>
			</div>
		</section>
	</div>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue';
import { getAdminProfile } from '@/lib/auth';

const loading = ref(false);
const profile = getAdminProfile();
const stats = reactive({
	adminName: 'admin',
	defaultPassword: '123456'
});
const highlights = [
	{
		title: '单管理员账号',
		description: '后台只保留一个 admin，不再区分 AdminUser 和 SysUser 两套模型。',
		tag: '简化'
	},
	{
		title: '登录更直接',
		description: '服务启动时会自动确保默认管理员存在，输入账号密码即可登录。',
		tag: '默认初始化'
	},
	{
		title: '便于后续扩展',
		description: '当前先聚焦后台入口，后续再按需要追加具体业务模块和菜单。',
		tag: '可扩展'
	}
];

const greetingTitle = computed(() => `你好，${profile?.nickname || '管理员'}。`);

onMounted(() => {
	loadDashboard();
});

async function loadDashboard() {
	loading.value = true;
	stats.adminName = profile?.username || 'admin';
	stats.defaultPassword = '123456';
	loading.value = false;
}
</script>
