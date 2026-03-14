# UI-04 — Mobile-Responsive Kanban Layout

**Priority:** MEDIUM
**Domain:** Designer / UX
**Effort:** Medium (1.5h)

---

## Summary

The KDS Kanban board uses a fixed 3-column `grid-cols-3` layout that overflows on tablets and phones. Kitchen staff using tablets (the primary KDS device) see truncated or overflow content.

---

## Files to Modify

| File | Change |
|------|--------|
| `frontend/src/components/OrderList.tsx` | Make Kanban responsive with column tabs on mobile |
| `frontend/src/locales/en.json` + `es.json` | Ensure column labels are already translated (they are) |

---

## Implementation Steps

### Option A — Horizontal Scroll on Mobile (Simple)

Replace the `grid-cols-3` wrapper with a scrollable flex container on small screens:

```tsx
<div className="flex md:grid md:grid-cols-3 gap-8 items-start overflow-x-auto pb-4 snap-x snap-mandatory">
  {columns.map(column => (
    <div key={column.id} className="flex flex-col gap-6 min-w-[300px] snap-start md:min-w-0">
      {/* column content */}
    </div>
  ))}
</div>
```

### Option B — Tab Switcher on Mobile (Better UX)

Add a `Tabs` component visible only on small screens, hidden on `md+`:

```tsx
// Small screen: tab switcher
<div className="md:hidden mb-4">
  <Tabs value={activeColumn} onValueChange={setActiveColumn}>
    <TabsList className="w-full">
      {columns.map(col => (
        <TabsTrigger key={col.id} value={col.id} className="flex-1">
          {col.label}
          <Badge className="ml-2">{orders?.filter(o => o.formattedState === col.id).length || 0}</Badge>
        </TabsTrigger>
      ))}
    </TabsList>
  </Tabs>
</div>

// Large screen: full grid (existing layout)
<div className="hidden md:grid grid-cols-3 gap-8 items-start">
  {/* all columns */}
</div>

// Mobile: only active column
<div className="md:hidden">
  {columns.filter(c => c.id === activeColumn).map(column => (
    // column content
  ))}
</div>
```

**Option B is recommended** — it prevents cognitive overload on small screens.

### State for Active Column

```tsx
const [activeColumn, setActiveColumn] = useState<string>("Created");
```

---

## Test Command

```bash
cd frontend && pnpm test
```

---

## Acceptance Criteria

- [ ] On screens < 768px (`md` breakpoint), only one Kanban column is shown at a time
- [ ] Tab switcher shows order count badge for each column
- [ ] On screens >= 768px, all three columns display side by side (existing behavior unchanged)
- [ ] `cd frontend && pnpm test` passes
