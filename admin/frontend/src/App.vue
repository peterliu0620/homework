<template>
	<a-config-provider :theme="themeConfig" :locale="zhCN">
		<router-view />
	</a-config-provider>
</template>

<script setup>
import zhCN from 'ant-design-vue/es/locale/zh_CN';

const themeConfig = {
	token: {
		colorPrimary: '#0f766e',
		colorInfo: '#0f766e',
		colorSuccess: '#0f766e',
		colorWarning: '#ea580c',
		colorError: '#dc2626',
		borderRadius: 16,
		fontFamily: "'Bahnschrift', 'Segoe UI', 'PingFang SC', 'Microsoft YaHei', sans-serif",
		boxShadowSecondary: '0 18px 40px rgba(15, 23, 42, 0.12)'
	}
};
</script>
