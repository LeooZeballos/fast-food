package net.leozeballos.FastFood.foodorder;

import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.leozeballos.FastFood.branch.BranchService;
import net.leozeballos.FastFood.error.ResourceNotFoundException;
import net.leozeballos.FastFood.foodorderdetail.FoodOrderDetail;
import net.leozeballos.FastFood.foodorderstatemachine.FoodOrderEvent;
import net.leozeballos.FastFood.foodorderstatemachine.FoodOrderState;
import net.leozeballos.FastFood.foodorderstatemachine.FoodOrderStateChangeInterceptor;
import net.leozeballos.FastFood.inventory.InventoryService;
import net.leozeballos.FastFood.item.ItemService;
import net.leozeballos.FastFood.mapper.FoodOrderMapper;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.support.DefaultStateMachineContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class FoodOrderService {

    public static final String FOOD_ORDER_ID_HEADER = "food_order_id";

    private final FoodOrderRepository foodOrderRepository;
    private final StateMachineFactory<FoodOrderState, FoodOrderEvent> stateMachineFactory;
    private final FoodOrderStateChangeInterceptor stateChangeInterceptor;
    private final BranchService branchService;
    private final ItemService itemService;
    private final InventoryService inventoryService;
    private final FoodOrderMapper foodOrderMapper;

    public List<FoodOrderDTO> findAllDTO(Long branchId) {
        List<FoodOrder> orders = (branchId == null) 
                ? foodOrderRepository.findAllWithDetails()
                : foodOrderRepository.findAllByBranchIdWithDetails(branchId);
        
        return orders.stream()
                .map(foodOrderMapper::toDTO)
                .collect(Collectors.toList());
    }

    public List<FoodOrder> findAll() {
        return foodOrderRepository.findAll();
    }

    public FoodOrderDTO findDTOById(Long id, Long branchId) {
        FoodOrder order = findById(id, branchId);
        return foodOrderMapper.toDTO(order);
    }

    public FoodOrder findById(Long id, Long branchId) {
        FoodOrder order = foodOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("FoodOrder not found with id: " + id));
        
        checkBranchAccess(order, branchId);
        return order;
    }

    private void checkBranchAccess(FoodOrder order, Long branchId) {
        if (branchId != null && !order.getBranch().getId().equals(branchId)) {
            throw new AccessDeniedException("User does not have access to this branch's data");
        }
    }

    @Timed(value = "foodorder.create", description = "Time to create a food order")
    @Transactional
    public FoodOrder createOrder(CreateOrderDTO createOrderDTO) {
        FoodOrder order = new FoodOrder();
        order.setState(FoodOrderState.CREATED);
        order.setBranch(branchService.findById(createOrderDTO.branchId()));
        
        List<FoodOrderDetail> details = createOrderDTO.items().stream()
                .map(itemDTO -> {
                    // Atomic: check AND decrement in one SQL statement
                    inventoryService.atomicDecrementOrThrow(
                        createOrderDTO.branchId(), itemDTO.itemId(), itemDTO.quantity());

                    FoodOrderDetail detail = new FoodOrderDetail();
                    var item = itemService.findById(itemDTO.itemId());
                    detail.setItem(item);
                    detail.setQuantity(itemDTO.quantity());
                    detail.setHistoricPrice(item.calculatePrice());
                    
                    return detail;
                })
                .collect(Collectors.toList());
        
        order.setFoodOrderDetails(details);
        return foodOrderRepository.save(order);
    }

    @Transactional
    public FoodOrder save(FoodOrder order) {
        if (order.getState() == null) {
            order.setState(FoodOrderState.CREATED);
        }
        for (FoodOrderDetail detail : order.getFoodOrderDetails()) {
            if (detail.getHistoricPrice() == 0 && detail.getItem() != null) {
                detail.setHistoricPrice(detail.getItem().calculatePrice());
            }
        }
        return foodOrderRepository.save(order);
    }

    public void delete(FoodOrder order) {
        foodOrderRepository.delete(order);
    }

    public void deleteById(Long id, Long branchId) {
        FoodOrder order = foodOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("FoodOrder not found with id: " + id));
        checkBranchAccess(order, branchId);
        foodOrderRepository.deleteById(id);
    }

    public void deleteAll() {
        foodOrderRepository.deleteAll();
    }

    public StateMachine<FoodOrderState, FoodOrderEvent> update(Long id, Long branchId) {
        findById(id, branchId); // Access check
        StateMachine<FoodOrderState, FoodOrderEvent> stateMachine = build(id);
        sendEvent(id, stateMachine, FoodOrderEvent.UPDATE);
        return stateMachine;
    }

    @Timed(value = "foodorder.start_preparation", description = "Time to start preparation of a food order")
    @Transactional
    public StateMachine<FoodOrderState, FoodOrderEvent> startPreparation(Long id, Long branchId) {
        FoodOrder order = findById(id, branchId); // Access check
        order.setPreparationStartTimestamp(LocalDateTime.now());
        foodOrderRepository.save(order);
        
        StateMachine<FoodOrderState, FoodOrderEvent> stateMachine = build(id);
        sendEvent(id, stateMachine, FoodOrderEvent.STARTPREPARATION);
        return stateMachine;
    }

    @Transactional
    public StateMachine<FoodOrderState, FoodOrderEvent> finishPreparation(Long id, Long branchId) {
        findById(id, branchId); // Access check
        StateMachine<FoodOrderState, FoodOrderEvent> stateMachine = build(id);
        sendEvent(id, stateMachine, FoodOrderEvent.FINISHPREPARATION);
        return stateMachine;
    }

    @Transactional
    public StateMachine<FoodOrderState, FoodOrderEvent> confirmPayment(Long id, Long branchId) {
        FoodOrder foodOrder = findById(id, branchId); // Access check
        StateMachine<FoodOrderState, FoodOrderEvent> stateMachine = build(id);
        sendEvent(id, stateMachine, FoodOrderEvent.CONFIRMPAYMENT);
        foodOrder.setPaymentTimestamp(LocalDateTime.now());
        foodOrderRepository.save(foodOrder);
        return stateMachine;
    }

    @Transactional
    public StateMachine<FoodOrderState, FoodOrderEvent> cancel(Long id, Long branchId) {
        FoodOrder order = findById(id, branchId); // Access check
        restoreStockForOrder(order);
        StateMachine<FoodOrderState, FoodOrderEvent> stateMachine = build(id);
        sendEvent(id, stateMachine, FoodOrderEvent.CANCEL);
        return stateMachine;
    }

    @Transactional
    public StateMachine<FoodOrderState, FoodOrderEvent> reject(Long id, Long branchId) {
        FoodOrder order = findById(id, branchId); // Access check
        restoreStockForOrder(order);
        StateMachine<FoodOrderState, FoodOrderEvent> stateMachine = build(id);
        sendEvent(id, stateMachine, FoodOrderEvent.REJECT);
        return stateMachine;
    }

    private void restoreStockForOrder(FoodOrder order) {
        Long branchId = order.getBranch().getId();
        for (FoodOrderDetail detail : order.getFoodOrderDetails()) {
            inventoryService.incrementStock(
                branchId,
                detail.getItem().getId(),
                detail.getQuantity()
            );
        }
    }

    private void sendEvent(Long id, StateMachine<FoodOrderState, FoodOrderEvent> stateMachine, FoodOrderEvent event) {
        Message<FoodOrderEvent> msg = MessageBuilder.withPayload(event)
                .setHeader(FOOD_ORDER_ID_HEADER, id)
                .build();
        stateMachine.sendEvent(Mono.just(msg)).subscribe(
            result -> log.debug("Event {} sent for order {}", event, id),
            error -> log.error("Error sending event {} for order {}: {}", event, id, error.getMessage())
        );
    }

    private StateMachine<FoodOrderState, FoodOrderEvent> build(Long id) {
        // Warning: this doesn't re-check branchId internally, 
        // callers MUST check before calling build() or via findById(id, branchId)
        FoodOrder order = foodOrderRepository.findById(id).orElseThrow();
        StateMachine<FoodOrderState, FoodOrderEvent> stateMachine = stateMachineFactory.getStateMachine(Long.toString(order.getId()));
        stateMachine.stopReactively().subscribe(
            null,
            error -> log.error("Error stopping state machine for order {}: {}", id, error.getMessage())
        );
        stateMachine.getStateMachineAccessor()
                .doWithAllRegions(sma -> {
                    sma.addStateMachineInterceptor(stateChangeInterceptor);
                    sma.resetStateMachineReactively(new DefaultStateMachineContext<>(order.getState(), null, null, null)).subscribe(
                        null,
                        error -> log.error("Error resetting state machine for order {}: {}", id, error.getMessage())
                    );
                });
        stateMachine.startReactively().subscribe(
            null,
            error -> log.error("Error starting state machine for order {}: {}", id, error.getMessage())
        );
        return stateMachine;
    }

    public List<FoodOrderDTO> findAllFoodOrdersByStateDTO(FoodOrderState state, Long branchId) {
        List<FoodOrder> orders = (branchId == null)
                ? foodOrderRepository.findAllByStateWithDetails(state)
                : foodOrderRepository.findAllByBranchIdAndStateWithDetails(branchId, state);

        return orders.stream()
                .map(foodOrderMapper::toDTO)
                .collect(Collectors.toList());
    }

    public List<FoodOrder> findAllFoodOrdersByState(FoodOrderState state) {
        return foodOrderRepository.findAllByStateWithDetails(state);
    }
}
