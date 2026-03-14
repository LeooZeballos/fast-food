# UI-01 — Confirmation Dialogs for Destructive Actions

**Priority:** HIGH
**Domain:** Designer / UX
**Effort:** Medium (2h)

---

## Summary

Cancel, Reject, and Delete buttons have no confirmation prompt. A single mis-click on a busy kitchen display can irreversibly cancel a paid order or delete a product with active inventory.

---

## Files to Modify

| File | Change |
|------|--------|
| `frontend/src/components/OrderList.tsx` | Wrap Cancel/Reject buttons in AlertDialog |
| `frontend/src/components/AdminPanel.tsx` (or ProductList/MenuList/BranchList) | Wrap Delete buttons in AlertDialog |
| `frontend/src/locales/en.json` + `es.json` | Add i18n keys for confirmation text |

---

## Implementation Steps

### Step 1 — Add i18n Keys

`en.json` under `"common"`:
```json
"confirmTitle": "Are you sure?",
"confirmCancel": "This action cannot be undone.",
"confirmDelete": "This will permanently delete the item.",
"confirmCancelOrder": "This will cancel the order and restore inventory.",
"confirmRejectOrder": "This will reject the order and restore inventory.",
"proceed": "Proceed",
"goBack": "Go back"
```

`es.json` — translate all above.

### Step 2 — Create Reusable ConfirmDialog Component

```tsx
// frontend/src/components/ui/confirm-dialog.tsx
import { AlertDialog, AlertDialogAction, AlertDialogCancel,
         AlertDialogContent, AlertDialogDescription,
         AlertDialogFooter, AlertDialogHeader, AlertDialogTitle,
         AlertDialogTrigger } from "@/components/ui/alert-dialog";
import { useTranslation } from "react-i18next";

interface ConfirmDialogProps {
  trigger: React.ReactNode;
  title: string;
  description: string;
  onConfirm: () => void;
  destructive?: boolean;
}

export function ConfirmDialog({ trigger, title, description, onConfirm, destructive }: ConfirmDialogProps) {
  const { t } = useTranslation();
  return (
    <AlertDialog>
      <AlertDialogTrigger asChild>{trigger}</AlertDialogTrigger>
      <AlertDialogContent>
        <AlertDialogHeader>
          <AlertDialogTitle>{title}</AlertDialogTitle>
          <AlertDialogDescription>{description}</AlertDialogDescription>
        </AlertDialogHeader>
        <AlertDialogFooter>
          <AlertDialogCancel>{t('common.goBack')}</AlertDialogCancel>
          <AlertDialogAction
            onClick={onConfirm}
            className={destructive ? "bg-destructive hover:bg-destructive/90" : ""}
          >
            {t('common.proceed')}
          </AlertDialogAction>
        </AlertDialogFooter>
      </AlertDialogContent>
    </AlertDialog>
  );
}
```

### Step 3 — Wrap Cancel/Reject in OrderList.tsx

```tsx
<ConfirmDialog
  trigger={
    <Button variant="ghost" className="w-11 h-11 ...">
      <XCircle className="h-5 w-5" />
    </Button>
  }
  title={t('common.confirmTitle')}
  description={t('common.confirmCancelOrder')}
  onConfirm={() => order.id && onAction.cancel(order.id)}
  destructive
/>
```

Repeat for the Reject button with `confirmRejectOrder`.

### Step 4 — Wrap Delete Buttons in Admin Components

Find all `onClick` delete handlers in `ProductList.tsx`, `MenuList.tsx`, `BranchList.tsx` and wrap them with `<ConfirmDialog>`.

---

## Test Command

```bash
cd frontend && pnpm test
```

---

## Acceptance Criteria

- [ ] Clicking Cancel on an order card opens a confirmation dialog before executing
- [ ] Clicking Reject on an order card opens a confirmation dialog before executing
- [ ] Clicking Delete in Admin panel opens a confirmation dialog before executing
- [ ] Dismissing the dialog (Go back) does not trigger the action
- [ ] All dialog text is translated in both `en.json` and `es.json`
- [ ] `cd frontend && pnpm test` passes
