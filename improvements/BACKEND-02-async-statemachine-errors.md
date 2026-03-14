# BACKEND-02 — Async State Machine Errors Are Silently Swallowed

**Priority:** HIGH
**Domain:** Backend / Reliability
**Effort:** Medium (1.5h)

---

## Summary

`FoodOrderService.sendEvent()` calls `stateMachine.sendEvent(Mono.just(msg)).subscribe()` with no error handler. If a state transition fails (invalid transition, DB error in interceptor), the error is silently discarded and the caller gets a successful HTTP 200 response with stale state data.

---

## Context

```java
// FoodOrderService.java
private void sendEvent(...) {
    Message<FoodOrderEvent> msg = MessageBuilder.withPayload(event)
            .setHeader(FOOD_ORDER_ID_HEADER, id)
            .build();
    stateMachine.sendEvent(Mono.just(msg)).subscribe();  // ← no error handler
}
```

The `.subscribe()` call without an error consumer means `onError` signals are converted to `onErrorDropped`, which only logs at DEBUG level. This makes it impossible to detect failed transitions in production.

---

## Files to Modify

| File | Change |
|------|--------|
| `src/main/java/net/leozeballos/FastFood/foodorder/FoodOrderService.java` | Add error handling to `sendEvent()` |

---

## Implementation Steps

### Step 1 — Add Logger

```java
private static final Logger log = LoggerFactory.getLogger(FoodOrderService.class);
```

### Step 2 — Replace subscribe() with Block or Error Handler

**Option A — Synchronous block (simpler, acceptable for non-reactive apps):**

```java
private void sendEvent(Long id, StateMachine<FoodOrderState, FoodOrderEvent> sm, FoodOrderEvent event) {
    Message<FoodOrderEvent> msg = MessageBuilder.withPayload(event)
            .setHeader(FOOD_ORDER_ID_HEADER, id)
            .build();

    StateMachineEventResult<FoodOrderState, FoodOrderEvent> result =
            sm.sendEvent(Mono.just(msg)).blockFirst();

    if (result == null || result.getResultType() == StateMachineEventResult.ResultType.DENIED) {
        throw new IllegalStateException(
            "State machine rejected event " + event + " for order " + id +
            ". Current state may not allow this transition.");
    }
}
```

**Option B — Reactive with error consumer:**

```java
sm.sendEvent(Mono.just(msg))
  .doOnNext(r -> {
      if (r.getResultType() == StateMachineEventResult.ResultType.DENIED) {
          log.error("State machine DENIED event {} for order {}", event, id);
      }
  })
  .doOnError(e -> log.error("State machine error for order {}: {}", id, e.getMessage()))
  .subscribe();
```

Option A is recommended — it converts the reactive call to synchronous and makes the error surface to the HTTP layer, which then returns a proper 4xx/5xx to the frontend.

### Step 3 — Update All Callers

`sendEvent()` is a private method called by all transition methods (`startPreparation`, `finishPreparation`, `confirmPayment`, `cancel`, `reject`). They all benefit automatically once `sendEvent()` is fixed.

---

## Test Command

```bash
mvn clean test -Dtest=FoodOrderServiceTest,FoodOrderServiceIntegrationTest
```

---

## Acceptance Criteria

- [ ] Attempting an invalid state transition (e.g., paying an order that's still `CREATED`) throws an exception that propagates to a 4xx HTTP response
- [ ] No state machine errors are silently dropped
- [ ] Valid transitions still work correctly
- [ ] `mvn clean test` passes with `Failures: 0, Errors: 0`
