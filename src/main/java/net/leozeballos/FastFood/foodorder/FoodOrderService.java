package net.leozeballos.FastFood.foodorder;

import lombok.RequiredArgsConstructor;
import net.leozeballos.FastFood.error.ResourceNotFoundException;
import net.leozeballos.FastFood.foodorderdetail.FoodOrderDetail;
import net.leozeballos.FastFood.foodorderstatemachine.FoodOrderEvent;
import net.leozeballos.FastFood.foodorderstatemachine.FoodOrderState;
import net.leozeballos.FastFood.foodorderstatemachine.FoodOrderStateChangeInterceptor;
import net.leozeballos.FastFood.mapper.FoodOrderMapper;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.support.DefaultStateMachineContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class FoodOrderService {

    public static final String FOOD_ORDER_ID_HEADER = "food_order_id";

    private final FoodOrderRepository foodOrderRepository;
    private final StateMachineFactory<FoodOrderState, FoodOrderEvent> stateMachineFactory;
    private final FoodOrderStateChangeInterceptor stateChangeInterceptor;
    private final net.leozeballos.FastFood.branch.BranchService branchService;
    private final net.leozeballos.FastFood.item.ItemService itemService;
    private final net.leozeballos.FastFood.inventory.InventoryService inventoryService;
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
            throw new org.springframework.security.access.AccessDeniedException("User does not have access to this branch's data");
        }
    }

    @Transactional
    public FoodOrder createOrder(CreateOrderDTO createOrderDTO) {
        // Validate stock before creating the order
        for (var itemDTO : createOrderDTO.items()) {
            if (!inventoryService.isItemAvailable(createOrderDTO.branchId(), itemDTO.itemId(), itemDTO.quantity())) {
                throw new IllegalStateException("Item not available in branch inventory: " + itemDTO.itemId());
            }
        }

        FoodOrder order = new FoodOrder();
        order.setState(FoodOrderState.CREATED);
        order.setBranch(branchService.findById(createOrderDTO.branchId()));
        
        List<FoodOrderDetail> details = createOrderDTO.items().stream()
                .map(itemDTO -> {
                    FoodOrderDetail detail = new FoodOrderDetail();
                    var item = itemService.findById(itemDTO.itemId());
                    detail.setItem(item);
                    detail.setQuantity(itemDTO.quantity());
                    detail.setHistoricPrice(item.calculatePrice());
                    
                    // Decrement stock
                    inventoryService.decrementStock(createOrderDTO.branchId(), itemDTO.itemId(), itemDTO.quantity());
                    
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
        findById(id, branchId); // Access check
        StateMachine<FoodOrderState, FoodOrderEvent> stateMachine = build(id);
        sendEvent(id, stateMachine, FoodOrderEvent.CANCEL);
        return stateMachine;
    }

    @Transactional
    public StateMachine<FoodOrderState, FoodOrderEvent> reject(Long id, Long branchId) {
        findById(id, branchId); // Access check
        StateMachine<FoodOrderState, FoodOrderEvent> stateMachine = build(id);
        sendEvent(id, stateMachine, FoodOrderEvent.REJECT);
        return stateMachine;
    }

    private void sendEvent(Long id, StateMachine<FoodOrderState, FoodOrderEvent> stateMachine, FoodOrderEvent event) {
        Message<FoodOrderEvent> msg = MessageBuilder.withPayload(event)
                .setHeader(FOOD_ORDER_ID_HEADER, id)
                .build();
        stateMachine.sendEvent(Mono.just(msg)).subscribe();
    }

    private StateMachine<FoodOrderState, FoodOrderEvent> build(Long id) {
        // Warning: this doesn't re-check branchId internally, 
        // callers MUST check before calling build() or via findById(id, branchId)
        FoodOrder order = foodOrderRepository.findById(id).orElseThrow();
        StateMachine<FoodOrderState, FoodOrderEvent> stateMachine = stateMachineFactory.getStateMachine(Long.toString(order.getId()));
        stateMachine.stopReactively().subscribe();
        stateMachine.getStateMachineAccessor()
                .doWithAllRegions(sma -> {
                    sma.addStateMachineInterceptor(stateChangeInterceptor);
                    sma.resetStateMachineReactively(new DefaultStateMachineContext<>(order.getState(), null, null, null)).subscribe();
                });
        stateMachine.startReactively().subscribe();
        return stateMachine;
    }

    public List<FoodOrderDTO> findAllFoodOrdersByStateDTO(FoodOrderState state, Long branchId) {
        List<FoodOrder> orders;
        if (branchId == null) {
             orders = foodOrderRepository.findAllWithDetails().stream()
                .filter(o -> o.getState() == state)
                .collect(Collectors.toList());
        } else {
            orders = foodOrderRepository.findAllByBranchIdAndStateWithDetails(branchId, state);
        }

        return orders.stream()
                .map(foodOrderMapper::toDTO)
                .collect(Collectors.toList());
    }

    public List<FoodOrder> findAllFoodOrdersByState(FoodOrderState state) {
        return foodOrderRepository.findAllWithDetails().stream()
                .filter(o -> o.getState() == state)
                .collect(Collectors.toList());
    }
}
