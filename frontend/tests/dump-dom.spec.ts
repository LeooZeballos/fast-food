import { test } from '@playwright/test';
import * as fs from 'fs';

test('dump dom', async ({ page }) => {
  await page.goto('http://localhost:4000');
  await page.waitForTimeout(2000);
  const html = await page.content();
  fs.writeFileSync('dom_dump.html', html);
});
