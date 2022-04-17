package net.leozeballos.FastFood.foodorder;

import net.leozeballos.FastFood.foodorderstatemachine.FoodOrderState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface FoodOrderRepository extends JpaRepository<FoodOrder, Long> {

    @Query("SELECT fo FROM FoodOrder fo WHERE fo.state = ?1")
    List<FoodOrder> findAllFoodOrdersByState(FoodOrderState state);

}