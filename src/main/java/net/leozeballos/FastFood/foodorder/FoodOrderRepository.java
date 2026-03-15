package net.leozeballos.FastFood.foodorder;

import net.leozeballos.FastFood.foodorderstatemachine.FoodOrderState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface FoodOrderRepository extends JpaRepository<FoodOrder, Long> {

    @Query("SELECT DISTINCT fo FROM FoodOrder fo " +
           "LEFT JOIN FETCH fo.branch b " +
           "LEFT JOIN FETCH fo.foodOrderDetails fod " +
           "LEFT JOIN FETCH fod.item i")
    List<FoodOrder> findAllWithDetails();

    @Query("SELECT DISTINCT fo FROM FoodOrder fo " +
           "LEFT JOIN FETCH fo.branch b " +
           "LEFT JOIN FETCH fo.foodOrderDetails fod " +
           "LEFT JOIN FETCH fod.item i " +
           "WHERE b.id = ?1")
    List<FoodOrder> findAllByBranchIdWithDetails(Long branchId);

    @Query("SELECT DISTINCT fo FROM FoodOrder fo " +
           "LEFT JOIN FETCH fo.branch b " +
           "LEFT JOIN FETCH fo.foodOrderDetails fod " +
           "LEFT JOIN FETCH fod.item i " +
           "WHERE b.id = ?1 AND fo.state = ?2")
    List<FoodOrder> findAllByBranchIdAndStateWithDetails(Long branchId, FoodOrderState state);

    @Query("SELECT DISTINCT fo FROM FoodOrder fo " +
           "LEFT JOIN FETCH fo.branch b " +
           "LEFT JOIN FETCH fo.foodOrderDetails fod " +
           "LEFT JOIN FETCH fod.item i " +
           "WHERE fo.state = ?1")
    List<FoodOrder> findAllByStateWithDetails(FoodOrderState state);

    @Query("SELECT DISTINCT fo FROM FoodOrder fo " +
           "LEFT JOIN FETCH fo.branch b " +
           "LEFT JOIN FETCH fo.foodOrderDetails fod " +
           "LEFT JOIN FETCH fod.item i " +
           "WHERE fo.state = FoodOrderState.CREATED AND fo.creationTimestamp < :cutoff")
    List<FoodOrder> findExpiredCreatedOrders(@Param("cutoff") LocalDateTime cutoff);

    @Query("SELECT COUNT(fo) FROM FoodOrder fo " +
           "JOIN fo.foodOrderDetails fod " +
           "WHERE fod.item.id = :itemId " +
           "AND fo.state NOT IN (FoodOrderState.PAID, " +
           "                     FoodOrderState.CANCELLED, " +
           "                     FoodOrderState.REJECTED)")
    long countActiveOrdersByItemId(@Param("itemId") Long itemId);

}
