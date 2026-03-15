package net.leozeballos.FastFood.foodorder;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FoodOrderExpiryService {

    private final FoodOrderRepository foodOrderRepository;
    private final FoodOrderService foodOrderService;

    @Value("${app.order.expiry-minutes:30}")
    private int expiryMinutes;

    @Scheduled(fixedDelayString = "${app.order.expiry-check-interval-ms:60000}")
    @Transactional
    public void cancelExpiredOrders() {
        LocalDateTime cutoff = LocalDateTime.now().minusMinutes(expiryMinutes);
        List<FoodOrder> expired = foodOrderRepository.findExpiredCreatedOrders(cutoff);

        if (!expired.isEmpty()) {
            log.info("Found {} expired orders to auto-cancel (cutoff: {})", expired.size(), cutoff);
        }

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
