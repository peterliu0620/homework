<template>
	<div class="dashboard-page">
		<section class="dashboard-hero">
			<div class="hero-copy">
				<div class="panel-kicker">系统概览</div>
				<h2>{{ greetingTitle }}</h2>
				<p>
					在这里统一查看视障守护后台的核心模块，快速进入用户、家属、日志和药品相关管理页面。
				</p>
				<div class="toolbar-insights">
					<div class="insight-pill">
						<span class="insight-dot active"></span>
						用户管理
					</div>
					<div class="insight-pill">
						<span class="insight-dot"></span>
						家属协同
					</div>
					<div class="insight-pill">
						<span class="insight-dot muted"></span>
						药品与日志
					</div>
				</div>
			</div>

			<div class="hero-stage">
				<div class="stage-window">
					<div class="window-topbar">
						<div class="window-dots">
							<span></span>
							<span></span>
							<span></span>
						</div>
						<div class="window-label">管理概览</div>
					</div>
					<div class="window-grid">
						<div class="window-panel window-panel-strong">
							<div class="mini-label">当前管理员</div>
							<div class="mini-value">{{ loading ? '--' : stats.adminName }}</div>
							<div class="mini-meta">当前活跃后台身份</div>
						</div>
						<div class="window-panel">
							<div class="mini-label">模块布局</div>
							<div class="mini-value">4 个模块</div>
							<div class="mini-meta">用户、家属、日志、药品</div>
						</div>
						<div class="window-strip">
							<div class="strip-track">
								<div class="strip-fill"></div>
							</div>
							<div class="strip-meta">
								<span>系统一致性</span>
								<span>86%</span>
							</div>
						</div>
					</div>
				</div>
			</div>
		</section>

		<section class="metrics-grid">
			<div class="metric-panel accent">
				<div class="metric-label">当前身份</div>
				<div class="metric-value">{{ profile?.nickname || '管理员' }}</div>
				<div class="metric-meta">当前登录的后台管理员账号。</div>
			</div>
			<div class="metric-panel">
				<div class="metric-label">后台端口</div>
				<div class="metric-value">8081</div>
				<div class="metric-meta">管理端接口统一经由 `/api/admin/*` 暴露。</div>
			</div>
			<div class="metric-panel">
				<div class="metric-label">平台能力</div>
				<div class="metric-value">用户 + 家属</div>
				<div class="metric-meta">覆盖应用用户维护与家属协同两条核心链路。</div>
			</div>
			<div class="metric-panel">
				<div class="metric-label">安全主线</div>
				<div class="metric-value">药品预警</div>
				<div class="metric-meta">支持档案优先播报、过期预警、重复扫码预警。</div>
			</div>
		</section>

		<section class="dashboard-grid">
			<div class="fluent-panel shortcut-panel">
				<div class="panel-header">
					<div>
						<div class="panel-kicker">快捷入口</div>
						<div class="section-title">工作台捷径</div>
					</div>
					<div class="section-caption">从首页直接进入主要模块</div>
				</div>
				<div class="shortcut-list">
					<button
						v-for="shortcut in shortcuts"
						:key="shortcut.title"
						type="button"
						class="shortcut-item"
						@click="goTo(shortcut.path)"
					>
						<div class="shortcut-copy">
							<div class="shortcut-title">{{ shortcut.title }}</div>
							<div class="shortcut-meta">{{ shortcut.description }}</div>
						</div>
						<div class="shortcut-tag">{{ shortcut.tag }}</div>
					</button>
				</div>
			</div>

			<div class="fluent-panel command-panel">
				<div class="panel-header">
					<div>
						<div class="panel-kicker">核心能力</div>
						<div class="section-title">核心能力总览</div>
					</div>
					<div class="section-caption">概览后台当前的主要管理能力</div>
				</div>
				<a-skeleton active :loading="loading" :paragraph="{ rows: 4 }">
					<div class="command-list">
						<div v-for="item in highlights" :key="item.title" class="command-item">
							<div class="command-index">{{ item.index }}</div>
							<div class="command-copy">
								<div class="command-name">{{ item.title }}</div>
								<div class="command-meta">{{ item.description }}</div>
							</div>
							<div class="command-tag">{{ item.tag }}</div>
						</div>
					</div>
				</a-skeleton>
			</div>
		</section>

		<section class="story-grid">
			<div class="fluent-panel emphasis-panel">
				<div class="panel-header">
					<div>
						<div class="panel-kicker">平台重点</div>
						<div class="section-title">后台管理重点</div>
					</div>
					<div class="section-caption">聚焦视障守护项目的核心管理内容</div>
				</div>
				<div class="focus-stack">
					<div class="focus-item">
						<div class="focus-name">信息更集中</div>
						<div class="focus-meta">首页直接呈现后台核心模块，减少无关信息干扰。</div>
					</div>
					<div class="focus-item">
						<div class="focus-name">入口更明确</div>
						<div class="focus-meta">从首页可快速进入用户、家属、日志和药品管理页面。</div>
					</div>
					<div class="focus-item">
						<div class="focus-name">管理更清晰</div>
						<div class="focus-meta">围绕视障辅助、家属协同与药品安全形成统一后台视图。</div>
					</div>
				</div>
			</div>

			<div class="fluent-panel">
				<div class="panel-header">
					<div>
						<div class="panel-kicker">使用顺序</div>
						<div class="section-title">推荐操作路径</div>
					</div>
					<div class="section-caption">按常用场景查看后台主要模块</div>
				</div>
				<div class="story-list">
					<div class="story-item">
						<div class="story-step">01</div>
						<div class="story-copy">
							<div class="story-name">先看应用用户</div>
							<div class="story-meta">先查看平台用户信息，为绑定和日志留存打底。</div>
						</div>
					</div>
					<div class="story-item">
						<div class="story-step">02</div>
						<div class="story-copy">
							<div class="story-name">再看家属绑定</div>
							<div class="story-meta">查看家属与用户之间的绑定关系，确认协同链路。</div>
						</div>
					</div>
					<div class="story-item">
						<div class="story-step">03</div>
						<div class="story-copy">
							<div class="story-name">最后讲药品和日志</div>
							<div class="story-meta">查看药品档案和共享日志，关注风险提醒与记录留存。</div>
						</div>
					</div>
				</div>
			</div>
		</section>
	</div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue';
import { useRouter } from 'vue-router';
import { getAdminProfile } from '@/lib/auth';

interface DashboardStats {
	adminName: string;
}

interface HighlightItem {
	index: string;
	title: string;
	description: string;
	tag: string;
}

interface ShortcutItem {
	title: string;
	description: string;
	path: string;
	tag: string;
}

const router = useRouter();
const loading = ref(false);
const profile = getAdminProfile();
const stats = reactive<DashboardStats>({
	adminName: 'admin'
});

const highlights: HighlightItem[] = [
	{
		index: '01',
		title: '应用用户统一管理',
		description: '支持增删改查、状态维护与基础资料管理，作为所有协同功能的底座。',
		tag: '用户'
	},
	{
		index: '02',
		title: '家属绑定与日志共享',
		description: '通过后台直接绑定家属与用户，让核心生活记录可以远程查看。',
		tag: '协同'
	},
	{
		index: '03',
		title: '药品档案与风险提醒',
		description: '预录药品档案后，识别链路会优先播报用法用量并触发安全预警。',
		tag: '安全'
	}
];

const shortcuts: ShortcutItem[] = [
	{
		title: '进入应用用户',
		description: '集中维护登录用户与账号状态。',
		path: '/app-users',
		tag: '基础'
	},
	{
		title: '进入 家属绑定',
		description: '为家属建立与视障用户的查看关系。',
		path: '/family-bindings',
		tag: '协同'
	},
	{
		title: '进入 共享日志',
		description: '查看药品识别、寻物、地点与预警摘要。',
		path: '/shared-logs',
		tag: '日志'
	},
	{
		title: '进入 药品档案',
		description: '预录药品信息，支撑命中优先播报。',
		path: '/medicine-profiles',
		tag: '药品'
	}
];

const greetingTitle = computed(() => `你好，${profile?.nickname || '管理员'}。`);

onMounted(() => {
	loadDashboard();
});

async function loadDashboard() {
	loading.value = true;
	stats.adminName = profile?.username || 'admin';
	loading.value = false;
}

function goTo(path: string) {
	router.push(path);
}
</script>

<style scoped>
.dashboard-page {
	display: flex;
	flex-direction: column;
	gap: 20px;
}

.dashboard-hero,
.fluent-panel {
	position: relative;
	overflow: hidden;
	border-radius: 30px;
	border: 1px solid rgba(255, 255, 255, 0.78);
	background: linear-gradient(160deg, rgba(255, 255, 255, 0.62), rgba(243, 248, 255, 0.44));
	backdrop-filter: blur(22px) saturate(145%);
	box-shadow: 0 22px 60px rgba(64, 105, 163, 0.12);
}

.dashboard-hero {
	display: grid;
	grid-template-columns: minmax(0, 1.18fr) minmax(320px, 0.82fr);
	gap: 24px;
	padding: 34px;
}

.dashboard-hero::before,
.fluent-panel::before {
	content: '';
	position: absolute;
	top: -110px;
	right: -40px;
	width: 280px;
	height: 280px;
	border-radius: 999px;
	background: radial-gradient(circle, rgba(102, 163, 255, 0.22), rgba(102, 163, 255, 0));
}

.panel-kicker {
	font-size: 12px;
	font-weight: 700;
	letter-spacing: 0.18em;
	text-transform: uppercase;
	color: rgba(80, 109, 148, 0.76);
}

.hero-copy,
.hero-stage {
	position: relative;
	z-index: 1;
}

.hero-copy h2 {
	margin: 14px 0 0;
	font-family: var(--font-display);
	font-size: 40px;
	font-weight: 720;
	letter-spacing: -0.04em;
	line-height: 1.08;
	color: #173355;
}

.hero-copy p {
	max-width: 720px;
	margin: 16px 0 0;
	font-size: 15px;
	line-height: 1.8;
	font-weight: 450;
	color: rgba(71, 92, 121, 0.9);
}

.toolbar-insights {
	display: flex;
	flex-wrap: wrap;
	gap: 10px;
	margin-top: 24px;
}

.insight-pill {
	display: inline-flex;
	align-items: center;
	gap: 8px;
	padding: 10px 14px;
	border-radius: 999px;
	background: rgba(255, 255, 255, 0.68);
	border: 1px solid rgba(255, 255, 255, 0.8);
	color: #234264;
	font-size: 13px;
	box-shadow: inset 0 1px 0 rgba(255, 255, 255, 0.92);
}

.insight-dot {
	width: 8px;
	height: 8px;
	border-radius: 999px;
	background: #4c8dff;
}

.insight-dot.active {
	background: #1f7cff;
}

.insight-dot.muted {
	background: #94a3b8;
}

.hero-stage {
	display: flex;
	align-items: center;
}

.stage-window {
	width: 100%;
	padding: 16px;
	border-radius: 28px;
	background: linear-gradient(180deg, rgba(255, 255, 255, 0.72), rgba(232, 242, 255, 0.5));
	border: 1px solid rgba(255, 255, 255, 0.82);
	box-shadow: inset 0 1px 0 rgba(255, 255, 255, 0.95), 0 18px 40px rgba(96, 136, 192, 0.16);
}

.window-topbar {
	display: flex;
	align-items: center;
	justify-content: space-between;
	padding: 6px 8px 18px;
	color: #4b668d;
	font-size: 12px;
	font-weight: 600;
}

.window-dots {
	display: flex;
	gap: 8px;
}

.window-dots span {
	width: 8px;
	height: 8px;
	border-radius: 999px;
	background: rgba(109, 149, 209, 0.42);
}

.window-grid {
	display: grid;
	gap: 12px;
}

.window-panel,
.window-strip,
.metric-panel,
.command-item,
.shortcut-item,
.story-item,
.focus-item {
	position: relative;
	padding: 18px 20px;
	border-radius: 24px;
	background: linear-gradient(180deg, rgba(255, 255, 255, 0.74), rgba(247, 250, 255, 0.56));
	border: 1px solid rgba(255, 255, 255, 0.84);
	box-shadow: inset 0 1px 0 rgba(255, 255, 255, 0.96);
	transition: transform 0.2s ease, box-shadow 0.2s ease, background-color 0.2s ease;
}

.window-panel:hover,
.metric-panel:hover,
.command-item:hover,
.shortcut-item:hover,
.story-item:hover,
.focus-item:hover {
	transform: translateY(-2px);
	box-shadow: inset 0 1px 0 rgba(255, 255, 255, 0.96), 0 18px 34px rgba(88, 130, 191, 0.14);
}

.window-panel-strong,
.metric-panel.accent,
.emphasis-panel {
	background: linear-gradient(160deg, rgba(236, 244, 255, 0.88), rgba(224, 238, 255, 0.64));
	border-color: rgba(255, 255, 255, 0.88);
	box-shadow: inset 0 1px 0 rgba(255, 255, 255, 0.96), 0 18px 34px rgba(74, 123, 194, 0.14);
}

.mini-label,
.metric-label {
	font-size: 12px;
	font-weight: 560;
	letter-spacing: 0.12em;
	text-transform: uppercase;
	color: rgba(79, 109, 148, 0.76);
}

.mini-value,
.metric-value {
	margin-top: 10px;
	font-family: var(--font-display);
	font-size: 30px;
	font-weight: 720;
	letter-spacing: -0.03em;
	line-height: 1.14;
	font-variant-numeric: tabular-nums;
	color: #173355;
}

.mini-meta,
.metric-meta,
.command-meta,
.shortcut-meta,
.story-meta,
.focus-meta,
.section-caption {
	margin-top: 8px;
	font-size: 14px;
	line-height: 1.75;
	color: rgba(71, 92, 121, 0.82);
}

.window-strip {
	display: flex;
	flex-direction: column;
	gap: 12px;
}

.strip-track {
	height: 10px;
	border-radius: 999px;
	background: rgba(173, 198, 234, 0.38);
	overflow: hidden;
}

.strip-fill {
	width: 86%;
	height: 100%;
	border-radius: inherit;
	background: linear-gradient(90deg, #5b93ff, #8fc3ff);
}

.strip-meta,
.panel-header {
	display: flex;
	align-items: flex-start;
	justify-content: space-between;
	gap: 12px;
}

.strip-meta {
	align-items: center;
	font-size: 13px;
	color: #39557b;
}

.metrics-grid,
.dashboard-grid,
.story-grid {
	display: grid;
	gap: 20px;
}

.metrics-grid {
	grid-template-columns: repeat(4, minmax(0, 1fr));
}

.dashboard-grid,
.story-grid {
	grid-template-columns: minmax(0, 1.08fr) minmax(0, 0.92fr);
}

.section-title {
	font-family: var(--font-display);
	font-size: 18px;
	font-weight: 680;
	letter-spacing: -0.02em;
	color: #173355;
}

.command-list,
.shortcut-list,
.story-list,
.focus-stack {
	display: flex;
	flex-direction: column;
	gap: 12px;
}

.command-item,
.shortcut-item,
.story-item,
.focus-item {
	display: flex;
	align-items: flex-start;
	justify-content: space-between;
	gap: 14px;
}

.shortcut-item {
	width: 100%;
	cursor: pointer;
	text-align: left;
	font: inherit;
	outline: none;
}

.shortcut-item:focus-visible {
	box-shadow: inset 0 1px 0 rgba(255, 255, 255, 0.96), 0 0 0 2px rgba(65, 126, 222, 0.22);
}

.command-index,
.story-step {
	flex: 0 0 auto;
	display: grid;
	place-items: center;
	width: 38px;
	height: 38px;
	border-radius: 14px;
	font-size: 13px;
	font-weight: 700;
	color: #12458d;
	background: linear-gradient(135deg, rgba(255, 255, 255, 0.94), rgba(210, 228, 255, 0.98));
	border: 1px solid rgba(255, 255, 255, 0.84);
}

.command-copy,
.shortcut-copy,
.story-copy {
	flex: 1;
}

.command-name,
.shortcut-title,
.story-name,
.focus-name {
	font-size: 16px;
	font-weight: 660;
	letter-spacing: -0.01em;
	color: #173355;
}

.command-tag,
.shortcut-tag {
	align-self: center;
	padding: 8px 12px;
	border-radius: 999px;
	background: rgba(230, 239, 255, 0.8);
	border: 1px solid rgba(255, 255, 255, 0.88);
	font-size: 12px;
	font-weight: 650;
	color: #2357a2;
}

.focus-item {
	flex-direction: column;
	align-items: flex-start;
}

@media (max-width: 1280px) {
	.metrics-grid {
		grid-template-columns: repeat(2, minmax(0, 1fr));
	}
}

@media (max-width: 992px) {
	.dashboard-hero,
	.dashboard-grid,
	.story-grid {
		grid-template-columns: 1fr;
	}
}

@media (max-width: 640px) {
	.metrics-grid {
		grid-template-columns: 1fr;
	}

	.hero-copy h2 {
		font-size: 32px;
	}
}
</style>
