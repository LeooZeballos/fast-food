package net.leozeballos.FastFood.inventory;

import net.leozeballos.FastFood.branch.Branch;
import net.leozeballos.FastFood.branch.BranchRepository;
import net.leozeballos.FastFood.product.Product;
import net.leozeballos.FastFood.product.ProductRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class InventoryRepositoryTest {

    @Autowired private InventoryRepository underTest;
    @Autowired private BranchRepository branchRepository;
    @Autowired private ProductRepository productRepository;

    @Test
    void findByBranchIdAndItemIdReturnsInventory() {
        // given
        Branch branch = branchRepository.save(Branch.builder().name("Test Branch").build());
        Product product = productRepository.save(new Product("Test Burger", 10.0));
        
        Inventory inventory = Inventory.builder()
                .branch(branch)
                .item(product)
                .stockQuantity(50)
                .isAvailable(true)
                .build();
        underTest.save(inventory);

        // when
        Optional<Inventory> result = underTest.findByBranchIdAndItemId(branch.getId(), product.getId());

        // then
        assertThat(result).isPresent();
        assertThat(result.get().getStockQuantity()).isEqualTo(50);
    }

    @Test
    void findAllByBranchIdReturnsList() {
        // given
        Branch branch = branchRepository.save(Branch.builder().name("Test Branch").build());
        Product product1 = productRepository.save(new Product("Burger 1", 10.0));
        Product product2 = productRepository.save(new Product("Burger 2", 12.0));
        
        underTest.save(Inventory.builder().branch(branch).item(product1).stockQuantity(10).build());
        underTest.save(Inventory.builder().branch(branch).item(product2).stockQuantity(20).build());

        // when
        List<Inventory> result = underTest.findAllByBranchId(branch.getId());

        // then
        assertThat(result).hasSize(2);
    }
}
