<template>
	<view class="page theme-gold">
		<view class="page-glow glow-one"></view>
		<view class="page-glow glow-two"></view>
		<view class="page-grid"></view>

		<view class="hero-shell">
			<view class="hero-copy">
				<text class="eyebrow">Vision Scan</text>
				<text class="hero-title">首版只保留单次智能视觉识别</text>
				<text class="hero-desc">拍一张图或者从相册选一张图，系统会返回场景、识别摘要和物体清单。知识问答、寻物和调试模块都已经移除。</text>

				<view class="hero-actions">
					<button class="action-btn primary" @click="pickImage(['camera'])" :disabled="loading">拍照识别</button>
					<button class="action-btn ghost" @click="pickImage(['album'])" :disabled="loading">相册识别</button>
				</view>
			</view>

			<view class="hero-stats">
				<view class="stat-card stat-card-strong">
					<text class="stat-label">当前状态</text>
					<text class="stat-value">{{ loading ? '识别中' : '待识别' }}</text>
				</view>
				<view class="stat-card">
					<text class="stat-label">最近场景</text>
					<text class="stat-value stat-value-small">{{ result ? result.scene : '暂无' }}</text>
				</view>
				<view class="stat-card">
					<text class="stat-label">识别物体</text>
					<text class="stat-value">{{ itemCount }}</text>
				</view>
				<view class="stat-card">
					<text class="stat-label">最近时间</text>
					<text class="stat-value stat-value-small">{{ capturedAtText }}</text>
				</view>
			</view>
		</view>

		<view v-if="imagePath" class="section-panel">
			<view class="section-head">
				<view>
					<text class="section-kicker">当前图片</text>
					<text class="section-title">待识别画面</text>
				</view>
			</view>
			<image class="preview-image" :src="imagePath" mode="aspectFill"></image>
		</view>

		<view v-if="result" class="section-panel">
			<view class="section-head">
				<view>
					<text class="section-kicker">识别结果</text>
					<text class="section-title">{{ result.scene || '未识别到明确场景' }}</text>
				</view>
				<text class="section-tag">最新一次</text>
			</view>

			<view class="result-highlight">
				<text class="result-title">语音摘要</text>
				<text class="result-text">{{ result.narration || '本次没有返回语音摘要。' }}</text>
			</view>

			<view class="result-grid">
				<view class="result-meta">
					<text class="meta-label">触发指令</text>
					<text class="meta-value">{{ result.triggerCommand || '识别当前画面' }}</text>
				</view>
				<view class="result-meta">
					<text class="meta-label">位置摘要</text>
					<text class="meta-value">{{ result.positionSummary || '暂无' }}</text>
				</view>
			</view>

			<view class="item-list" v-if="result.items && result.items.length">
				<view v-for="(item, index) in result.items" :key="`${item.name}-${index}`" class="item-card">
					<text class="item-name">{{ item.name }}</text>
					<text class="item-meta">{{ item.position || '未知位置' }} / 置信度 {{ item.confidence || 0 }}%</text>
				</view>
			</view>
		</view>

		<view v-if="!result" class="section-panel empty-panel">
			<text class="empty-title">还没有识别结果</text>
			<text class="empty-text">点击上方按钮拍一张图，或者从相册选一张图，就可以开始首版识别流程。</text>
		</view>

		<app-tab-bar current="home" />
	</view>
</template>

<script>
	import { defineComponent } from 'vue';
	import AppTabBar from '../../components/app-tab-bar.vue';
	import { API_BASE } from '../../utils/api';
	import { getAuthUser, isVisionRole } from '../../utils/auth';

	export default defineComponent({
		components: {
			AppTabBar
		},
		data() {
			return {
				loading: false,
				result: null,
				imagePath: ''
			};
		},
		computed: {
			itemCount() {
				return this.result && this.result.items ? this.result.items.length : 0;
			},
			capturedAtText() {
				return this.result && this.result.capturedAt ? String(this.result.capturedAt).replace('T', ' ') : '暂无';
			}
		},
		onLoad() {
			this.ensureVisionUser();
		},
		methods: {
			ensureVisionUser() {
				const user = getAuthUser();
				if (!user || !user.id) {
					uni.reLaunch({ url: '/pages/auth/auth' });
					return false;
				}
				if (!isVisionRole(user.role)) {
					uni.reLaunch({ url: '/pages/family-center/family-center' });
					return false;
				}
				return true;
			},
			pickImage(sourceType) {
				if (!this.ensureVisionUser()) {
					return;
				}
				uni.chooseImage({
					count: 1,
					sizeType: ['compressed'],
					sourceType,
					success: (res) => {
						const filePath = res.tempFilePaths && res.tempFilePaths[0];
						if (!filePath) {
							return;
						}
						this.imagePath = filePath;
						this.uploadImage(filePath);
					}
				});
			},
			uploadImage(filePath) {
				const user = getAuthUser();
				this.loading = true;
				uni.uploadFile({
					url: `${API_BASE}/api/vision/analyze`,
					filePath,
					name: 'image',
					formData: {
						userId: String(user.id),
						command: '识别当前画面'
					},
					success: (res) => {
						this.loading = false;
						if (res.statusCode !== 200) {
							const data = this.parseJson(res.data);
							uni.showToast({ title: (data && data.message) || '识别失败', icon: 'none' });
							return;
						}
						this.result = this.parseJson(res.data) || null;
					},
					fail: () => {
						this.loading = false;
						uni.showToast({ title: '识别请求失败', icon: 'none' });
					}
				});
			},
			parseJson(value) {
				if (!value) {
					return null;
				}
				if (typeof value === 'object') {
					return value;
				}
				try {
					return JSON.parse(value);
				} catch (error) {
					return null;
				}
			}
		}
	});
</script>

<style>
	@import '../../styles/experience-shell.css';

	.preview-image {
		display: block;
		width: 100%;
		height: 420rpx;
		border-radius: 24rpx;
		margin-top: 18rpx;
	}

	.result-highlight,
	.result-meta,
	.item-card,
	.empty-panel {
		border-radius: 24rpx;
	}

	.result-highlight {
		padding: 22rpx;
		background: linear-gradient(135deg, rgba(236, 244, 255, 0.96), rgba(255, 255, 255, 0.94));
		border: 1px solid rgba(193, 220, 255, 0.92);
		box-shadow: 0 18rpx 40rpx rgba(79, 118, 172, 0.08);
	}

	.result-title,
	.item-name,
	.empty-title {
		display: block;
		font-size: 32rpx;
		line-height: 1.3;
		font-weight: bold;
		color: var(--text-main);
	}

	.result-text,
	.item-meta,
	.empty-text {
		display: block;
		margin-top: 10rpx;
		font-size: 28rpx;
		line-height: 1.7;
		color: var(--text-soft);
	}

	.result-grid {
		display: grid;
		grid-template-columns: repeat(2, minmax(0, 1fr));
		gap: 14rpx;
		margin-top: 18rpx;
	}

	.result-meta {
		padding: 20rpx;
		background: linear-gradient(180deg, rgba(255, 255, 255, 0.8), rgba(245, 249, 255, 0.72));
		border: 1px solid rgba(255, 255, 255, 0.84);
		box-shadow: 0 16rpx 36rpx rgba(79, 118, 172, 0.08);
	}

	.meta-label {
		display: block;
		font-size: 22rpx;
		color: var(--text-soft);
	}

	.meta-value {
		display: block;
		margin-top: 8rpx;
		font-size: 28rpx;
		line-height: 1.5;
		font-weight: bold;
		color: var(--text-main);
	}

	.item-list {
		display: grid;
		gap: 14rpx;
		margin-top: 18rpx;
	}

	.item-card {
		padding: 20rpx;
		background: rgba(255, 255, 255, 0.72);
		border: 1px solid rgba(255, 255, 255, 0.84);
		box-shadow: 0 16rpx 36rpx rgba(79, 118, 172, 0.08);
	}

	.empty-title {
		font-size: 36rpx;
	}

	@media screen and (max-width: 720px) {
		.result-grid {
			grid-template-columns: 1fr;
		}
	}
</style>
