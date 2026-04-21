<template>
	<view :class="['page', themeClass, largeFontClass]">
		<view class="page-glow glow-one"></view>
		<view class="page-glow glow-two"></view>
		<view class="page-grid"></view>

		<view class="hero-shell">
			<view class="hero-copy">
				<text class="eyebrow">Care Profile</text>
				<text class="hero-title">把关键照护信息集中登记到后端</text>
				<text class="hero-desc">首版包含视障人士基本信息、紧急联系人和药品/疾病注意事项三类表单，保存后会直接写入后端。</text>

				<view class="hero-actions">
					<button class="action-btn primary" @click="saveAll">保存登记</button>
					<button class="action-btn ghost" @click="goRecords">查看记录</button>
				</view>
			</view>

			<view class="hero-stats">
				<view class="stat-card stat-card-strong">
					<text class="stat-label">资料完整度</text>
					<text class="stat-value">{{ completionText }}</text>
				</view>
				<view class="stat-card">
					<text class="stat-label">当前联系人</text>
					<text class="stat-value stat-value-small">{{ profile.emergencyContact.name || '待填写' }}</text>
				</view>
				<view class="stat-card">
					<text class="stat-label">常用药提醒</text>
					<text class="stat-value stat-value-small">{{ medicineShortText }}</text>
				</view>
				<view class="stat-card">
					<text class="stat-label">照护对象</text>
					<text class="stat-value stat-value-small">{{ profile.basicInfo.name || '待填写' }}</text>
				</view>
			</view>
		</view>

		<view class="section-panel">
			<view class="section-head">
				<view>
					<text class="section-kicker">表单一</text>
					<text class="section-title">视障人士基本信息</text>
				</view>
			</view>

			<view class="form-grid">
				<input class="input-field" v-model.trim="profile.basicInfo.name" placeholder="姓名" />
				<input class="input-field" v-model.trim="profile.basicInfo.gender" placeholder="性别" />
				<input class="input-field" v-model.trim="profile.basicInfo.age" placeholder="年龄" />
				<input class="input-field" v-model.trim="profile.basicInfo.visionLevel" placeholder="视力情况，例如全盲/低视力" />
			</view>
			<input class="input-field input-full" v-model.trim="profile.basicInfo.address" placeholder="常住地址" />
			<textarea class="textarea-field" v-model="profile.basicInfo.notes" placeholder="补充备注，例如出行习惯、居住情况等"></textarea>
		</view>

		<view class="section-panel">
			<view class="section-head">
				<view>
					<text class="section-kicker">表单二</text>
					<text class="section-title">紧急联系人信息</text>
				</view>
			</view>

			<view class="form-grid">
				<input class="input-field" v-model.trim="profile.emergencyContact.name" placeholder="联系人姓名" />
				<input class="input-field" v-model.trim="profile.emergencyContact.relation" placeholder="与视障人士关系" />
				<input class="input-field" v-model.trim="profile.emergencyContact.phone" placeholder="联系电话" />
				<input class="input-field" v-model.trim="profile.emergencyContact.backupPhone" placeholder="备用电话" />
			</view>
		</view>

		<view class="section-panel">
			<view class="section-head">
				<view>
					<text class="section-kicker">表单三</text>
					<text class="section-title">常用药品 / 疾病注意事项</text>
				</view>
			</view>

			<textarea class="textarea-field" v-model="profile.healthInfo.medicine" placeholder="常用药品、服药频次、放置位置"></textarea>
			<textarea class="textarea-field" v-model="profile.healthInfo.diseaseNote" placeholder="疾病史、就医提醒、外出注意事项"></textarea>
			<textarea class="textarea-field" v-model="profile.healthInfo.allergy" placeholder="过敏信息"></textarea>
			<textarea class="textarea-field" v-model="profile.healthInfo.reminder" placeholder="其他提醒，例如门锁、钥匙、陪诊安排"></textarea>
		</view>

		<view class="section-panel shortcut-panel">
			<view class="action-row">
				<button class="action-btn primary" @click="saveAll">保存登记</button>
				<button class="action-btn ghost" @click="goCenter">返回家属中心</button>
			</view>
		</view>

		<app-tab-bar current="family-profile" />
	</view>
</template>

<script>
	import { defineComponent } from 'vue';
	import AppTabBar from '../../components/app-tab-bar.vue';
	import { API_BASE } from '../../utils/api';
	import { getAuthUser, isFamilyRole } from '../../utils/auth';
	import { loadUserSettings } from '../../utils/user-settings';

	function emptyProfile() {
		return {
			basicInfo: {
				name: '',
				gender: '',
				age: '',
				visionLevel: '',
				address: '',
				notes: ''
			},
			emergencyContact: {
				name: '',
				relation: '',
				phone: '',
				backupPhone: ''
			},
			healthInfo: {
				medicine: '',
				diseaseNote: '',
				allergy: '',
				reminder: ''
			}
		};
	}

	export default defineComponent({
		components: {
			AppTabBar
		},
		data() {
			return {
				settings: loadUserSettings(),
				profile: emptyProfile()
			};
		},
		computed: {
			themeClass() {
				return this.settings.contrastMode === 'black-yellow' ? 'theme-yellow' : 'theme-gold';
			},
			largeFontClass() {
				return this.settings.extraLargeText ? 'font-large' : '';
			},
			completionText() {
				let count = 0;
				if (this.profile.basicInfo.name && this.profile.basicInfo.age) count += 1;
				if (this.profile.emergencyContact.name && this.profile.emergencyContact.phone) count += 1;
				if (this.profile.healthInfo.medicine || this.profile.healthInfo.diseaseNote) count += 1;
				return `${count}/3`;
			},
			medicineShortText() {
				const medicine = this.profile.healthInfo.medicine || '';
				return medicine ? medicine.slice(0, 14) + (medicine.length > 14 ? '...' : '') : '待填写';
			}
		},
		onLoad() {
			this.ensureFamilyRole();
		},
		onShow() {
			this.settings = loadUserSettings();
			this.loadProfile();
		},
		methods: {
			ensureFamilyRole() {
				const user = getAuthUser();
				if (!user || !user.id) {
					uni.reLaunch({ url: '/pages/auth/auth' });
					return false;
				}
				if (!isFamilyRole(user.role)) {
					uni.reLaunch({ url: '/pages/index/index' });
					return false;
				}
				return true;
			},
			loadProfile() {
				const user = getAuthUser();
				uni.request({
					url: `${API_BASE}/api/family/profile`,
					method: 'GET',
					data: {
						familyUserId: user.id
					},
					success: (res) => {
						if (res.statusCode === 200) {
							this.profile = res.data || emptyProfile();
							return;
						}
						const data = res.data || {};
						uni.showToast({ title: data.message || '资料加载失败', icon: 'none' });
					},
					fail: () => {
						uni.showToast({ title: '资料加载失败', icon: 'none' });
					}
				});
			},
			saveAll() {
				const user = getAuthUser();
				uni.request({
					url: `${API_BASE}/api/family/profile?familyUserId=${user.id}`,
					method: 'POST',
					header: {
						'Content-Type': 'application/json'
					},
					data: this.profile,
					success: (res) => {
						if (res.statusCode === 200) {
							this.profile = res.data || this.profile;
							uni.showToast({ title: '登记信息已保存', icon: 'success' });
							return;
						}
						const data = res.data || {};
						uni.showToast({ title: data.message || '保存失败', icon: 'none' });
					},
					fail: () => {
						uni.showToast({ title: '保存失败', icon: 'none' });
					}
				});
			},
			goRecords() {
				uni.redirectTo({
					url: '/pages/family-records/family-records'
				});
			},
			goCenter() {
				uni.redirectTo({
					url: '/pages/family-center/family-center'
				});
			}
		}
	});
</script>

<style>
	@import '../../styles/experience-shell.css';

	.form-grid {
		display: grid;
		grid-template-columns: repeat(2, minmax(0, 1fr));
		gap: 14rpx;
	}

	.input-field,
	.textarea-field {
		width: 100%;
		box-sizing: border-box;
		margin-top: 16rpx;
		padding: 22rpx;
		border-radius: 22rpx;
		background: rgba(255, 255, 255, 0.74);
		border: 1px solid rgba(255, 255, 255, 0.86);
		color: var(--text-main);
		font-size: 28rpx;
		box-shadow: inset 0 1px 0 rgba(255, 255, 255, 0.78);
	}

	.input-field {
		height: 88rpx;
	}

	.input-full {
		display: block;
	}

	.textarea-field {
		min-height: 180rpx;
		line-height: 1.65;
	}

	@media screen and (max-width: 720px) {
		.form-grid {
			grid-template-columns: 1fr;
		}
	}
</style>
