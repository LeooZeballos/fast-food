package net.leozeballos.FastFood.inventory;

import net.leozeballos.FastFood.product.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InventoryServiceTest {

    @Mock private InventoryRepository inventoryRepository;
    private InventoryService underTest;

    @BeforeEach
    void setUp() {
        underTest = new InventoryService(inventoryRepository);
    }

    @Test
    void isItemAvailableReturnsTrueWhenStockIsSufficient() {
        // given
        Long branchId = 1L;
        Long itemId = 2L;
        int quantity = 5;
        Inventory inventory = Inventory.builder()
                .stockQuantity(10)
                .isAvailable(true)
                .build();
        when(inventoryRepository.findByBranchIdAndItemId(branchId, itemId))
                .thenReturn(Optional.of(inventory));

        // when
        boolean available = underTest.isItemAvailable(branchId, itemId, quantity);

        // then
        assertThat(available).isTrue();
    }

    @Test
    void isItemAvailableReturnsFalseWhenStockIsInsufficient() {
        // given
        Long branchId = 1L;
        Long itemId = 2L;
        int quantity = 15;
        Inventory inventory = Inventory.builder()
                .stockQuantity(10)
                .isAvailable(true)
                .build();
        when(inventoryRepository.findByBranchIdAndItemId(branchId, itemId))
                .thenReturn(Optional.of(inventory));

        // when
        boolean available = underTest.isItemAvailable(branchId, itemId, quantity);

        // then
        assertThat(available).isFalse();
    }

    @Test
    void atomicDecrementOrThrowReducesQuantity() {
        // given
        Long branchId = 1L;
        Long itemId = 2L;
        int quantity = 5;
        when(inventoryRepository.atomicDecrement(branchId, itemId, quantity))
                .thenReturn(1);

        // when
        underTest.atomicDecrementOrThrow(branchId, itemId, quantity);

        // then
        verify(inventoryRepository).atomicDecrement(branchId, itemId, quantity);
    }

    @Test
    void atomicDecrementOrThrowThrowsExceptionWhenAffectedIsZero() {
        // given
        Long branchId = 1L;
        Long itemId = 2L;
        int quantity = 15;
        when(inventoryRepository.atomicDecrement(branchId, itemId, quantity))
                .thenReturn(0);

        // when & then
        assertThatThrownBy(() -> underTest.atomicDecrementOrThrow(branchId, itemId, quantity))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Insufficient stock");
    }

    @Test
    void decrementStockCallsAtomicDecrement() {
        // given
        Long branchId = 1L;
        Long itemId = 2L;
        int quantity = 5;
        when(inventoryRepository.atomicDecrement(branchId, itemId, quantity))
                .thenReturn(1);

        // when
        underTest.decrementStock(branchId, itemId, quantity);

        // then
        verify(inventoryRepository).atomicDecrement(branchId, itemId, quantity);
    }
}
