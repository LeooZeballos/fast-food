# UI-05 — Optimistic Updates for Order Actions

**Priority:** MEDIUM
**Domain:** Designer / UX
**Effort:** Medium (2h)

---

## Summary

With a 5-second polling interval, order state changes take up to 5 seconds to visually reflect after clicking an action button. Using TanStack Query optimistic updates, cards can move to their new column immediately.

---

## Files to Modify

| File | Change |
|------|--------|
| `frontend/src/components/OrderList.tsx` | Add `onMutate` / `onError` / `onSettled` for optimistic updates |

---

## Implementation Steps

### Step 1 — Understand TanStack Query Optimistic Update Pattern

```tsx
const mutation = useMutation({
  mutationFn: startPreparation,
  onMutate: async (orderId) => {
    // 1. Cancel any in-flight refetch to avoid race
    await queryClient.cancelQueries({ queryKey: ["orders", "all"] });
    // 2. Snapshot current data
    const previousOrders = queryClient.getQueryData<FoodOrderDTO[]>(["orders", "all"]);
    // 3. Optimistically update to new state
    queryClient.setQueryData<FoodOrderDTO[]>(["orders", "all"], (old) =>
      old?.map(o => o.id === orderId ? { ...o, formattedState: "Inpreparation" } : o) ?? []
    );
    return { previousOrders };
  },
  onError: (_error, _orderId, context) => {
    // Roll back on error
    if (context?.previousOrders) {
      queryClient.setQueryData(["orders", "all"], context.previousOrders);
    }
  },
  onSettled: () => {
    // Always refetch to sync with server
    queryClient.invalidateQueries({ queryKey: ["orders"] });
  },
});
```

### Step 2 — Apply to Each Action Mutation

Create individual mutations for each action with the appropriate target state:

| Action | Optimistic `formattedState` |
|--------|----------------------------|
| `startPreparation` | `"Inpreparation"` |
| `finishPreparation` | `"Done"` |
| `confirmPayment` | `"Paid"` |
| `cancelOrder` | `"Cancelled"` |
| `rejectOrder` | `"Rejected"` |

### Step 3 — Handle Columns

The columns filter by `formattedState`. After the optimistic update, the card will immediately disappear from the current column and appear in the new one (for `Created → Inpreparation → Done` flow). Terminal states (`Paid`, `Cancelled`, `Rejected`) will remove the card from all visible columns.

### Step 4 — Combine with UI-02 (Loading States)

Use `onMutate` to also set `pendingOrderId` (from UI-02) so the spinner shows while waiting for the real response.

---

## Test Command

```bash
cd frontend && pnpm test
```

---

## Acceptance Criteria

- [ ] After clicking "Start", the order card visually moves from "New Orders" to "In Kitchen" column immediately (< 100ms)
- [ ] If the request fails, the card snaps back to its original column and an error toast is shown (UI-03)
- [ ] After the request settles, the data is refetched to confirm server state
- [ ] `cd frontend && pnpm test` passes
