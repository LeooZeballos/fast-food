# ARCH-03 — Split FoodOrderService Responsibilities

**Priority:** LOW
**Domain:** Architect
**Effort:** Large (4h)

---

## Summary

`FoodOrderService` is 220+ lines mixing three distinct concerns: (1) business logic for orders, (2) inventory management orchestration, and (3) state machine orchestration. This violates Single Responsibility Principle and makes the class hard to test in isolation.

---

## Target Architecture

```
FoodOrderService          — business logic: create, find, delete, branch access
FoodOrderStateMachineService — state machine: build, sendEvent, all transitions
```

`FoodOrderService` will delegate state transitions to `FoodOrderStateMachineService`.

---

## Files to Create / Modify

| File | Change |
|------|--------|
| `src/main/java/net/leozeballos/FastFood/foodorder/FoodOrderStateMachineService.java` | New service |
| `src/main/java/net/leozeballos/FastFood/foodorder/FoodOrderService.java` | Remove state machine code, delegate |
| `src/test/java/net/leozeballos/FastFood/foodorder/FoodOrderStateMachineServiceTest.java` | New test class |

---

## Implementation Steps

### Step 1 — Create FoodOrderStateMachineService

Move `build()`, `sendEvent()`, `startPreparation()`, `finishPreparation()`, `confirmPayment()`, `cancel()`, `reject()`, and `update()` into the new service:

```java
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FoodOrderStateMachineService {

    public static final String FOOD_ORDER_ID_HEADER = "food_order_id";

    private final FoodOrderRepository foodOrderRepository;
    private final StateMachineFactory<FoodOrderState, FoodOrderEvent> stateMachineFactory;
    private final FoodOrderStateChangeInterceptor stateChangeInterceptor;

    @Transactional
    public void startPreparation(FoodOrder order) {
        order.setPreparationStartTimestamp(LocalDateTime.now());
        foodOrderRepository.save(order);
        StateMachine<FoodOrderState, FoodOrderEvent> sm = build(order);
        sendEvent(order.getId(), sm, FoodOrderEvent.STARTPREPARATION);
    }

    // ... other transition methods

    public StateMachine<FoodOrderState, FoodOrderEvent> build(FoodOrder order) {
        StateMachine<FoodOrderState, FoodOrderEvent> sm =
                stateMachineFactory.getStateMachine(Long.toString(order.getId()));
        sm.stopReactively().block();
        sm.getStateMachineAccessor().doWithAllRegions(sma -> {
            sma.addStateMachineInterceptor(stateChangeInterceptor);
            sma.resetStateMachineReactively(
                new DefaultStateMachineContext<>(order.getState(), null, null, null)
            ).block();
        });
        sm.startReactively().block();
        return sm;
    }

    private void sendEvent(Long id, StateMachine<FoodOrderState, FoodOrderEvent> sm,
                           FoodOrderEvent event) {
        // Use the improved blocking version from BACKEND-02
    }
}
```

### Step 2 — Update FoodOrderService

Inject `FoodOrderStateMachineService` and delegate:

```java
@Transactional
public StateMachine<FoodOrderState, FoodOrderEvent> startPreparation(Long id, Long branchId) {
    FoodOrder order = findById(id, branchId);
    return stateMachineService.startPreparation(order);
}
```

### Step 3 — Update FoodOrderRestController

No changes needed — the controller calls `FoodOrderService` which now delegates internally.

### Step 4 — Update Tests

- `FoodOrderServiceTest` should mock `FoodOrderStateMachineService`
- New `FoodOrderStateMachineServiceTest` tests the machine logic directly

---

## Test Command

```bash
mvn clean test -Dtest=FoodOrderServiceTest,FoodOrderStateMachineServiceTest
```

---

## Acceptance Criteria

- [ ] `FoodOrderService` has no `StateMachineFactory`, `StateMachineInterceptor`, or `build()`/`sendEvent()` methods
- [ ] `FoodOrderStateMachineService` encapsulates all state machine operations
- [ ] All existing tests pass without modification
- [ ] `mvn clean test` passes with `Failures: 0, Errors: 0`
