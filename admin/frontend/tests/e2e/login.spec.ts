import { test, expect } from '@playwright/test';

test('admin can log in and enter dashboard', async ({ page }) => {
  await page.route('**/api/admin/auth/login', async (route) => {
    await route.fulfill({
      status: 200,
      contentType: 'application/json',
      body: JSON.stringify({
        success: true,
        data: {
          id: 1,
          username: 'admin',
          nickname: '系统管理员',
          token: 'mock-token'
        }
      })
    });
  });

  await page.goto('/login');
  await page.getByLabel('密码').fill('123456');
  await page.getByRole('button', { name: '进入管理台' }).click();

  await expect(page).toHaveURL(/\/dashboard$/);
  await expect(page.locator('.header-title')).toHaveText('系统概览');

  const token = await page.evaluate(() => localStorage.getItem('admin_token'));
  expect(token).toBe('mock-token');
});
