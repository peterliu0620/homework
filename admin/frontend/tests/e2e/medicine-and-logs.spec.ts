import { test, expect, type Page, type Route } from '@playwright/test';

type MedicineProfile = {
  id: number;
  userId: number;
  username: string;
  nickname: string;
  familyMemberId?: number | null;
  medicineName: string;
  genericName?: string | null;
  description?: string | null;
  dosageUsage?: string | null;
  suitablePeople?: string | null;
  contraindications?: string | null;
  expiryDate?: string | null;
  barcodeOrAlias?: string | null;
};

type SharedLog = {
  recordId: number;
  userId: number;
  username: string;
  nickname: string;
  mode: string;
  coreItem?: string | null;
  scene?: string | null;
  triggerCommand?: string | null;
  locationText?: string | null;
  alertTriggered: boolean;
  alertType?: string | null;
  alertMessage?: string | null;
  capturedAt?: string | null;
};

test('admin can manage medicine profiles and query shared logs', async ({ page }) => {
  const state = createMockState();
  await seedAuth(page);
  await mockMedicineApis(page, state);

  await page.goto('/medicine-profiles');
  await expect(page.locator('.table-title')).toHaveText('药品档案列表');
  await expect(page.locator('tbody').getByText('阿莫西林胶囊')).toBeVisible();

  await page.getByRole('button', { name: '新增档案' }).click();
  await page.getByLabel('用户 ID').fill('12');
  await page.getByLabel('家属 ID').fill('2');
  await page.getByLabel('药品名称').fill('布洛芬缓释胶囊');
  await page.getByLabel('通用名').fill('布洛芬');
  await page.getByLabel('用法用量').fill('一次一粒，每日两次');
  await page.getByLabel('适用人群').fill('成人');
  await page.getByLabel('禁忌').fill('消化道溃疡患者慎用');
  await page.getByLabel('说明摘要').fill('用于缓解疼痛和退热');
  await page.getByLabel('别名/条码').fill('布洛芬,ibuprofen');
  await page.getByRole('button', { name: '确 定' }).click();

  await expect(page.locator('tbody').getByText('布洛芬缓释胶囊')).toBeVisible();

  await page.goto('/shared-logs');
  await expect(page.locator('.table-title')).toHaveText('日志摘要列表');
  await page.getByRole('button', { name: '查询摘要' }).click();

  await expect(page.locator('tbody').getByText('阿莫西林胶囊')).toBeVisible();
  await expect(page.locator('tbody').getByText('repeat_scan')).toBeVisible();
});

function createMockState() {
  const profiles: MedicineProfile[] = [
    {
      id: 1,
      userId: 11,
      username: 'blind-user-1',
      nickname: '视障用户甲',
      familyMemberId: 1,
      medicineName: '阿莫西林胶囊',
      genericName: '阿莫西林',
      dosageUsage: '一次两粒，每日三次',
      suitablePeople: '成人',
      contraindications: '青霉素过敏者禁用',
      expiryDate: '2026-12-31',
      barcodeOrAlias: '阿莫西林,amoxicillin'
    }
  ];

  const logs: SharedLog[] = [
    {
      recordId: 1,
      userId: 11,
      username: 'blind-user-1',
      nickname: '视障用户甲',
      mode: 'analyze',
      coreItem: '阿莫西林胶囊',
      scene: '卧室',
      triggerCommand: '识别药品',
      locationText: '床头柜',
      alertTriggered: true,
      alertType: 'repeat_scan',
      alertMessage: '短时重复扫码，请联系家属确认。',
      capturedAt: '2026-04-14T21:00:00'
    }
  ];

  let nextId = 2;

  return {
    profiles,
    logs,
    nextId: () => nextId++
  };
}

async function seedAuth(page: Page) {
  await page.addInitScript(() => {
    localStorage.setItem('admin_token', 'mock-token');
    localStorage.setItem(
      'admin_profile',
      JSON.stringify({
        id: 1,
        username: 'admin',
        nickname: '系统管理员'
      })
    );
  });
}

async function mockMedicineApis(page: Page, state: ReturnType<typeof createMockState>) {
  await page.route('**/api/admin/medicine-profiles**', async (route) => {
    const method = route.request().method();

    if (method === 'GET') {
      const keyword = new URL(route.request().url()).searchParams.get('keyword')?.trim();
      const list = keyword
        ? state.profiles.filter((item) =>
            `${item.medicineName} ${item.genericName ?? ''} ${item.barcodeOrAlias ?? ''}`.includes(keyword)
          )
        : state.profiles;
      await fulfill(route, list);
      return;
    }

    if (method === 'POST') {
      const payload = route.request().postDataJSON() as MedicineProfile;
      const created: MedicineProfile = {
        ...payload,
        id: state.nextId(),
        username: payload.userId === 12 ? 'blind-user-2' : 'blind-user-1',
        nickname: payload.userId === 12 ? '视障用户乙' : '视障用户甲'
      };
      state.profiles.push(created);
      await fulfill(route, created);
      return;
    }

    await route.fallback();
  });

  await page.route(/.*\/api\/admin\/medicine-profiles\/\d+$/, async (route) => {
    const method = route.request().method();
    const id = Number(route.request().url().split('/').pop());
    const index = state.profiles.findIndex((item) => item.id === id);

    if (method === 'PUT' && index >= 0) {
      const payload = route.request().postDataJSON() as MedicineProfile;
      state.profiles[index] = {
        ...state.profiles[index],
        ...payload
      };
      await fulfill(route, state.profiles[index]);
      return;
    }

    if (method === 'DELETE') {
      if (index >= 0) {
        state.profiles.splice(index, 1);
      }
      await fulfill(route, true);
      return;
    }

    await route.fallback();
  });

  await page.route('**/api/admin/shared-logs**', async (route) => {
    await fulfill(route, state.logs);
  });
}

async function fulfill(route: Route, data: unknown) {
  await route.fulfill({
    status: 200,
    contentType: 'application/json',
    body: JSON.stringify({
      success: true,
      data
    })
  });
}
