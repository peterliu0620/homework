import { test, expect, type Page, type Route } from '@playwright/test';

type AppUser = {
  id: number;
  username: string;
  nickname: string;
  phone?: string | null;
  email?: string | null;
  status: number;
  lastLoginAt?: string | null;
  createdAt?: string | null;
};

test('admin can create, edit and delete app users', async ({ page }) => {
  const state = createMockState();
  await seedAuth(page);
  await mockAppUserApis(page, state);

  await page.goto('/app-users');
  await expect(page.locator('.table-title')).toHaveText('用户列表');
  await expect(page.locator('tbody').getByText('blind-user-1')).toBeVisible();

  await page.getByRole('button', { name: '新增用户' }).click();
  await page.getByLabel('用户名').fill('blind-user-2');
  await page.getByLabel('密码').fill('123456');
  await page.getByLabel('昵称').fill('视障用户乙');
  await page.getByLabel('手机号').fill('13800138002');
  await page.getByLabel('邮箱').fill('blind2@example.com');
  await page.getByRole('radio', { name: '启用' }).check();
  await page.getByRole('button', { name: '确 定' }).click();

  await expect(page.locator('tbody').getByText('blind-user-2')).toBeVisible();

  await page.getByRole('button', { name: '编辑' }).nth(1).click();
  await page.getByLabel('昵称').fill('视障用户乙-更新');
  await page.getByLabel('手机号').fill('13900139002');
  await page.getByRole('radio', { name: '禁用' }).check();
  await page.getByRole('button', { name: '确 定' }).click();

  await expect(page.locator('tbody').getByText('视障用户乙-更新')).toBeVisible();
  await expect(page.locator('tbody').getByText('禁用')).toBeVisible();

  await page.getByRole('button', { name: '删除' }).nth(1).click();
  await page.locator('.ant-popover').getByRole('button').last().click();
  await expect(page.locator('tbody').getByText('blind-user-2')).not.toBeVisible();
});

function createMockState() {
  const users: AppUser[] = [
    {
      id: 1,
      username: 'blind-user-1',
      nickname: '视障用户甲',
      phone: '13800138001',
      email: 'blind1@example.com',
      status: 1,
      lastLoginAt: '2026-04-14T21:00:00',
      createdAt: '2026-04-14T21:00:00'
    }
  ];
  let nextId = 2;

  return {
    users,
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

async function mockAppUserApis(page: Page, state: ReturnType<typeof createMockState>) {
  await page.route('**/api/admin/app-users**', async (route) => {
    const method = route.request().method();

    if (method === 'GET') {
      const keyword = new URL(route.request().url()).searchParams.get('keyword')?.trim();
      const list = keyword
        ? state.users.filter((item) =>
            `${item.username} ${item.nickname} ${item.phone ?? ''} ${item.email ?? ''}`.includes(keyword)
          )
        : state.users;
      await fulfill(route, list);
      return;
    }

    if (method === 'POST') {
      const payload = route.request().postDataJSON() as {
        username?: string;
        password: string;
        nickname: string;
        phone: string;
        email: string;
        status: number;
      };
      const created: AppUser = {
        id: state.nextId(),
        username: payload.username || `user-${Date.now()}`,
        nickname: payload.nickname,
        phone: payload.phone,
        email: payload.email,
        status: payload.status,
        createdAt: '2026-04-14T21:00:00',
        lastLoginAt: null
      };
      state.users.push(created);
      await fulfill(route, created);
      return;
    }

    await route.fallback();
  });

  await page.route(/.*\/api\/admin\/app-users\/\d+$/, async (route) => {
    const method = route.request().method();
    const id = Number(route.request().url().split('/').pop());
    const index = state.users.findIndex((item) => item.id === id);

    if (method === 'PUT' && index >= 0) {
      const payload = route.request().postDataJSON() as {
        nickname: string;
        phone: string;
        email: string;
        status: number;
      };
      state.users[index] = {
        ...state.users[index],
        nickname: payload.nickname,
        phone: payload.phone,
        email: payload.email,
        status: payload.status
      };
      await fulfill(route, state.users[index]);
      return;
    }

    if (method === 'DELETE') {
      if (index >= 0) {
        state.users.splice(index, 1);
      }
      await fulfill(route, true);
      return;
    }

    await route.fallback();
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
