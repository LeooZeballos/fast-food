# UI-03 — Error Toasts for Failed Order Actions

**Priority:** HIGH
**Domain:** Designer / UX
**Effort:** Tiny (30 min)

---

## Summary

Order action mutations in `OrderList.tsx` have `onSuccess` callbacks but **no `onError` handler**. When a mutation fails (network error, invalid state transition, 403), the failure is silently discarded with no user feedback.

---

## Files to Modify

| File | Change |
|------|--------|
| `frontend/src/components/OrderList.tsx` | Add `onError` to `mutationOptions` |
| `frontend/src/locales/en.json` + `es.json` | Add `kitchen.actionError` i18n key |

---

## Implementation Steps

### Step 1 — Add i18n Keys

`en.json` under `"kitchen"`:
```json
"actionError": "Action failed",
"actionErrorDesc": "Could not update the order. Please try again."
```

`es.json`:
```json
"actionError": "Acción fallida",
"actionErrorDesc": "No se pudo actualizar el pedido. Inténtalo de nuevo."
```

### Step 2 — Add onError to mutationOptions

In `OrderList.tsx`:

```tsx
const { t } = useTranslation();

const mutationOptions = {
  onSuccess: () => {
    queryClient.invalidateQueries({ queryKey: ["orders"] });
  },
  onError: (error: any) => {
    toast.error(t('kitchen.actionError'), {
      description: error?.message || t('kitchen.actionErrorDesc'),
    });
  },
};
```

`toast` is already used in `TakeOrder.tsx` — import it from `sonner` the same way:

```tsx
import { toast } from "sonner";
```

The `<Toaster />` is already mounted in `App.tsx`.

---

## Test Command

```bash
cd frontend && pnpm test
```

---

## Acceptance Criteria

- [ ] When an order action fails, a red error toast appears with a descriptive message
- [ ] The error message from the backend (`error.message` set by the RFC 7807 interceptor) is used when available
- [ ] Toast text is translated in both languages
- [ ] `cd frontend && pnpm test` passes
