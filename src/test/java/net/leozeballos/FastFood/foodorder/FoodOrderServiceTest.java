package net.leozeballos.FastFood.foodorder;

import net.leozeballos.FastFood.foodorderstatemachine.FoodOrderEvent;
import net.leozeballos.FastFood.foodorderstatemachine.FoodOrderState;
import net.leozeballos.FastFood.foodorderstatemachine.FoodOrderStateChangeInterceptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.statemachine.config.StateMachineFactory;


import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class FoodOrderServiceTest {

    @Mock private FoodOrderRepository foodOrderRepository;
    @Mock private StateMachineFactory<FoodOrderState, FoodOrderEvent> stateMachineFactory;
    @Mock private FoodOrderStateChangeInterceptor foodOrderStateChangeInterceptor;
    private FoodOrderService underTest;

    @BeforeEach
    void setUp() {
        underTest = new FoodOrderService(foodOrderRepository, stateMachineFactory, foodOrderStateChangeInterceptor);
    }

    @Test
    void canFindAllFoodOrders() {
        // when
        underTest.findAll();

        // then
        verify(foodOrderRepository).findAll();
    }

    @Test
    void canFindFoodOrderById() {
        // given
        Long id = 1L;

        // when
        underTest.findById(id);

        // then
        verify(foodOrderRepository).findById(id);
    }

    @Test
    void canSaveFoodOrder() {
        // given
        FoodOrder foodOrder = new FoodOrder();

        // when
        underTest.save(foodOrder);

        // then
        verify(foodOrderRepository).save(foodOrder);
    }

    @Test
    void canDeleteFoodOrder() {
        // given
        FoodOrder foodOrder = new FoodOrder();

        // when
        underTest.delete(foodOrder);

        // then
        ArgumentCaptor<FoodOrder> captor = ArgumentCaptor.forClass(FoodOrder.class);
        verify(foodOrderRepository).delete(captor.capture());
        assertThat(captor.getValue()).isEqualTo(foodOrder);
    }

    @Test
    void canDeleteFoodOrderById() {
        // given
        Long id = 1L;

        // when
        underTest.deleteById(id);

        // then
        verify(foodOrderRepository).deleteById(id);
    }

    @Test
    void canDeleteAllFoodOrders() {
        // when
        underTest.deleteAll();

        // then
        verify(foodOrderRepository).deleteAll();
    }

    /*@Test
    void canUpdateFoodOrder() {
        // given
        FoodOrder foodOrder = FoodOrder.builder().state(FoodOrderState.CREATED).build();

        // when
        underTest.update(foodOrder.getId());

        // then
        assertThat(foodOrder.getState()).isEqualTo(FoodOrderState.CREATED);
    }

    @Test
    void canStartPreparationOfFoodOrder() {
    }

    @Test
    void canFinishPreparationOfFoodOrder() {
    }

    @Test
    void canConfirmPaymentOfFoodOrder() {
    }

    @Test
    void canCancelFoodOrder() {
    }

    @Test
    void canRejectFoodOrder() {
    }*/

    @Test
    void canFindAllFoodOrdersByState() {
        // when
        underTest.findAllFoodOrdersByState(FoodOrderState.CREATED);

        // then
        verify(foodOrderRepository).findAllFoodOrdersByState(FoodOrderState.CREATED);
    }

}