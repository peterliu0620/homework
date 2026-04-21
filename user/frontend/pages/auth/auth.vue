<template>
	<view class="page auth-page">
		<view class="auth-glow auth-glow-one"></view>
		<view class="auth-glow auth-glow-two"></view>
		<view class="auth-grid"></view>

		<view class="auth-shell">
			<view class="brand-column">
				<text class="brand-eyebrow">Vision Guide</text>
				<text class="brand-title">同一入口，切换两种照护视角</text>
				<text class="brand-desc">视障人士进入识别工作台，家人进入记录与登记中心。现在登录和登记都直接走后端接口，不再依赖本地 mock 数据。</text>

				<view class="role-showcase">
					<view
						v-for="item in roleOptions"
						:key="item.value"
						:class="['role-card', selectedRole === item.value ? 'role-card-active' : '']"
						@click="selectedRole = item.value"
					>
						<text class="role-name">{{ item.label }}</text>
						<text class="role-desc">{{ item.desc }}</text>
					</view>
				</view>

				<view class="brand-rail">
					<view class="brand-rail-item">
						<text class="brand-rail-label">当前身份</text>
						<text class="brand-rail-value">{{ currentRoleLabel }}</text>
					</view>
					<view class="brand-rail-item">
						<text class="brand-rail-label">进入后首页</text>
						<text class="brand-rail-value">{{ selectedRole === USER_ROLE_FAMILY ? '家属中心' : '识别首页' }}</text>
					</view>
				</view>

				<view class="demo-panel">
					<text class="demo-title">当前流程说明</text>
					<text class="demo-line">请先注册对应身份，再使用账号密码登录。</text>
					<text class="demo-line">家人身份会自动尝试关联唯一的视障人士账号，方便先跑通首版流程。</text>
				</view>
			</view>

			<view class="auth-panel">
				<view class="panel-head">
					<text class="panel-kicker">身份登录</text>
					<text class="panel-title">{{ mode === 'login' ? '先选择身份，再进入对应工作区' : '创建当前身份下的新账号' }}</text>
				</view>

				<view class="tabs">
					<view :class="['tab', mode === 'login' ? 'active' : '']" @click="mode = 'login'">登录</view>
					<view :class="['tab', mode === 'register' ? 'active' : '']" @click="mode = 'register'">注册</view>
				</view>

				<view class="role-picker">
					<view
						v-for="item in roleOptions"
						:key="item.value"
						:class="['picker-chip', selectedRole === item.value ? 'picker-chip-active' : '']"
						@click="selectedRole = item.value"
					>
						{{ item.label }}
					</view>
				</view>

				<view class="form-stack">
					<input class="input" v-model.trim="form.username" placeholder="用户名（至少 4 位）" />
					<input class="input" v-model.trim="form.password" password placeholder="密码（至少 6 位）" />
					<input v-if="mode === 'register'" class="input" v-model.trim="form.nickname" placeholder="昵称" />
					<input v-if="mode === 'register'" class="input" v-model.trim="form.phone" placeholder="手机号（可选）" />
					<input v-if="mode === 'register'" class="input" v-model.trim="form.email" placeholder="邮箱（可选）" />
				</view>

				<view class="action-group">
					<button class="btn primary" @click="submit" :disabled="loading">
						{{ loading ? '提交中...' : mode === 'login' ? `以${currentRoleLabel}身份登录` : `注册${currentRoleLabel}账号` }}
					</button>
					<button class="btn secondary" @click="mode = mode === 'login' ? 'register' : 'login'">
						{{ mode === 'login' ? '切换到注册' : '切换到登录' }}
					</button>
				</view>

				<view class="result" v-if="userInfo && userInfo.id">
					<text class="line">当前用户：{{ userInfo.nickname }}（{{ userInfo.username }}）</text>
					<text class="line">身份：{{ getRoleLabel(userInfo.role) }}</text>
				</view>
			</view>
		</view>
	</view>
</template>

<script>
	import { defineComponent } from 'vue';
	import { API_BASE } from '../../utils/api';
	import {
		ROLE_OPTIONS,
		USER_ROLE_FAMILY,
		USER_ROLE_VISION,
		getAuthUser,
		getDefaultRouteByRole,
		getRoleLabel,
		saveAuthUser
	} from '../../utils/auth';

	export default defineComponent({
		data() {
			return {
				mode: 'login',
				loading: false,
				userInfo: null,
				roleOptions: ROLE_OPTIONS,
				selectedRole: USER_ROLE_VISION,
				form: {
					username: '',
					password: '',
					nickname: '',
					phone: '',
					email: ''
				},
				USER_ROLE_FAMILY,
				USER_ROLE_VISION
			};
		},
		computed: {
			currentRoleLabel() {
				return getRoleLabel(this.selectedRole);
			}
		},
		onLoad() {
			const cached = getAuthUser();
			if (cached && cached.id) {
				this.userInfo = cached;
				uni.reLaunch({
					url: getDefaultRouteByRole(cached.role)
				});
			}
		},
		methods: {
			getRoleLabel,
			submit() {
				const payload = {
					...this.form,
					role: this.selectedRole
				};
				this.loading = true;
				uni.request({
					url: `${API_BASE}/api/auth/${this.mode === 'login' ? 'login' : 'register'}`,
					method: 'POST',
					header: {
						'Content-Type': 'application/json'
					},
					data: payload,
					success: (res) => {
						this.loading = false;
						if (res.statusCode !== 200) {
							const data = res.data || {};
							uni.showToast({ title: data.message || '请求失败', icon: 'none' });
							return;
						}
						this.userInfo = saveAuthUser(res.data || {});
						uni.showToast({ title: this.mode === 'login' ? '登录成功' : '注册成功', icon: 'success' });
						setTimeout(() => {
							uni.reLaunch({
								url: getDefaultRouteByRole(this.userInfo.role)
							});
						}, 240);
					},
					fail: () => {
						this.loading = false;
						uni.showToast({ title: '后端请求失败', icon: 'none' });
					}
				});
			}
		}
	});
</script>

<style>
	.page {
		min-height: 100vh;
	}

	.auth-page {
		position: relative;
		overflow: hidden;
		padding: 36rpx 28rpx;
		background:
			radial-gradient(circle at top left, rgba(146, 196, 255, 0.3), rgba(146, 196, 255, 0) 34%),
			radial-gradient(circle at 82% 10%, rgba(255, 255, 255, 0.94), rgba(255, 255, 255, 0) 26%),
			linear-gradient(180deg, #f4f8ff 0%, #edf4ff 48%, #e8f1ff 100%);
	}

	.auth-glow {
		position: absolute;
		border-radius: 999rpx;
		filter: blur(32rpx);
		pointer-events: none;
	}

	.auth-glow-one {
		top: -80rpx;
		right: -120rpx;
		width: 420rpx;
		height: 420rpx;
		background: radial-gradient(circle, rgba(76, 141, 255, 0.22) 0%, rgba(76, 141, 255, 0) 70%);
	}

	.auth-glow-two {
		left: -140rpx;
		bottom: 120rpx;
		width: 480rpx;
		height: 480rpx;
		background: radial-gradient(circle, rgba(202, 225, 255, 0.92) 0%, rgba(202, 225, 255, 0) 72%);
	}

	.auth-grid {
		position: absolute;
		inset: 0;
		background-image:
			linear-gradient(rgba(255, 255, 255, 0.24) 1px, transparent 1px),
			linear-gradient(90deg, rgba(255, 255, 255, 0.24) 1px, transparent 1px);
		background-size: 46rpx 46rpx;
		mask-image: linear-gradient(180deg, rgba(0, 0, 0, 0.48), transparent 84%);
		pointer-events: none;
	}

	.auth-shell {
		position: relative;
		z-index: 1;
		display: grid;
		grid-template-columns: minmax(0, 1.08fr) minmax(320rpx, 0.92fr);
		gap: 24rpx;
		align-items: stretch;
	}

	.brand-column,
	.auth-panel {
		border-radius: 30rpx;
		padding: 34rpx;
		backdrop-filter: blur(20rpx) saturate(140%);
		box-shadow: 0 28rpx 70rpx rgba(79, 118, 172, 0.12);
		border: 1px solid rgba(255, 255, 255, 0.84);
	}

	.brand-column {
		position: relative;
		overflow: hidden;
		background: linear-gradient(135deg, rgba(255, 255, 255, 0.74), rgba(242, 247, 255, 0.88) 52%, rgba(235, 243, 255, 0.8) 100%);
	}

	.auth-panel {
		background: linear-gradient(135deg, rgba(255, 255, 255, 0.78), rgba(246, 250, 255, 0.92));
	}

	.brand-eyebrow,
	.panel-kicker {
		display: block;
		font-size: 20rpx;
		letter-spacing: 6rpx;
		text-transform: uppercase;
		color: #7b8da8;
	}

	.brand-title,
	.panel-title {
		display: block;
		margin-top: 18rpx;
		line-height: 1.14;
		font-weight: bold;
		color: #173355;
	}

	.brand-title {
		max-width: 11em;
		font-size: 60rpx;
	}

	.panel-title {
		font-size: 42rpx;
	}

	.brand-desc,
	.role-desc,
	.demo-line,
	.line {
		display: block;
		font-size: 28rpx;
		line-height: 1.72;
		color: #5f7390;
	}

	.role-showcase,
	.form-stack {
		display: grid;
		gap: 14rpx;
	}

	.role-showcase {
		margin-top: 30rpx;
	}

	.role-card,
	.brand-rail-item,
	.demo-panel,
	.result {
		border-radius: 24rpx;
		background: linear-gradient(180deg, rgba(255, 255, 255, 0.8), rgba(245, 249, 255, 0.72));
		border: 1px solid rgba(255, 255, 255, 0.84);
		box-shadow: 0 16rpx 40rpx rgba(79, 118, 172, 0.08);
	}

	.role-card {
		padding: 24rpx;
	}

	.role-card-active {
		background: linear-gradient(135deg, rgba(236, 244, 255, 0.96), rgba(255, 255, 255, 0.94));
		border-color: rgba(193, 220, 255, 0.92);
		box-shadow: 0 20rpx 46rpx rgba(76, 141, 255, 0.12);
	}

	.role-name,
	.demo-title {
		display: block;
		font-size: 30rpx;
		font-weight: bold;
		color: #173355;
	}

	.brand-rail {
		display: grid;
		grid-template-columns: repeat(2, minmax(0, 1fr));
		gap: 14rpx;
		margin-top: 24rpx;
	}

	.brand-rail-item,
	.demo-panel,
	.result {
		padding: 22rpx;
	}

	.brand-rail-label {
		display: block;
		font-size: 22rpx;
		color: #7b8da8;
	}

	.brand-rail-value {
		display: block;
		margin-top: 10rpx;
		font-size: 30rpx;
		line-height: 1.45;
		font-weight: bold;
		color: #173355;
	}

	.demo-panel {
		margin-top: 18rpx;
	}

	.panel-head {
		margin-bottom: 24rpx;
	}

	.tabs,
	.role-picker {
		display: flex;
		padding: 8rpx;
		border-radius: 999rpx;
		overflow: hidden;
	}

	.tabs {
		background: rgba(255, 255, 255, 0.58);
		border: 1px solid rgba(255, 255, 255, 0.86);
		margin-bottom: 22rpx;
	}

	.role-picker {
		gap: 10rpx;
		background: rgba(255, 255, 255, 0.54);
		border: 1px solid rgba(255, 255, 255, 0.82);
		margin-bottom: 22rpx;
	}

	.tab,
	.picker-chip {
		flex: 1;
		text-align: center;
		padding: 18rpx 0;
		border-radius: 999rpx;
		font-size: 28rpx;
		transition: all 0.24s ease;
	}

	.tab {
		color: #6a7f9b;
	}

	.active,
	.picker-chip-active {
		background: linear-gradient(135deg, #0f6cbd, #3a96ff);
		color: #ffffff;
		font-weight: bold;
		box-shadow: 0 14rpx 34rpx rgba(15, 108, 189, 0.18);
	}

	.picker-chip {
		color: #5f7390;
		background: rgba(255, 255, 255, 0.66);
	}

	.input {
		height: 86rpx;
		background: rgba(255, 255, 255, 0.72);
		border: 1px solid rgba(255, 255, 255, 0.86);
		border-radius: 20rpx;
		padding: 0 22rpx;
		color: #173355;
		font-size: 28rpx;
		box-shadow: inset 0 1px 0 rgba(255, 255, 255, 0.78);
	}

	.btn {
		display: inline-flex;
		align-items: center;
		justify-content: center;
		height: 88rpx;
		border-radius: 999rpx;
		font-size: 28rpx;
		font-weight: bold;
		margin: 0;
	}

	.primary {
		background: linear-gradient(135deg, #0f6cbd, #3a96ff);
		color: #ffffff;
		box-shadow: 0 18rpx 42rpx rgba(15, 108, 189, 0.2);
	}

	.secondary {
		background: rgba(255, 255, 255, 0.66);
		color: #115ea3;
		border: 1px solid rgba(255, 255, 255, 0.86);
	}

	.action-group {
		display: grid;
		gap: 14rpx;
		margin-top: 22rpx;
	}

	.result {
		margin-top: 20rpx;
	}

	@media screen and (max-width: 720px) {
		.auth-shell,
		.brand-rail {
			grid-template-columns: 1fr;
		}

		.brand-title {
			font-size: 52rpx;
		}

		.panel-title {
			font-size: 38rpx;
		}

		.brand-column,
		.auth-panel {
			padding: 28rpx;
			border-radius: 28rpx;
		}
	}
</style>
