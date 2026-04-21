<template>
  <div class="ops-page">
    <section class="hero medicine-hero">
      <div>
        <div class="hero-kicker">药品档案管理</div>
        <h2>把药品档案提前录好，用户扫描命中后就能优先播报用法、禁忌和有效期提醒。</h2>
        <p>这里重点维护名称、别名、说明摘要和有效期，前端识别命中后会自动切到药品优先播报与强预警模式。</p>
        <div class="inline-summary">
          <div class="summary-pill">档案 <strong>{{ profiles.length }}</strong> 条</div>
          <div class="summary-pill">即将过期 <strong>{{ expiringSoonCount }}</strong> 条</div>
          <div class="summary-pill">已过期 <strong>{{ expiredCount }}</strong> 条</div>
        </div>
      </div>
      <div class="toolbar-stack">
        <a-input-search
          v-model:value="keyword"
          placeholder="搜索药品名称 / 别名"
          allow-clear
          enter-button="搜索"
          @search="loadProfiles"
        />
        <div class="action-cluster">
          <a-button size="large" @click="resetSearch">重置筛选</a-button>
          <a-button type="primary" size="large" @click="openCreateModal">新增档案</a-button>
        </div>
      </div>
    </section>

    <section class="stats-grid">
      <div class="stat-card accent">
        <div class="stat-label">档案总数</div>
        <div class="stat-value">{{ profiles.length }}</div>
      </div>
      <div class="stat-card">
        <div class="stat-label">即将过期</div>
        <div class="stat-value">{{ expiringSoonCount }}</div>
      </div>
      <div class="stat-card warn">
        <div class="stat-label">已经过期</div>
        <div class="stat-value">{{ expiredCount }}</div>
      </div>
    </section>

    <a-card class="surface-card" :bordered="false">
      <template #title>
        <div class="table-headline">
          <div>
            <div class="table-title">药品档案列表</div>
            <div class="table-subtitle">维护扫描命中后优先播报的药品资料与有效期信息</div>
          </div>
          <a-tag color="orange">共 {{ profiles.length }} 条</a-tag>
        </div>
      </template>
      <div class="table-toolbar">
        <div class="table-note">优先维护常用药品与有效期，方便识别命中后快速播报与提醒。</div>
        <div class="table-toolbar-meta">
          <a-tag color="orange">搜索条件 {{ keyword ? '已设置' : '未设置' }}</a-tag>
        </div>
      </div>
      <a-table
        row-key="id"
        :loading="loading"
        :data-source="profiles"
        :columns="columns"
        :pagination="{ pageSize: 8, showSizeChanger: false }"
        :scroll="{ x: 1180 }"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'medicineName'">
            <div>
              <div class="strong">{{ record.medicineName }}</div>
              <div class="muted">{{ record.genericName || '未填写通用名' }}</div>
            </div>
          </template>
          <template v-else-if="column.key === 'user'">
            <div>
              <div class="strong">{{ record.nickname }}</div>
              <div class="muted">{{ record.username }}</div>
            </div>
          </template>
          <template v-else-if="column.key === 'expiryDate'">
            <a-tag :color="isExpired(record.expiryDate) ? 'red' : 'green'">
              {{ record.expiryDate || '未填写' }}
            </a-tag>
          </template>
          <template v-else-if="column.key === 'action'">
            <div class="action-row">
              <a-button type="link" @click="openEditModal(record)">编辑</a-button>
              <a-popconfirm title="确认删除该药品档案吗？" @confirm="handleDelete(record)">
                <a-button type="link" danger>删除</a-button>
              </a-popconfirm>
            </div>
          </template>
        </template>
        <template #emptyText>
          <div class="table-empty">
            <div class="empty-orb">M</div>
            <div class="empty-title">还没有药品档案</div>
            <div class="empty-desc">可以先新增药品资料，后续识别命中后就能优先播报相关内容。</div>
            <div class="empty-actions">
              <a-button @click="resetSearch">重置筛选</a-button>
              <a-button type="primary" @click="openCreateModal">新增档案</a-button>
            </div>
          </div>
        </template>
      </a-table>
    </a-card>

    <a-modal
      v-model:open="modalOpen"
      :title="modalMode === 'create' ? '新增药品档案' : '编辑药品档案'"
      width="760px"
      :confirm-loading="submitLoading"
      @ok="handleSubmit"
      @cancel="handleCancel"
    >
      <a-form ref="formRef" :model="formState" :label-col="{ span: 5 }" :wrapper-col="{ span: 18 }">
        <a-form-item label="用户 ID" name="userId" :rules="[{ required: true, message: '请输入用户 ID' }]">
          <a-input-number v-model:value="formState.userId" :min="1" style="width: 100%" />
        </a-form-item>
        <a-form-item label="家属 ID" name="familyMemberId">
          <a-input-number v-model:value="formState.familyMemberId" :min="1" style="width: 100%" />
        </a-form-item>
        <a-form-item label="药品名称" name="medicineName" :rules="[{ required: true, message: '请输入药品名称' }]">
          <a-input v-model:value="formState.medicineName" />
        </a-form-item>
        <a-form-item label="通用名" name="genericName">
          <a-input v-model:value="formState.genericName" />
        </a-form-item>
        <a-form-item label="用法用量" name="dosageUsage">
          <a-textarea v-model:value="formState.dosageUsage" :rows="3" />
        </a-form-item>
        <a-form-item label="适用人群" name="suitablePeople">
          <a-textarea v-model:value="formState.suitablePeople" :rows="2" />
        </a-form-item>
        <a-form-item label="禁忌" name="contraindications">
          <a-textarea v-model:value="formState.contraindications" :rows="2" />
        </a-form-item>
        <a-form-item label="说明摘要" name="description">
          <a-textarea v-model:value="formState.description" :rows="2" />
        </a-form-item>
        <a-form-item label="有效期" name="expiryDate">
          <a-date-picker v-model:value="formState.expiryDate" value-format="YYYY-MM-DD" style="width: 100%" />
        </a-form-item>
        <a-form-item label="别名/条码" name="barcodeOrAlias">
          <a-input v-model:value="formState.barcodeOrAlias" placeholder="多个别名可用逗号分隔" />
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue';
import { message, type TableColumnsType } from 'ant-design-vue';
import type { FormInstance } from 'ant-design-vue/es/form';
import { createMedicineProfile, deleteMedicineProfile, fetchMedicineProfiles, updateMedicineProfile } from '@/api/familySafety';
import type { MedicineProfile, MedicineProfilePayload } from '@/types/models';

type ModalMode = 'create' | 'edit';

interface MedicineProfileFormState {
  userId: number | null;
  familyMemberId: number | null;
  medicineName: string;
  genericName: string;
  description: string;
  dosageUsage: string;
  suitablePeople: string;
  contraindications: string;
  expiryDate?: string;
  barcodeOrAlias: string;
}

const loading = ref(false);
const submitLoading = ref(false);
const modalOpen = ref(false);
const modalMode = ref<ModalMode>('create');
const currentId = ref<number | null>(null);
const keyword = ref('');
const profiles = ref<MedicineProfile[]>([]);
const formRef = ref<FormInstance>();

const formState = reactive<MedicineProfileFormState>({
  userId: null,
  familyMemberId: null,
  medicineName: '',
  genericName: '',
  description: '',
  dosageUsage: '',
  suitablePeople: '',
  contraindications: '',
  expiryDate: undefined,
  barcodeOrAlias: ''
});

const columns: TableColumnsType<MedicineProfile> = [
  { title: '药品', key: 'medicineName', width: 180 },
  { title: '所属用户', key: 'user', width: 160 },
  { title: '用法用量', dataIndex: 'dosageUsage', key: 'dosageUsage', width: 220 },
  { title: '适用人群', dataIndex: 'suitablePeople', key: 'suitablePeople', width: 180 },
  { title: '禁忌', dataIndex: 'contraindications', key: 'contraindications', width: 180 },
  { title: '有效期', dataIndex: 'expiryDate', key: 'expiryDate', width: 120 },
  { title: '别名/条码', dataIndex: 'barcodeOrAlias', key: 'barcodeOrAlias', width: 180 },
  { title: '操作', key: 'action', fixed: 'right', width: 120 }
];

const expiredCount = computed(() => profiles.value.filter((item) => isExpired(item.expiryDate)).length);
const expiringSoonCount = computed(() =>
	profiles.value.filter((item) => {
		if (!item.expiryDate || isExpired(item.expiryDate)) {
			return false;
		}
		const expiry = new Date(item.expiryDate).getTime();
		return expiry - Date.now() <= 30 * 24 * 60 * 60 * 1000;
	}).length
);

onMounted(loadProfiles);

async function loadProfiles() {
  loading.value = true;
  try {
    profiles.value = await fetchMedicineProfiles({ keyword: keyword.value.trim() || undefined });
  } catch (error) {
    message.error(error instanceof Error ? error.message : '加载药品档案失败');
  } finally {
    loading.value = false;
  }
}

function resetSearch() {
  keyword.value = '';
  loadProfiles();
}

function openCreateModal() {
  modalMode.value = 'create';
  currentId.value = null;
  resetForm();
  modalOpen.value = true;
}

function openEditModal(record: MedicineProfile) {
  modalMode.value = 'edit';
  currentId.value = record.id;
  resetForm();
  Object.assign(formState, {
    userId: record.userId,
    familyMemberId: record.familyMemberId ?? null,
    medicineName: record.medicineName,
    genericName: record.genericName || '',
    description: record.description || '',
    dosageUsage: record.dosageUsage || '',
    suitablePeople: record.suitablePeople || '',
    contraindications: record.contraindications || '',
    expiryDate: record.expiryDate || undefined,
    barcodeOrAlias: record.barcodeOrAlias || ''
  });
  modalOpen.value = true;
}

async function handleSubmit() {
  try {
    await formRef.value?.validate();
    submitLoading.value = true;
    const payload = buildPayload();
    if (modalMode.value === 'create') {
      await createMedicineProfile(payload);
      message.success('药品档案已创建');
    } else {
      await updateMedicineProfile(currentId.value as number, payload);
      message.success('药品档案已更新');
    }
    modalOpen.value = false;
    resetForm();
    await loadProfiles();
  } catch (error) {
    message.error(error instanceof Error ? error.message : '保存药品档案失败');
  } finally {
    submitLoading.value = false;
  }
}

async function handleDelete(record: MedicineProfile) {
  try {
    await deleteMedicineProfile(record.id);
    message.success('药品档案已删除');
    await loadProfiles();
  } catch (error) {
    message.error(error instanceof Error ? error.message : '删除药品档案失败');
  }
}

function handleCancel() {
  modalOpen.value = false;
  resetForm();
}

function resetForm() {
  Object.assign(formState, {
    userId: null,
    familyMemberId: null,
    medicineName: '',
    genericName: '',
    description: '',
    dosageUsage: '',
    suitablePeople: '',
    contraindications: '',
    expiryDate: undefined,
    barcodeOrAlias: ''
  });
  formRef.value?.clearValidate();
}

function buildPayload(): MedicineProfilePayload {
  return {
    userId: formState.userId as number,
    familyMemberId: formState.familyMemberId,
    medicineName: formState.medicineName.trim(),
    genericName: formState.genericName.trim(),
    description: formState.description.trim(),
    dosageUsage: formState.dosageUsage.trim(),
    suitablePeople: formState.suitablePeople.trim(),
    contraindications: formState.contraindications.trim(),
    expiryDate: formState.expiryDate,
    barcodeOrAlias: formState.barcodeOrAlias.trim()
  };
}

function isExpired(date?: string): boolean {
  return !!date && new Date(date).getTime() < Date.now() - 24 * 60 * 60 * 1000;
}
</script>

<style scoped>
@import '@/styles/ops-surface.css';

.medicine-hero {
	background:
		radial-gradient(circle at right top, rgba(144, 193, 255, 0.18), transparent 26%),
		linear-gradient(135deg, rgba(255, 255, 255, 0.8), rgba(241, 247, 255, 0.92) 55%, rgba(233, 243, 255, 0.84) 100%);
}
</style>
