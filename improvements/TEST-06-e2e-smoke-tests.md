# TEST-06 — E2E Smoke Tests (Playwright)

**Priority:** MEDIUM
**Domain:** Tester / Frontend
**Effort:** Medium (3h)

---

## Summary

Playwright is configured (`frontend/playwright.config.ts`) but no E2E tests exist. The `frontend/tests/example.spec.ts` file is the default placeholder. Add smoke tests for the two critical user flows.

---

## Files to Modify / Create

| File | Change |
|------|--------|
| `frontend/tests/example.spec.ts` | Replace placeholder with real smoke test |
| `frontend/tests/staff-order-flow.spec.ts` | Staff: place order → verify in KDS |
| `frontend/tests/admin-product-flow.spec.ts` | Admin: create product → verify in POS |

---

## Prerequisites

- Services must be running: `./start-all.sh`
- Default test credentials available (admin/admin, staff@branch1)
- `playwright.config.ts` must point to `http://localhost:4000` (or `5173` for dev)

---

## Implementation Steps

### Step 1 — Verify playwright.config.ts

Check that `baseURL` is set:
```typescript
use: {
  baseURL: 'http://localhost:4000',
},
```

And that the webServer is configured or services are pre-started.

### Step 2 — Staff Order Flow

```typescript
// frontend/tests/staff-order-flow.spec.ts
import { test, expect } from '@playwright/test';

test.describe('Staff: Place an Order', () => {
  test.beforeEach(async ({ page }) => {
    // Login as staff
    await page.goto('/');
    await page.fill('input[name="username"]', 'staff');
    await page.fill('input[name="password"]', 'staff');
    await page.click('button[type="submit"]');
    await expect(page).toHaveURL('/');
  });

  test('can place an order and see it in KDS', async ({ page }) => {
    // Select branch
    await page.getByTestId('branch-select').click();
    await page.getByRole('option').first().click();

    // Add first available product to cart
    const cards = page.getByTestId('product-card');
    await cards.first().click();

    // Place order
    await page.getByTestId('place-order-button').click();

    // Verify success toast
    await expect(page.getByText('Order placed successfully')).toBeVisible();

    // Navigate to KDS
    await page.getByText('Kitchen').click();

    // Verify order appears in New Orders column
    await expect(page.getByText('New Orders')).toBeVisible();
    // Order card should appear (check for order ID or item name)
  });
});
```

### Step 3 — Admin Product Flow

```typescript
// frontend/tests/admin-product-flow.spec.ts
test.describe('Admin: Create a Product', () => {
  test.beforeEach(async ({ page }) => {
    await page.goto('/');
    await page.fill('input[name="username"]', 'admin');
    await page.fill('input[name="password"]', 'admin');
    await page.click('button[type="submit"]');
  });

  test('can create a product and see it in POS', async ({ page }) => {
    // Go to admin panel
    await page.getByText('Admin').click();

    // Click New Product
    await page.getByText('New Product').click();

    // Fill form
    await page.fill('input[placeholder*="Burger"]', 'Test E2E Burger');
    await page.fill('input[placeholder*="price"]', '9.99');
    await page.click('button[type="submit"]');

    // Verify success
    await expect(page.getByText('Product created')).toBeVisible();

    // Go to POS and verify
    await page.getByText('Take Order').click();
    await expect(page.getByText('Test E2E Burger')).toBeVisible();
  });
});
```

---

## Test Command

```bash
cd frontend && pnpm test:e2e
```

---

## Acceptance Criteria

- [ ] Staff order flow test: login → place order → order visible in KDS
- [ ] Admin product flow test: login → create product → visible in POS
- [ ] Tests are isolated (clean up created test data after each run if possible)
- [ ] `pnpm test:e2e` passes with no failures
