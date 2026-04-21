<template>
	<view class="page theme-gold">
		<view class="page-glow glow-one"></view>
		<view class="page-glow glow-two"></view>
		<view class="page-grid"></view>

		<view class="hero-shell">
			<view class="hero-copy">
				<text class="eyebrow">Account</text>
				<text class="hero-title">账号页只保留身份确认和退出登录</text>
				<text class="hero-desc">个性化设置和非核心功能已经移除，这一页只负责查看当前账号信息，并提供退出入口。</text>

				<view class="hero-actions">
					<button class="action-btn ghost" @click="backToHome">返回识别页</button>
					<button class="action-btn danger" @click="logout">退出登录</button>
				</view>
			</view>

			<view class="hero-stats">
				<view class="stat-card stat-card-strong">
					<text class="stat-label">昵称</text>
					<text class="stat-value">{{ user.nickname || '未设置' }}</text>
				</view>
				<view class="stat-card">
					<text class="stat-label">用户名</text>
					<text class="stat-value stat-value-small">{{ user.username || '-' }}</text>
				</view>
				<view class="stat-card">
					<text class="stat-label">身份</text>
					<text class="stat-value">{{ roleLabel }}</text>
				</view>
				<view class="stat-card">
					<text class="stat-label">用户 ID</text>
					<text class="stat-value stat-value-small">{{ user.id || '-' }}</text>
				</view>
			</view>
		</view>

		<app-tab-bar current="profile" />
	</view>
</template>

<script>
	import { defineComponent } from 'vue';
	import AppTabBar from '../../components/app-tab-bar.vue';
	import { clearAuthUser, getAuthUser, getRoleLabel, isFamilyRole } from '../../utils/auth';

	export default defineComponent({
		components: {
			AppTabBar
		},
		data() {
			return {
				user: getAuthUser()
			};
		},
		computed: {
			roleLabel() {
				return getRoleLabel(this.user.role);
			}
		},
		onLoad() {
			const user = getAuthUser();
			if (!user || !user.id) {
				uni.reLaunch({ url: '/pages/auth/auth' });
				return;
			}
			if (isFamilyRole(user.role)) {
				uni.reLaunch({ url: '/pages/family-center/family-center' });
				return;
			}
			this.user = user;
		},
		methods: {
			backToHome() {
				uni.redirectTo({
					url: '/pages/index/index'
				});
			},
			logout() {
				uni.showModal({
					title: '退出登录',
					content: '确认退出当前账号吗？',
					success: (res) => {
						if (!res.confirm) {
							return;
						}
						clearAuthUser();
						uni.reLaunch({
							url: '/pages/auth/auth'
						});
					}
				});
			}
		}
	});
</script>

<style>
	@import '../../styles/experience-shell.css';

	.action-btn.danger {
		background: rgba(227, 80, 80, 0.12);
		color: #b42318;
		border: 1px solid rgba(227, 80, 80, 0.22);
	}
</style>
