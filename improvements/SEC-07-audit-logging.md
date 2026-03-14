# SEC-07 — Missing Audit Logging

**Priority:** MEDIUM
**Domain:** Security / Observability
**Effort:** Medium (2h)

---

## Summary

Critical mutations (delete product, delete branch, cancel order, reject order) are not logged with the identity of the actor. This makes post-incident investigation impossible and is an OWASP A09 finding.

---

## Context

Spring's `ApplicationEventPublisher` can be used to emit domain events that a dedicated audit listener records. This avoids coupling audit concerns into business services.

---

## Files to Create / Modify

| File | Change |
|------|--------|
| `src/main/java/net/leozeballos/FastFood/audit/AuditEvent.java` | New record |
| `src/main/java/net/leozeballos/FastFood/audit/AuditListener.java` | New `@EventListener` |
| `src/main/java/net/leozeballos/FastFood/foodorder/FoodOrderService.java` | Publish events on cancel/reject |
| `src/main/java/net/leozeballos/FastFood/product/ProductService.java` | Publish event on delete |
| `src/main/java/net/leozeballos/FastFood/branch/BranchService.java` | Publish event on delete |

---

## Implementation Steps

### Step 1 — Create AuditEvent Record

```java
package net.leozeballos.FastFood.audit;

import java.time.LocalDateTime;

public record AuditEvent(
    String action,       // e.g. "DELETE_PRODUCT", "CANCEL_ORDER"
    String entityType,
    Long entityId,
    String performedBy,  // username
    LocalDateTime timestamp
) {}
```

### Step 2 — Create AuditListener

```java
@Component
public class AuditListener {
    private static final Logger log = LoggerFactory.getLogger(AuditListener.class);

    @EventListener
    public void onAuditEvent(AuditEvent event) {
        log.warn("[AUDIT] action={} entity={} id={} by={} at={}",
            event.action(), event.entityType(), event.entityId(),
            event.performedBy(), event.timestamp());
    }
}
```

For persistence, replace the logger with a DB insert into an `audit_log` table via a repository.

### Step 3 — Inject Publisher into Services

```java
private final ApplicationEventPublisher eventPublisher;

// In cancel():
eventPublisher.publishEvent(new AuditEvent(
    "CANCEL_ORDER", "FoodOrder", id,
    SecurityContextHolder.getContext().getAuthentication().getName(),
    LocalDateTime.now()
));
```

### Step 4 — Cover Key Actions

Publish events for:
- `CANCEL_ORDER` — `FoodOrderService.cancel()`
- `REJECT_ORDER` — `FoodOrderService.reject()`
- `DELETE_PRODUCT` — `ProductService.deleteById()`
- `DELETE_BRANCH` — `BranchService.deleteById()`
- `DELETE_MENU` — `MenuService.deleteById()`

---

## Test Command

```bash
mvn clean test
```

---

## Acceptance Criteria

- [ ] Cancelling an order produces a WARN-level audit log entry with username and timestamp
- [ ] Deleting a product produces an audit log entry
- [ ] Audit events do not break existing functionality
- [ ] `mvn clean test` passes with `Failures: 0, Errors: 0`
