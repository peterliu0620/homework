import { test, expect, type Page, type Route } from '@playwright/test';

type FamilyMember = {
  id: number;
  name: string;
  phone?: string | null;
  email?: string | null;
  bindingCount: number;
  createdAt: string;
  updatedAt: string;
};

type FamilyBinding = {
  id: number;
  familyMemberId: number;
  familyName: string;
  familyPhone?: string | null;
  familyEmail?: string | null;
  userId: number;
  username: string;
  nickname: string;
  relationship: string;
  status: string;
  createdAt: string;
};

const now = '2026-04-14T21:00:00';

test('admin can manage family members and bindings', async ({ page }) => {
  const state = createMockState();
  await seedAuth(page);
  await mockFamilyApis(page, state);

  await page.goto('/family-bindings');
  await expect(page.locator('.table-title').first()).toHaveText('家属列表');
  await expect(page.locator('tbody').getByText('李家属').first()).toBeVisible();

  await page.getByRole('button', { name: '新增家属' }).click();
  await page.getByLabel('家属姓名').fill('王家属');
  await page.getByLabel('手机号').fill('13900139002');
  await page.getByLabel('邮箱').fill('wang@example.com');
  await page.getByRole('button', { name: '确 定' }).click();

  await expect(page.locator('tbody').getByText('王家属').first()).toBeVisible();

  await page.getByRole('button', { name: '新增绑定' }).first().click();
  await page.getByRole('combobox', { name: /家属/ }).click();
  await page.getByTitle('王家属 · 13900139002').click();
  await page.getByRole('combobox', { name: /视障人士/ }).click();
  await page.getByTitle('视障用户乙 · blind-user-2').click();
  await page.getByLabel('关系').fill('配偶');
  await page.getByRole('button', { name: '确 定' }).click();

  await expect(page.locator('tbody').getByText('配偶').first()).toBeVisible();

  await page.getByRole('button', { name: '查看绑定' }).nth(1).click();
  await expect(page.locator('.ant-drawer-title')).toHaveText('王家属 的绑定关系');
  await expect(page.locator('.ant-drawer-body').getByText('视障用户乙')).toBeVisible();

  await page.getByRole('button', { name: '解绑' }).click();
  await page.locator('.ant-popover').getByRole('button').last().click();
  await expect(page.locator('.ant-drawer-body').getByText('视障用户乙')).not.toBeVisible();
});

function createMockState() {
  const familyMembers: FamilyMember[] = [
    {
      id: 1,
      name: '李家属',
      phone: '13900139001',
      email: 'li@example.com',
      bindingCount: 1,
      createdAt: now,
      updatedAt: now
    }
  ];

  const bindings: FamilyBinding[] = [
    {
      id: 1,
      familyMemberId: 1,
      familyName: '李家属',
      familyPhone: '13900139001',
      familyEmail: 'li@example.com',
      userId: 11,
      username: 'blind-user-1',
      nickname: '视障用户甲',
      relationship: '女儿',
      status: 'ACTIVE',
      createdAt: now
    }
  ];

  const users = [
    {
      id: 11,
      username: 'blind-user-1',
      nickname: '视障用户甲',
      phone: '13800138001',
      email: 'blind1@example.com',
      status: 1
    },
    {
      id: 12,
      username: 'blind-user-2',
      nickname: '视障用户乙',
      phone: '13800138002',
      email: 'blind2@example.com',
      status: 1
    }
  ];

  let nextFamilyId = 2;
  let nextBindingId = 2;

  return {
    familyMembers,
    bindings,
    users,
    nextFamilyId: () => nextFamilyId++,
    nextBindingId: () => nextBindingId++
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

async function mockFamilyApis(page: Page, state: ReturnType<typeof createMockState>) {
  await page.route('**/api/admin/app-users**', async (route) => {
    await fulfill(route, state.users);
  });

  await page.route('**/api/admin/family-members**', async (route) => {
    const method = route.request().method();
    if (method === 'GET') {
      const keyword = new URL(route.request().url()).searchParams.get('keyword')?.trim();
      const list = keyword
        ? state.familyMembers.filter((item) =>
            `${item.name} ${item.phone ?? ''} ${item.email ?? ''}`.includes(keyword)
          )
        : state.familyMembers;
      await fulfill(route, list);
      return;
    }

    if (method === 'POST') {
      const payload = route.request().postDataJSON() as { name: string; phone?: string; email?: string };
      const created: FamilyMember = {
        id: state.nextFamilyId(),
        name: payload.name,
        phone: payload.phone || '',
        email: payload.email || '',
        bindingCount: 0,
        createdAt: now,
        updatedAt: now
      };
      state.familyMembers.push(created);
      await fulfill(route, created);
      return;
    }

    await route.fallback();
  });

  await page.route(/.*\/api\/admin\/family-members\/\d+$/, async (route) => {
    const method = route.request().method();
    const id = Number(route.request().url().split('/').pop());
    const target = state.familyMembers.find((item) => item.id === id);

    if (method === 'PUT' && target) {
      const payload = route.request().postDataJSON() as { name: string; phone?: string; email?: string };
      target.name = payload.name;
      target.phone = payload.phone || '';
      target.email = payload.email || '';
      target.updatedAt = now;
      syncBindingFamilyInfo(state.bindings, target);
      await fulfill(route, target);
      return;
    }

    if (method === 'GET' && target) {
      await fulfill(route, target);
      return;
    }

    if (method === 'DELETE') {
      const familyIndex = state.familyMembers.findIndex((item) => item.id === id);
      if (familyIndex >= 0) {
        state.familyMembers.splice(familyIndex, 1);
      }
      for (let i = state.bindings.length - 1; i >= 0; i -= 1) {
        if (state.bindings[i].familyMemberId === id) {
          state.bindings.splice(i, 1);
        }
      }
      await fulfill(route, true);
      return;
    }

    await route.fallback();
  });

  await page.route('**/api/admin/family-bindings**', async (route) => {
    await fulfill(route, state.bindings);
  });

  await page.route(/.*\/api\/admin\/family-members\/\d+\/blind-users$/, async (route) => {
    const match = route.request().url().match(/family-members\/(\d+)\/blind-users$/);
    const id = Number(match?.[1]);
    await fulfill(
      route,
      state.bindings.filter((item) => item.familyMemberId === id)
    );
  });

  await page.route(/.*\/api\/admin\/family-members\/\d+\/bindings$/, async (route) => {
    const match = route.request().url().match(/family-members\/(\d+)\/bindings$/);
    const familyMemberId = Number(match?.[1]);
    const payload = route.request().postDataJSON() as { userId: number; relationship: string; status: string };
    const family = state.familyMembers.find((item) => item.id === familyMemberId);
    const user = state.users.find((item) => item.id === payload.userId);
    if (!family || !user) {
      await route.fulfill({ status: 400, contentType: 'application/json', body: JSON.stringify({ success: false, message: 'mock data missing' }) });
      return;
    }
    family.bindingCount += 1;
    const binding: FamilyBinding = {
      id: state.nextBindingId(),
      familyMemberId,
      familyName: family.name,
      familyPhone: family.phone,
      familyEmail: family.email,
      userId: user.id,
      username: user.username,
      nickname: user.nickname,
      relationship: payload.relationship,
      status: payload.status,
      createdAt: now
    };
    state.bindings.push(binding);
    await fulfill(route, binding);
  });

  await page.route(/.*\/api\/admin\/family-members\/\d+\/bindings\/\d+$/, async (route) => {
    const match = route.request().url().match(/family-members\/(\d+)\/bindings\/(\d+)$/);
    const familyMemberId = Number(match?.[1]);
    const userId = Number(match?.[2]);
    const family = state.familyMembers.find((item) => item.id === familyMemberId);
    const index = state.bindings.findIndex((item) => item.familyMemberId === familyMemberId && item.userId === userId);
    if (index >= 0) {
      state.bindings.splice(index, 1);
      if (family && family.bindingCount > 0) {
        family.bindingCount -= 1;
      }
    }
    await fulfill(route, true);
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

function syncBindingFamilyInfo(bindings: FamilyBinding[], family: FamilyMember) {
  bindings.forEach((item) => {
    if (item.familyMemberId === family.id) {
      item.familyName = family.name;
      item.familyPhone = family.phone;
      item.familyEmail = family.email;
    }
  });
}
