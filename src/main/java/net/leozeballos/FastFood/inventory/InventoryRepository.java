package net.leozeballos.FastFood.inventory;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.List;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    
    Optional<Inventory> findByBranchIdAndItemId(Long branchId, Long itemId);
    
    List<Inventory> findAllByBranchId(Long branchId);
}
