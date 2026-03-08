import { test, expect } from '@playwright/test';

test('has title', async ({ page }) => {
  await page.goto('/');
  await expect(page).toHaveTitle(/FastFood Admin/i);
});

test('can switch tabs', async ({ page }) => {
  await page.goto('/');
  
  // Click on Products tab
  await page.getByRole('tab', { name: 'Products' }).click();
  await expect(page.getByRole('tabpanel', { name: 'Products' })).toBeVisible();

  // Click on Menus tab
  await page.getByRole('tab', { name: 'Menus' }).click();
  await expect(page.getByRole('tabpanel', { name: 'Menus' })).toBeVisible();
});
