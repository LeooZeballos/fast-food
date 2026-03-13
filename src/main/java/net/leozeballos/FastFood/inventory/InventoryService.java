package net.leozeballos.FastFood.inventory;

import lombok.RequiredArgsConstructor;
import net.leozeballos.FastFood.error.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
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
        Inventory inventory = inventoryRepository.findByBranchIdAndItemId(branchId, itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Item not found in branch inventory"));
        
        if (!inventory.hasStock(quantity)) {
            throw new IllegalStateException("Insufficient stock for item: " + inventory.getItem().getName());
        }
        
        inventory.reduceStock(quantity);
        inventoryRepository.save(inventory);
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
