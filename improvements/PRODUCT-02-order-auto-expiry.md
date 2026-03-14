# PRODUCT-02 — Order Auto-Expiry (SLA Timeout)

**Priority:** MEDIUM
**Domain:** Product / Business Logic
**Effort:** Medium (2h)

---

## Summary

Orders stuck in `CREATED` state (not yet started) have no timeout mechanism. A branch could forget about an order and it would remain in `CREATED` indefinitely, clogging the KDS and preventing accurate reporting.

---

## Business Rule

> Any order that has been in `CREATED` state for more than 30 minutes without being started should be automatically cancelled. The auto-cancel should restore inventory (see PRODUCT-01).

---

## Files to Modify / Create

| File | Change |
|------|--------|
| `src/main/java/net/leozeballos/FastFood/foodorder/FoodOrderExpiryService.java` | New `@Service` with `@Scheduled` |
| `src/main/java/net/leozeballos/FastFood/foodorder/FoodOrderRepository.java` | Add query for expired orders |
| `src/main/java/net/leozeballos/FastFood/FastFoodApplication.java` | Enable scheduling |
| `src/main/resources/application.properties` | Add expiry duration config |

---

## Implementation Steps

### Step 1 — Enable Scheduling

```java
@SpringBootApplication
@EnableScheduling   // ADD THIS
public class FastFoodApplication { ... }
```

### Step 2 — Add Repository Query for Expired Orders

```java
@Query("SELECT fo FROM FoodOrder fo " +
       "LEFT JOIN FETCH fo.branch b " +
       "LEFT JOIN FETCH fo.foodOrderDetails fod " +
       "LEFT JOIN FETCH fod.item " +
       "WHERE fo.state = 'CREATED' AND fo.creationTimestamp < :cutoff")
List<FoodOrder> findExpiredCreatedOrders(@Param("cutoff") LocalDateTime cutoff);
```

### Step 3 — Create FoodOrderExpiryService

```java
@Service
@RequiredArgsConstructor
@Transactional
public class FoodOrderExpiryService {

    private static final Logger log = LoggerFactory.getLogger(FoodOrderExpiryService.class);

    private final FoodOrderRepository foodOrderRepository;
    private final FoodOrderService foodOrderService;

    @Value("${app.order.expiry-minutes:30}")
    private int expiryMinutes;

    @Scheduled(fixedDelayString = "${app.order.expiry-check-interval-ms:60000}")
    public void cancelExpiredOrders() {
        LocalDateTime cutoff = LocalDateTime.now().minusMinutes(expiryMinutes);
        List<FoodOrder> expired = foodOrderRepository.findExpiredCreatedOrders(cutoff);

        for (FoodOrder order : expired) {
            try {
                log.warn("Auto-cancelling expired order id={}, created={}",
                         order.getId(), order.getCreationTimestamp());
                // null branchId = system-level bypass (no user auth check)
                foodOrderService.cancel(order.getId(), null);
            } catch (Exception e) {
                log.error("Failed to auto-cancel order id={}: {}", order.getId(), e.getMessage());
            }
        }
    }
}
```

### Step 4 — Add Configuration

```properties
app.order.expiry-minutes=30
app.order.expiry-check-interval-ms=60000
```

### Step 5 — Disable Scheduling in Tests

```properties
# application-test.properties
spring.task.scheduling.enabled=false
```

Or use `@MockBean FoodOrderExpiryService` in tests that need the scheduler off.

---

## Test Command

```bash
mvn clean test
```

---

## Acceptance Criteria

- [ ] Orders older than 30 minutes in `CREATED` state are auto-cancelled by the scheduler
- [ ] Auto-cancelled orders restore inventory stock (requires PRODUCT-01)
- [ ] The scheduler interval is configurable via properties
- [ ] Scheduling is disabled in test profile (no unexpected state changes during tests)
- [ ] `mvn clean test` passes with `Failures: 0, Errors: 0`
