package net.leozeballos.FastFood.inventory;

import net.leozeballos.FastFood.branch.Branch;
import net.leozeballos.FastFood.branch.BranchRepository;
import net.leozeballos.FastFood.product.Product;
import net.leozeballos.FastFood.product.ProductRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application.properties")
class InventoryServiceIntegrationTest {

    @Autowired private InventoryService underTest;
    @Autowired private InventoryRepository inventoryRepository;
    @Autowired private BranchRepository branchRepository;
    @Autowired private ProductRepository productRepository;

    @Test
    void atomicDecrementPreventsOversellingUnderConcurrency() throws InterruptedException {
        // given
        Branch branch = branchRepository.save(Branch.builder().name("Concurrency Branch").build());
        Product product = productRepository.save(new Product("Concurrency Burger", 10.0));
        
        // Initial stock = 1
        inventoryRepository.save(Inventory.builder()
                .branch(branch)
                .item(product)
                .stockQuantity(1)
                .isAvailable(true)
                .build());

        int numberOfThreads = 10;
        ExecutorService service = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch latch = new CountDownLatch(1);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);

        // when
        for (int i = 0; i < numberOfThreads; i++) {
            service.execute(() -> {
                try {
                    latch.await();
                    underTest.atomicDecrementOrThrow(branch.getId(), product.getId(), 1);
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    failureCount.incrementAndGet();
                }
            });
        }

        latch.countDown(); // Start all threads at once
        service.shutdown();
        service.awaitTermination(10, java.util.concurrent.TimeUnit.SECONDS);

        // then
        // Exactly one should succeed, 9 should fail
        assertThat(successCount.get()).isEqualTo(1);
        assertThat(failureCount.get()).isEqualTo(numberOfThreads - 1);

        Inventory finalInventory = inventoryRepository.findByBranchIdAndItemId(branch.getId(), product.getId()).orElseThrow();
        assertThat(finalInventory.getStockQuantity()).isEqualTo(0);
    }
}
