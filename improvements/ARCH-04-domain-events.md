# ARCH-04 — Domain Events for Loose Coupling

**Priority:** LOW
**Domain:** Architect
**Effort:** Large (4h)

---

## Summary

Order state changes (created, cancelled, paid) are handled inline in service methods. Any cross-cutting concern (audit logging, email notifications, analytics) requires modifying `FoodOrderService`. Domain events decouple these concerns.

---

## Context

Spring's `ApplicationEventPublisher` allows services to emit events that any number of `@EventListener` beans can react to independently. This is the foundation for adding SEC-07 (audit logging), PRODUCT-02 (auto-expiry notifications), and future features like email receipts.

---

## Files to Create / Modify

| File | Change |
|------|--------|
| `src/main/java/net/leozeballos/FastFood/foodorder/events/` | New package for event records |
| `src/main/java/net/leozeballos/FastFood/foodorder/events/OrderCreatedEvent.java` | New record |
| `src/main/java/net/leozeballos/FastFood/foodorder/events/OrderStateChangedEvent.java` | New record |
| `src/main/java/net/leozeballos/FastFood/foodorder/FoodOrderService.java` | Publish events |
| `src/main/java/net/leozeballos/FastFood/audit/AuditListener.java` | Listen for events (from SEC-07) |

---

## Implementation Steps

### Step 1 — Create Event Records

```java
// OrderCreatedEvent.java
public record OrderCreatedEvent(
    Long orderId,
    Long branchId,
    double total,
    String createdBy,
    LocalDateTime timestamp
) {}

// OrderStateChangedEvent.java
public record OrderStateChangedEvent(
    Long orderId,
    FoodOrderState previousState,
    FoodOrderState newState,
    String changedBy,
    LocalDateTime timestamp
) {}
```

### Step 2 — Inject Publisher into FoodOrderService

```java
private final ApplicationEventPublisher eventPublisher;
```

### Step 3 — Publish on Create

```java
@Transactional
public FoodOrder createOrder(CreateOrderDTO dto) {
    // ... existing logic ...
    FoodOrder saved = foodOrderRepository.save(order);

    eventPublisher.publishEvent(new OrderCreatedEvent(
        saved.getId(),
        saved.getBranch().getId(),
        saved.calculateTotal(),
        SecurityContextHolder.getContext().getAuthentication().getName(),
        LocalDateTime.now()
    ));

    return saved;
}
```

### Step 4 — Publish on State Change

After each state transition (cancel, reject, confirmPayment):

```java
eventPublisher.publishEvent(new OrderStateChangedEvent(
    order.getId(),
    previousState,  // capture before sendEvent
    newState,       // the target state
    currentUsername(),
    LocalDateTime.now()
));
```

### Step 5 — Update AuditListener to Use Events

Replace direct method calls from SEC-07 with event listeners:

```java
@Component
public class AuditListener {
    @EventListener
    public void onOrderCreated(OrderCreatedEvent event) {
        log.warn("[AUDIT] ORDER_CREATED id={} branch={} total={} by={} at={}",
            event.orderId(), event.branchId(), event.total(),
            event.createdBy(), event.timestamp());
    }

    @EventListener
    public void onOrderStateChanged(OrderStateChangedEvent event) {
        log.warn("[AUDIT] ORDER_STATE_CHANGE id={} {}→{} by={} at={}",
            event.orderId(), event.previousState(), event.newState(),
            event.changedBy(), event.timestamp());
    }
}
```

---

## Test Command

```bash
mvn clean test
```

---

## Acceptance Criteria

- [ ] Creating an order publishes `OrderCreatedEvent`
- [ ] Cancelling/rejecting/paying an order publishes `OrderStateChangedEvent`
- [ ] `AuditListener` (from SEC-07) reacts to events and logs audit entries
- [ ] Events do not affect the transactional behavior of the service (events are published after commit if using `@TransactionalEventListener`)
- [ ] `mvn clean test` passes with `Failures: 0, Errors: 0`
