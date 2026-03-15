# TEST-04 — State Machine Transition Tests

**Priority:** MEDIUM
**Domain:** Tester
**Effort:** Medium (2h)

---

## Summary

`StateMachineConfig` defines which transitions are valid, but there are no tests verifying that valid transitions work and that invalid ones (e.g., `PAID → CREATED`) are properly rejected.

---

## Files to Create / Modify

| File | Change |
|------|--------|
| `src/test/java/net/leozeballos/FastFood/foodorderstatemachine/StateMachineTransitionTest.java` | New integration test |

---

## Implementation Steps

### Step 1 — Create Test Class

Use `@SpringBootTest` with the `test` profile to get the real state machine factory:

```java
@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(properties = {
    "SPRING_DATASOURCE_URL=jdbc:h2:mem:testdb",
    "SPRING_DATASOURCE_USERNAME=sa",
    "SPRING_DATASOURCE_PASSWORD=",
    "SPRING_DATASOURCE_DRIVER_CLASS_NAME=org.h2.Driver"
})
class StateMachineTransitionTest {

    @Autowired
    StateMachineFactory<FoodOrderState, FoodOrderEvent> stateMachineFactory;
```

### Step 2 — Helper to Build a Machine at a Given State

```java
private StateMachine<FoodOrderState, FoodOrderEvent> buildAt(FoodOrderState state) {
    StateMachine<FoodOrderState, FoodOrderEvent> sm = stateMachineFactory.getStateMachine();
    sm.stopReactively().block();
    sm.getStateMachineAccessor().doWithAllRegions(sma ->
        sma.resetStateMachineReactively(
            new DefaultStateMachineContext<>(state, null, null, null)
        ).block()
    );
    sm.startReactively().block();
    return sm;
}
```

### Step 3 — Test Valid Transitions

```java
@Test void created_to_inpreparation_via_startpreparation() {
    var sm = buildAt(FoodOrderState.CREATED);
    sendEvent(sm, FoodOrderEvent.STARTPREPARATION);
    assertEquals(FoodOrderState.INPREPARATION, sm.getState().getId());
}

@Test void inpreparation_to_done_via_finishpreparation() { ... }
@Test void done_to_paid_via_confirmpayment() { ... }
@Test void created_to_cancelled_via_cancel() { ... }
@Test void inpreparation_to_cancelled_via_cancel() { ... }
@Test void done_to_rejected_via_reject() { ... }
```

### Step 4 — Test Invalid Transitions (Guard Rejection)

```java
@Test void paid_cannot_transition_to_anything() {
    var sm = buildAt(FoodOrderState.PAID);
    // Sending CANCEL to a PAID order should be DENIED
    var result = sm.sendEvent(Mono.just(
        MessageBuilder.withPayload(FoodOrderEvent.CANCEL).build()
    )).blockFirst();
    assertEquals(StateMachineEventResult.ResultType.DENIED, result.getResultType());
    assertEquals(FoodOrderState.PAID, sm.getState().getId());  // state unchanged
}

@Test void created_cannot_finish_preparation() {
    var sm = buildAt(FoodOrderState.CREATED);
    var result = sm.sendEvent(Mono.just(
        MessageBuilder.withPayload(FoodOrderEvent.FINISHPREPARATION).build()
    )).blockFirst();
    assertEquals(StateMachineEventResult.ResultType.DENIED, result.getResultType());
}
```

---

## Test Command

```bash
mvn clean test -Dtest=StateMachineTransitionTest
```

---

## Acceptance Criteria

- [ ] All 6 valid transitions are tested and pass
- [ ] All invalid transitions from terminal states (`PAID`, `CANCELLED`, `REJECTED`) are tested and return `DENIED`
- [ ] Invalid skip transitions (e.g., `CREATED → DONE` directly) are tested and denied
- [ ] `mvn clean test` passes with `Failures: 0, Errors: 0`
