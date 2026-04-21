<template>
	<div class="login-page">
		<div class="login-hero">
			<div class="hero-kicker">管理员后台</div>
			<h1>把后台管理做得清楚、顺手、稳定。</h1>
			<p>
				当前版本只保留单管理员登录，使用固定账号 <strong>admin</strong> 即可进入管理台。
			</p>

			<div class="hero-points">
				<div class="hero-point">
					<span class="hero-dot"></span>
					<span>统一管理应用用户、账号状态和基础资料</span>
				</div>
				<div class="hero-point">
					<span class="hero-dot"></span>
					<span>集中处理家属绑定与视障用户协同关系</span>
				</div>
				<div class="hero-point">
					<span class="hero-dot"></span>
					<span>同步查看药品档案、共享日志和安全提醒</span>
				</div>
			</div>
		</div>

		<div class="login-panel">
			<div class="login-card">
				<div class="login-card-top">
					<div class="login-badge">后台登录</div>
					<h2>管理员登录</h2>
					<p>默认账号：admin　默认密码：123456</p>
				</div>

				<a-form
					layout="vertical"
					:model="formState"
					:rules="rules"
					@finish="handleSubmit"
				>
					<a-form-item label="用户名" name="username">
						<a-input
							v-model:value="formState.username"
							placeholder="请输入 admin"
							size="large"
						/>
					</a-form-item>
					<a-form-item label="密码" name="password">
						<a-input-password
							v-model:value="formState.password"
							placeholder="请输入密码"
							size="large"
						/>
					</a-form-item>
					<a-form-item class="login-submit">
						<a-button
							type="primary"
							html-type="submit"
							size="large"
							block
							:loading="submitting"
						>
							进入管理台
						</a-button>
					</a-form-item>
				</a-form>
			</div>
		</div>
	</div>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue';
import { message } from 'ant-design-vue';
import type { Rule } from 'ant-design-vue/es/form';
import { useRoute, useRouter } from 'vue-router';
import { login } from '@/api/auth';
import { setAuthSession } from '@/lib/auth';

const route = useRoute();
const router = useRouter();
const submitting = ref(false);

const formState = reactive({
	username: 'admin',
	password: ''
});

const rules: Record<string, Rule[]> = {
	username: [{ required: true, message: '请输入用户名' }],
	password: [{ required: true, message: '请输入密码' }]
};

async function handleSubmit() {
	submitting.value = true;

	try {
		const data = await login({
			username: formState.username.trim(),
			password: formState.password
		});

		setAuthSession(data);
		message.success(`欢迎回来，${data.nickname}`);
		const redirect = typeof route.query.redirect === 'string' ? route.query.redirect : '/dashboard';
		await router.replace(redirect);
	} catch (error) {
		message.error(error instanceof Error ? error.message : '登录失败');
	} finally {
		submitting.value = false;
	}
}
</script>
