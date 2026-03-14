package net.leozeballos.FastFood.foodorder;

import net.leozeballos.FastFood.branch.Branch;
import net.leozeballos.FastFood.branch.BranchRepository;
import net.leozeballos.FastFood.inventory.Inventory;
import net.leozeballos.FastFood.inventory.InventoryRepository;
import net.leozeballos.FastFood.product.Product;
import net.leozeballos.FastFood.product.ProductRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application.properties")
@Transactional
class FoodOrderServiceIntegrationTest {

    @Autowired private FoodOrderService underTest;
    @Autowired private InventoryRepository inventoryRepository;
    @Autowired private BranchRepository branchRepository;
    @Autowired private ProductRepository productRepository;

    @Test
    void createOrderDecrementsInventoryStock() {
        // given
        Branch branch = branchRepository.save(Branch.builder().name("Test Branch").build());
        Product product = productRepository.save(new Product("Test Burger", 10.0));
        
        inventoryRepository.save(Inventory.builder()
                .branch(branch)
                .item(product)
                .stockQuantity(100)
                .isAvailable(true)
                .build());

        CreateOrderDTO.OrderDetailItemDTO itemDTO = new CreateOrderDTO.OrderDetailItemDTO(product.getId(), 5);
        CreateOrderDTO createOrderDTO = new CreateOrderDTO(branch.getId(), List.of(itemDTO));

        // when
        underTest.createOrder(createOrderDTO);

        // then
        Inventory updatedInventory = inventoryRepository.findByBranchIdAndItemId(branch.getId(), product.getId()).orElseThrow();
        assertThat(updatedInventory.getStockQuantity()).isEqualTo(95);
    }

    @Test
    void createOrderThrowsExceptionWhenNoStock() {
        // given
        Branch branch = branchRepository.save(Branch.builder().name("Test Branch").build());
        Product product = productRepository.save(new Product("Test Burger", 10.0));
        
        inventoryRepository.save(Inventory.builder()
                .branch(branch)
                .item(product)
                .stockQuantity(2)
                .isAvailable(true)
                .build());

        CreateOrderDTO.OrderDetailItemDTO itemDTO = new CreateOrderDTO.OrderDetailItemDTO(product.getId(), 5);
        CreateOrderDTO createOrderDTO = new CreateOrderDTO(branch.getId(), List.of(itemDTO));

        // when & then
        assertThatThrownBy(() -> underTest.createOrder(createOrderDTO))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Insufficient stock");
        
        // Verify stock was NOT decremented due to rollback or early check
        Inventory updatedInventory = inventoryRepository.findByBranchIdAndItemId(branch.getId(), product.getId()).orElseThrow();
        assertThat(updatedInventory.getStockQuantity()).isEqualTo(2);
    }
}
