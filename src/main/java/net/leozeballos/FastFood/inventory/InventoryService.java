package net.leozeballos.FastFood.inventory;

import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InventoryService {

    private final InventoryRepository inventoryRepository;

    /**
     * Check if an item is available at a branch.
     * @param branchId ID of the branch.
     * @param itemId ID of the item.
     * @param quantity Quantity requested.
     * @return boolean True if available.
     */
    public boolean isItemAvailable(Long branchId, Long itemId, int quantity) {
        return inventoryRepository.findByBranchIdAndItemId(branchId, itemId)
                .map(inventory -> inventory.hasStock(quantity))
                .orElse(false); // If not listed in inventory, it's not available
    }

    /**
     * Decrement stock for a specific item at a branch.
     * @param branchId ID of the branch.
     * @param itemId ID of the item.
     * @param quantity Quantity to decrement.
     */
    @Transactional
    public void decrementStock(Long branchId, Long itemId, int quantity) {
        atomicDecrementOrThrow(branchId, itemId, quantity);
    }

    /**
     * Decrement stock for a specific item at a branch atomically.
     * @param branchId ID of the branch.
     * @param itemId ID of the item.
     * @param quantity Quantity to decrement.
     * @throws IllegalStateException If stock is insufficient.
     */
    @Timed(value = "inventory.decrement", description = "Time to atomically decrement stock")
    @Transactional
    public void atomicDecrementOrThrow(Long branchId, Long itemId, int quantity) {
        int affected = inventoryRepository.atomicDecrement(branchId, itemId, quantity);
        if (affected == 0) {
            throw new IllegalStateException("Insufficient stock for item id: " + itemId + " at branch: " + branchId);
        }
    }

    /**
     * Increment stock for a specific item at a branch.
     * @param branchId ID of the branch.
     * @param itemId ID of the item.
     * @param quantity Quantity to increment.
     */
    @Transactional
    public void incrementStock(Long branchId, Long itemId, int quantity) {
        int affected = inventoryRepository.incrementStock(branchId, itemId, quantity);
        if (affected == 0) {
            log.warn("Could not restore stock for item {} at branch {} — inventory record not found.", itemId, branchId);
        }
    }

    /**
     * Get all inventory items for a branch.
     */
    public List<Inventory> findByBranch(Long branchId) {
        return inventoryRepository.findAllByBranchId(branchId);
    }

    @Transactional
    public Inventory save(Inventory inventory) {
        return inventoryRepository.save(inventory);
    }
}
