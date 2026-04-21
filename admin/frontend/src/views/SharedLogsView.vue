<template>
  <div class="ops-page">
    <section class="hero logs-hero">
      <div>
        <div class="hero-kicker">共享日志摘要</div>
        <h2>家属只看核心摘要，不暴露全部识别细节，但关键风险一眼就能看见。</h2>
        <p>支持按家属、用户、模式过滤，重点关注药品识别、地点摘要、寻物频次和安全预警。</p>
        <div class="inline-summary">
          <div class="summary-pill">当前摘要 <strong>{{ logs.length }}</strong> 条</div>
          <div class="summary-pill">药品相关 <strong>{{ medicineCount }}</strong> 条</div>
          <div class="summary-pill">预警 <strong>{{ alertCount }}</strong> 次</div>
        </div>
      </div>
      <div class="toolbar-stack">
        <div class="filter-grid filter-grid-compact">
          <a-input-number v-model:value="filters.familyMemberId" :min="1" placeholder="家属 ID" style="width: 100%" />
          <a-input-number v-model:value="filters.userId" :min="1" placeholder="用户 ID（可选）" style="width: 100%" />
          <a-select v-model:value="filters.mode" allow-clear placeholder="操作类型">
            <a-select-option value="analyze">识别</a-select-option>
            <a-select-option value="find-target">寻物</a-select-option>
          </a-select>
        </div>
        <div class="filter-grid">
          <a-date-picker v-model:value="filters.date" value-format="YYYY-MM-DD" style="width: 100%" />
          <div class="action-cluster">
            <a-button size="large" @click="resetFilters">重置筛选</a-button>
            <a-button type="primary" size="large" @click="loadLogs">查询摘要</a-button>
          </div>
        </div>
      </div>
    </section>

    <section class="stats-grid">
      <div class="stat-card accent">
        <div class="stat-label">摘要条数</div>
        <div class="stat-value">{{ logs.length }}</div>
      </div>
      <div class="stat-card">
        <div class="stat-label">药品相关</div>
        <div class="stat-value">{{ medicineCount }}</div>
      </div>
      <div class="stat-card warn">
        <div class="stat-label">预警次数</div>
        <div class="stat-value">{{ alertCount }}</div>
      </div>
    </section>

    <a-card class="surface-card" :bordered="false">
      <template #title>
        <div class="table-headline">
          <div>
            <div class="table-title">日志摘要列表</div>
            <div class="table-subtitle">按家属和用户维度查看关键识别与预警摘要</div>
          </div>
          <a-tag color="green">共 {{ logs.length }} 条</a-tag>
        </div>
      </template>
      <div class="table-toolbar">
        <div class="table-note">日志只保留摘要信息，便于家属和管理员快速查看关键记录与风险提醒。</div>
        <div class="table-toolbar-meta">
          <a-tag color="blue">已生效筛选 {{ activeFilterCount }} 项</a-tag>
        </div>
      </div>
      <a-table
        row-key="recordId"
        :loading="loading"
        :data-source="logs"
        :columns="columns"
        :pagination="{ pageSize: 8, showSizeChanger: false }"
        :scroll="{ x: 1080 }"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'user'">
            <div>
              <div class="strong">{{ record.nickname }}</div>
              <div class="muted">{{ record.username }}</div>
            </div>
          </template>
          <template v-else-if="column.key === 'mode'">
            <a-tag :color="record.mode === 'find-target' ? 'geekblue' : 'cyan'">{{ record.mode === 'find-target' ? '寻物' : '识别' }}</a-tag>
          </template>
          <template v-else-if="column.key === 'coreItem'">
            <div>
              <div class="strong">{{ record.coreItem || '无主物品' }}</div>
              <div class="muted">{{ record.scene || '未知场景' }}</div>
            </div>
          </template>
          <template v-else-if="column.key === 'alert'">
            <a-tag v-if="record.alertTriggered" color="red">{{ record.alertType || '预警' }}</a-tag>
            <span v-else class="muted">无</span>
          </template>
          <template v-else-if="column.key === 'capturedAt'">
            <span class="muted">{{ formatDateTime(record.capturedAt) }}</span>
          </template>
        </template>
        <template #emptyText>
          <div class="table-empty">
            <div class="empty-orb">L</div>
            <div class="empty-title">没有匹配的日志摘要</div>
            <div class="empty-desc">请调整家属、用户、模式或日期条件后重新查询。</div>
            <div class="empty-actions">
              <a-button @click="resetFilters">重置筛选</a-button>
              <a-button type="primary" @click="loadLogs">重新查询</a-button>
            </div>
          </div>
        </template>
      </a-table>
    </a-card>
  </div>
</template>

<script setup lang="ts">
import { computed, reactive, ref } from 'vue';
import { message, type TableColumnsType } from 'ant-design-vue';
import { fetchSharedLogs } from '@/api/familySafety';
import { formatDateTime } from '@/lib/format';
import type { SharedLog, SharedLogFilters } from '@/types/models';

interface SharedLogQueryState {
  familyMemberId: number | null;
  userId: number | null;
  mode?: string;
  date?: string;
}

const loading = ref(false);
const logs = ref<SharedLog[]>([]);
const filters = reactive<SharedLogQueryState>({
  familyMemberId: 1,
  userId: null,
  mode: undefined,
  date: undefined
});

const columns: TableColumnsType<SharedLog> = [
  { title: '用户', key: 'user', width: 160 },
  { title: '类型', dataIndex: 'mode', key: 'mode', width: 120 },
  { title: '核心内容', key: 'coreItem', width: 230 },
  { title: '指令', dataIndex: 'triggerCommand', key: 'triggerCommand', width: 200 },
  { title: '地点', dataIndex: 'locationText', key: 'locationText', width: 180 },
  { title: '预警', key: 'alert', width: 120 },
  { title: '预警说明', dataIndex: 'alertMessage', key: 'alertMessage', width: 220 },
  { title: '时间', dataIndex: 'capturedAt', key: 'capturedAt', width: 180 }
];

const alertCount = computed(() => logs.value.filter((item) => item.alertTriggered).length);
const medicineCount = computed(() => logs.value.filter((item) => /药|胶囊|片/.test(item.coreItem || '')).length);
const activeFilterCount = computed(() => {
	return [filters.familyMemberId, filters.userId, filters.mode, filters.date].filter(Boolean).length;
});

loadLogs();

async function loadLogs() {
  if (!filters.familyMemberId) {
    message.warning('请先输入家属 ID');
    return;
  }
  loading.value = true;
  try {
    logs.value = await fetchSharedLogs(buildFilters());
  } catch (error) {
    message.error(error instanceof Error ? error.message : '加载日志失败');
  } finally {
    loading.value = false;
  }
}

function buildFilters(): SharedLogFilters {
  return {
    familyMemberId: filters.familyMemberId as number,
    userId: filters.userId || undefined,
    mode: filters.mode,
    date: filters.date
  };
}

function resetFilters() {
	filters.familyMemberId = 1;
	filters.userId = null;
	filters.mode = undefined;
	filters.date = undefined;
	loadLogs();
}
</script>

<style scoped>
@import '@/styles/ops-surface.css';

.logs-hero {
	background:
		radial-gradient(circle at right top, rgba(127, 183, 255, 0.18), transparent 28%),
		linear-gradient(135deg, rgba(255, 255, 255, 0.8), rgba(239, 246, 255, 0.92) 56%, rgba(232, 243, 255, 0.84) 100%);
}
</style>
