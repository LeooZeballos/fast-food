import { test, expect } from '@playwright/test';

test.beforeEach(async ({ page }) => {
  await page.goto('/');
  await page.waitForSelector('#username', { state: 'visible', timeout: 15000 });
  await page.fill('#username', 'admin');
  await page.fill('#password', 'admin');
  await page.click('button[type="submit"]');
  
  // Wait a bit for the transition
  await page.waitForTimeout(3000);
  
  // Try to find ANY admin text in the page
  await expect(page.locator('body')).toContainText(/admin/i, { timeout: 15000 });
});

test('has title', async ({ page }) => {
  await expect(page).toHaveTitle(/FastFood Admin/i);
});

test('can switch main views', async ({ page }) => {
  await page.click('button:has-text("Kitchen")');
  await page.waitForTimeout(2000);
  await expect(page.locator('body')).toContainText(/KDS/i);

  await page.click('button:has-text("Admin")');
  await page.waitForTimeout(2000);
  await expect(page.locator('body')).toContainText(/Admin Panel/i);
  
  await page.click('button:has-text("Take Order")');
  await page.waitForTimeout(2000);
  await expect(page.locator('body')).toContainText(/POS Terminal/i);
});

test('can switch admin tabs', async ({ page }) => {
  await page.click('button:has-text("Admin")');
  await page.waitForTimeout(2000);
  
  await page.click('button[role="tab"]:has-text("Menus")');
  await page.waitForTimeout(1000);
  await expect(page.locator('body')).toContainText(/Bundled item management/i);

  await page.click('button[role="tab"]:has-text("Branches")');
  await page.waitForTimeout(1000);
  await expect(page.locator('body')).toContainText(/Location management/i);
  
  await page.click('button[role="tab"]:has-text("Products")');
  await page.waitForTimeout(1000);
  await expect(page.locator('body')).toContainText(/Individual item management/i);
});

test('can add items to cart', async ({ page }) => {
  await page.click('button:has-text("Take Order")');
  await page.waitForTimeout(2000);
  
  const branchTrigger = page.locator('button').filter({ hasText: /Select location/i }).or(page.locator('button').filter({ hasText: /Service Branch/i })).first();
  
  if (await branchTrigger.isVisible()) {
    await branchTrigger.click();
    await page.waitForSelector('[role="option"]', { timeout: 5000 });
    await page.getByRole('option').first().click();
    await page.waitForTimeout(1000);
  }

  const productCard = page.locator('.group').filter({ hasText: /PRODUCT/i }).first();
  await productCard.click();
  
  await expect(page.locator('body')).toContainText(/Total Due/i);
});
