# TEST-05 — TakeOrder Component Tests

**Priority:** HIGH
**Domain:** Tester / Frontend
**Effort:** Medium (2h)

---

## Summary

`TakeOrder.tsx` is the most complex frontend component and has zero tests. It contains cart logic, branch selection, availability checks, and order submission — all critical paths with no coverage.

---

## Files to Create

| File | Change |
|------|--------|
| `frontend/src/components/__tests__/TakeOrder.test.tsx` | New test file |

---

## Implementation Steps

### Step 1 — Read Existing Test Files

Read `frontend/src/components/__tests__/OrderList.test.tsx` and `ProductList.test.tsx` to understand the test setup pattern (mocking, `renderWithProviders`, etc.).

### Step 2 — Setup Mocks

```tsx
import { renderWithProviders } from '@/test/test-utils';
import { TakeOrder } from '../TakeOrder';
import { server } from '@/test/server'; // if MSW is configured
import { http, HttpResponse } from 'msw';

// Mock API responses
beforeEach(() => {
  server.use(
    http.get('*/api/v1/branches', () => HttpResponse.json([
      { id: 1, name: 'Branch A', street: '123 Main St', city: 'Springfield' }
    ])),
    http.get('*/api/v1/products', () => HttpResponse.json([
      { id: 1, name: 'Burger', price: 5.99, active: true, formattedPrice: '5,99 €' }
    ])),
    http.get('*/api/v1/menus', () => HttpResponse.json([])),
  );
});
```

### Step 3 — Test: Cart Add/Remove

```tsx
test('adds item to cart when product card is clicked', async () => {
  renderWithProviders(<TakeOrder />);

  // Wait for products to load
  await screen.findByText('Burger');

  // Click product card
  fireEvent.click(screen.getAllByTestId('product-card')[0]);

  // Cart should show the item
  expect(screen.getByText('1 × 5,99 €')).toBeInTheDocument();
});

test('removes item from cart when trash button is clicked', async () => {
  // ... add item first, then remove
});
```

### Step 4 — Test: Checkout Disabled Without Branch

```tsx
test('place order button is disabled when no branch is selected', async () => {
  renderWithProviders(<TakeOrder />);
  await screen.findByText('Burger');

  const placeOrderBtn = screen.getByTestId('place-order-button');
  expect(placeOrderBtn).toBeDisabled();
});
```

### Step 5 — Test: Out-of-Stock Items Not Clickable

```tsx
test('out-of-stock items cannot be added to cart', async () => {
  server.use(
    http.get('*/api/v1/inventory/branch/*', () => HttpResponse.json([
      { id: 1, stockQuantity: 0, isAvailable: false, item: { id: 1, name: 'Burger' } }
    ]))
  );

  // Select a branch to trigger inventory check
  // Then try clicking the product card
  // Verify cart remains empty
});
```

### Step 6 — Test: Checkout Submits Correct Payload

```tsx
test('checkout sends correct order payload', async () => {
  let capturedBody: any;
  server.use(
    http.post('*/api/v1/orders', async ({ request }) => {
      capturedBody = await request.json();
      return HttpResponse.json({ id: 1, formattedState: 'Created', ... });
    })
  );

  // Add item, select branch, click checkout
  // Assert capturedBody.branchId === 1
  // Assert capturedBody.items[0].itemId === 1
});
```

---

## Test Command

```bash
cd frontend && pnpm test TakeOrder
```

---

## Acceptance Criteria

- [ ] At least 5 unit tests covering: add to cart, remove from cart, disable without branch, out-of-stock not clickable, checkout submits correct payload
- [ ] All tests use the existing `renderWithProviders` test utility
- [ ] `cd frontend && pnpm test` passes with no failures
