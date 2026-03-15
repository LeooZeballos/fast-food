package net.leozeballos.FastFood.foodorder;

import net.leozeballos.FastFood.branch.Branch;
import net.leozeballos.FastFood.foodorderdetail.FoodOrderDetail;
import net.leozeballos.FastFood.foodorderstatemachine.FoodOrderEvent;
import net.leozeballos.FastFood.foodorderstatemachine.FoodOrderState;
import net.leozeballos.FastFood.foodorderstatemachine.FoodOrderStateChangeInterceptor;
import net.leozeballos.FastFood.inventory.InventoryService;
import net.leozeballos.FastFood.item.Item;
import net.leozeballos.FastFood.item.ItemService;
import net.leozeballos.FastFood.util.AuditService;
import net.leozeballos.FastFood.branch.BranchService;
import net.leozeballos.FastFood.mapper.FoodOrderMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.support.DefaultStateMachineContext;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FoodOrderStockRestorationTest {

    @Mock private FoodOrderRepository foodOrderRepository;
    @Mock private StateMachineFactory<FoodOrderState, FoodOrderEvent> stateMachineFactory;
    @Mock private FoodOrderStateChangeInterceptor foodOrderStateChangeInterceptor;
    @Mock private BranchService branchService;
    @Mock private ItemService itemService;
    @Mock private InventoryService inventoryService;
    @Mock private FoodOrderMapper foodOrderMapper;
    @Mock private AuditService auditService;
    @Mock private StateMachine<FoodOrderState, FoodOrderEvent> stateMachine;

    private FoodOrderService underTest;

    @BeforeEach
    void setUp() {
        underTest = new FoodOrderService(foodOrderRepository, stateMachineFactory, foodOrderStateChangeInterceptor, branchService, itemService, inventoryService, foodOrderMapper, auditService);
    }


    @Test
    void cancelOrderShouldRestoreStock() {
        // given
        Long orderId = 1L;
        Long branchId = 10L;
        Long itemId = 100L;
        int quantity = 2;

        Branch branch = new Branch();
        branch.setId(branchId);

        Item item = mock(Item.class);
        when(item.getId()).thenReturn(itemId);

        FoodOrderDetail detail = new FoodOrderDetail();
        detail.setItem(item);
        detail.setQuantity(quantity);

        FoodOrder order = new FoodOrder();
        order.setId(orderId);
        order.setBranch(branch);
        order.setFoodOrderDetails(List.of(detail));
        order.setState(FoodOrderState.CREATED);

        when(foodOrderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(stateMachineFactory.getStateMachine(anyString())).thenReturn(stateMachine);
        when(stateMachine.getStateMachineAccessor()).thenReturn(mock(org.springframework.statemachine.access.StateMachineAccessor.class));
        when(stateMachine.startReactively()).thenReturn(reactor.core.publisher.Mono.empty());
        when(stateMachine.stopReactively()).thenReturn(reactor.core.publisher.Mono.empty());
        when(stateMachine.sendEvent(any(reactor.core.publisher.Mono.class))).thenReturn(reactor.core.publisher.Flux.empty());

        // when
        underTest.cancel(orderId, null);

        // then
        verify(inventoryService).incrementStock(eq(branchId), eq(itemId), eq(quantity));
    }

    @Test
    void rejectOrderShouldRestoreStock() {
        // given
        Long orderId = 1L;
        Long branchId = 10L;
        Long itemId = 100L;
        int quantity = 3;

        Branch branch = new Branch();
        branch.setId(branchId);

        Item item = mock(Item.class);
        when(item.getId()).thenReturn(itemId);

        FoodOrderDetail detail = new FoodOrderDetail();
        detail.setItem(item);
        detail.setQuantity(quantity);

        FoodOrder order = new FoodOrder();
        order.setId(orderId);
        order.setBranch(branch);
        order.setFoodOrderDetails(List.of(detail));
        order.setState(FoodOrderState.DONE);

        when(foodOrderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(stateMachineFactory.getStateMachine(anyString())).thenReturn(stateMachine);
        when(stateMachine.getStateMachineAccessor()).thenReturn(mock(org.springframework.statemachine.access.StateMachineAccessor.class));
        when(stateMachine.startReactively()).thenReturn(reactor.core.publisher.Mono.empty());
        when(stateMachine.stopReactively()).thenReturn(reactor.core.publisher.Mono.empty());
        when(stateMachine.sendEvent(any(reactor.core.publisher.Mono.class))).thenReturn(reactor.core.publisher.Flux.empty());

        // when
        underTest.reject(orderId, null);

        // then
        verify(inventoryService).incrementStock(eq(branchId), eq(itemId), eq(quantity));
    }
}
