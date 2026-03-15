package net.leozeballos.FastFood.foodorder;

import net.leozeballos.FastFood.foodorderstatemachine.FoodOrderEvent;
import net.leozeballos.FastFood.foodorderstatemachine.FoodOrderState;
import net.leozeballos.FastFood.foodorderstatemachine.FoodOrderStateChangeInterceptor;
import net.leozeballos.FastFood.mapper.FoodOrderMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.statemachine.config.StateMachineFactory;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FoodOrderServiceTest {

    @Mock private FoodOrderRepository foodOrderRepository;
    @Mock private StateMachineFactory<FoodOrderState, FoodOrderEvent> stateMachineFactory;
    @Mock private FoodOrderStateChangeInterceptor foodOrderStateChangeInterceptor;
    @Mock private net.leozeballos.FastFood.branch.BranchService branchService;
    @Mock private net.leozeballos.FastFood.item.ItemService itemService;
    @Mock private net.leozeballos.FastFood.inventory.InventoryService inventoryService;
    @Spy private FoodOrderMapper foodOrderMapper;
    private FoodOrderService underTest;

    @BeforeEach
    void setUp() {
        underTest = new FoodOrderService(foodOrderRepository, stateMachineFactory, foodOrderStateChangeInterceptor, branchService, itemService, inventoryService, foodOrderMapper);
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
        FoodOrder order = new FoodOrder();
        when(foodOrderRepository.findById(id)).thenReturn(Optional.of(order));

        // when
        underTest.findById(id, null);

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
        FoodOrder order = new FoodOrder();
        when(foodOrderRepository.findById(id)).thenReturn(Optional.of(order));

        // when
        underTest.deleteById(id, null);

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

    @Test
    void canFindAllFoodOrdersByState() {
        // when
        underTest.findAllFoodOrdersByState(FoodOrderState.CREATED);

        // then
        verify(foodOrderRepository).findAllByStateWithDetails(FoodOrderState.CREATED);
    }

    @Test
    void canConvertToDTO() {
        // given
        net.leozeballos.FastFood.branch.Branch branch = new net.leozeballos.FastFood.branch.Branch();
        branch.setName("Main Branch");
        
        net.leozeballos.FastFood.product.Product product = new net.leozeballos.FastFood.product.Product();
        product.setPrice(10.0);
        product.setName("Product 1");

        net.leozeballos.FastFood.foodorderdetail.FoodOrderDetail detail = new net.leozeballos.FastFood.foodorderdetail.FoodOrderDetail();
        detail.setId(1L);
        detail.setItem(product);
        detail.setQuantity(2);
        detail.setHistoricPrice(10.0);

        java.time.LocalDateTime now = java.time.LocalDateTime.now();
        FoodOrder order = new FoodOrder();
        order.setId(1L);
        order.setBranch(branch);
        order.setCreationTimestamp(now);
        order.setState(FoodOrderState.CREATED);
        order.setFoodOrderDetails(java.util.List.of(detail));

        // when
        FoodOrderDTO dto = foodOrderMapper.toDTO(order);

        // then
        assertThat(dto.id()).isEqualTo(1L);
        assertThat(dto.branchName()).isEqualTo("Main Branch");
        assertThat(dto.creationTimestamp()).isEqualTo(now);
        assertThat(dto.formattedState()).isEqualTo("Created");
        assertThat(dto.total()).isEqualTo(20.0);
        assertThat(dto.foodOrderDetails()).hasSize(1);
        assertThat(dto.foodOrderDetails().get(0).itemName()).isEqualTo("Product 1");
    }

}
