package net.leozeballos.FastFood.foodorder;

import net.leozeballos.FastFood.foodorderstatemachine.FoodOrderState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface FoodOrderRepository extends JpaRepository<FoodOrder, Long> {

    @Query("SELECT DISTINCT fo FROM FoodOrder fo " +
           "LEFT JOIN FETCH fo.branch b " +
           "LEFT JOIN FETCH fo.foodOrderDetails fod " +
           "LEFT JOIN FETCH fod.item i " +
           "WHERE fo.state = ?1")
    List<FoodOrder> findAllFoodOrdersByState(FoodOrderState state);

    @Query("SELECT DISTINCT fo FROM FoodOrder fo " +
           "LEFT JOIN FETCH fo.branch b " +
           "LEFT JOIN FETCH fo.foodOrderDetails fod " +
           "LEFT JOIN FETCH fod.item i")
    List<FoodOrder> findAllWithDetails();

}
