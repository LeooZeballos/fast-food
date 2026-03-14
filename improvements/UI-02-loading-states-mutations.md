# UI-02 — Loading States on Order Action Buttons

**Priority:** HIGH
**Domain:** Designer / UX
**Effort:** Small (1h)

---

## Summary

Order action buttons (Start, Ready, Pay, Cancel, Reject) in `OrderList.tsx` give no visual feedback while a mutation is in flight. The user can click multiple times and sees no indication that the action was received.

---

## Files to Modify

| File | Change |
|------|--------|
| `frontend/src/components/OrderList.tsx` | Add `isPending` state per mutation; disable buttons during loading |

---

## Implementation Steps

### Step 1 — Extract Mutations with isPending

Currently, `OrderList.tsx` uses a shared `mutationOptions` object and creates mutations inline. Restructure to capture individual `isPending` states:

```tsx
const startMutation  = useMutation({ mutationFn: startPreparation,  ...mutationOptions });
const finishMutation = useMutation({ mutationFn: finishPreparation, ...mutationOptions });
const payMutation    = useMutation({ mutationFn: confirmPayment,    ...mutationOptions });
const cancelMutation = useMutation({ mutationFn: cancelOrder,       ...mutationOptions });
const rejectMutation = useMutation({ mutationFn: rejectOrder,       ...mutationOptions });

const actions = {
  start:  startMutation.mutate,
  finish: finishMutation.mutate,
  pay:    payMutation.mutate,
  cancel: cancelMutation.mutate,
  reject: rejectMutation.mutate,
};
```

Pass `isPending` state down via `onAction` or a new `pending` prop to `OrderCard`.

### Step 2 — Update OrderCard Props

```tsx
function OrderCard({ order, onAction, pendingOrderId }: {
  order: FoodOrderDTO;
  onAction: { start: ...; finish: ...; pay: ...; cancel: ...; reject: ...; };
  pendingOrderId: number | null;
}) {
  const isPending = pendingOrderId === order.id;
```

### Step 3 — Add Spinner to Buttons

```tsx
<Button
  className="..."
  onClick={() => order.id && onAction.start(order.id)}
  disabled={isPending}
>
  {isPending
    ? <div className="w-4 h-4 border-2 border-white border-t-transparent rounded-full animate-spin" />
    : <><Play className="mr-2 h-4 w-4" /> {t('kitchen.actions.start')}</>
  }
</Button>
```

### Step 4 — Track Which Order is Pending

In `OrderList`, track the order ID of the currently pending action:

```tsx
const [pendingOrderId, setPendingOrderId] = useState<number | null>(null);

const mutationOptions = {
  onMutate: (id: number) => setPendingOrderId(id),
  onSettled: () => setPendingOrderId(null),
  onSuccess: () => queryClient.invalidateQueries({ queryKey: ["orders"] }),
};
```

---

## Test Command

```bash
cd frontend && pnpm test
```

---

## Acceptance Criteria

- [ ] Clicking an action button shows a spinner while the request is in flight
- [ ] The button is disabled during the pending state (prevents double-submit)
- [ ] After the mutation settles (success or error), the button returns to normal
- [ ] `cd frontend && pnpm test` passes
