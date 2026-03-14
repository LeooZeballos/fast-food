package net.leozeballos.FastFood.inventory;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;
import java.util.List;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    
    Optional<Inventory> findByBranchIdAndItemId(Long branchId, Long itemId);
    
    List<Inventory> findAllByBranchId(Long branchId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE Inventory i SET i.stockQuantity = i.stockQuantity - :quantity " +
           "WHERE i.branch.id = :branchId AND i.item.id = :itemId " +
           "AND i.stockQuantity >= :quantity AND i.isAvailable = true")
    int atomicDecrement(@Param("branchId") Long branchId,
                        @Param("itemId") Long itemId,
                        @Param("quantity") int quantity);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE Inventory i SET i.stockQuantity = i.stockQuantity + :quantity " +
           "WHERE i.branch.id = :branchId AND i.item.id = :itemId")
    int incrementStock(@Param("branchId") Long branchId,
                       @Param("itemId") Long itemId,
                       @Param("quantity") int quantity);
}
