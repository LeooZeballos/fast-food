package net.leozeballos.FastFood.foodorder;

import lombok.RequiredArgsConstructor;
import net.leozeballos.FastFood.foodorderdetail.FoodOrderDetail;
import net.leozeballos.FastFood.foodorderdetail.FoodOrderDetailDTO;
import net.leozeballos.FastFood.foodorderstatemachine.FoodOrderEvent;
import net.leozeballos.FastFood.foodorderstatemachine.FoodOrderState;
import net.leozeballos.FastFood.foodorderstatemachine.FoodOrderStateChangeInterceptor;
import net.leozeballos.FastFood.util.FormattingUtils;
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
public class FoodOrderService {

    public static final String FOOD_ORDER_ID_HEADER = "food_order_id";

    private final FoodOrderRepository foodOrderRepository;
    private final StateMachineFactory<FoodOrderState, FoodOrderEvent> stateMachineFactory;
    private final FoodOrderStateChangeInterceptor stateChangeInterceptor;

    public List<FoodOrderDTO> findAllDTO() {
        return foodOrderRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<FoodOrder> findAll() {
        return foodOrderRepository.findAll();
    }

    public FoodOrderDTO findDTOById(Long id) {
        return foodOrderRepository.findById(id)
                .map(this::convertToDTO)
                .orElse(null);
    }

    public FoodOrder findById(Long id) {
        return foodOrderRepository.findById(id).orElse(null);
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

    public void deleteById(Long id) {
        foodOrderRepository.deleteById(id);
    }

    public void deleteAll() {
        foodOrderRepository.deleteAll();
    }

    public StateMachine<FoodOrderState, FoodOrderEvent> update(Long id) {
        StateMachine<FoodOrderState, FoodOrderEvent> stateMachine = build(id);
        sendEvent(id, stateMachine, FoodOrderEvent.UPDATE);
        return stateMachine;
    }

    public StateMachine<FoodOrderState, FoodOrderEvent> startPreparation(Long id) {
        StateMachine<FoodOrderState, FoodOrderEvent> stateMachine = build(id);
        sendEvent(id, stateMachine, FoodOrderEvent.STARTPREPARATION);
        return stateMachine;
    }

    public StateMachine<FoodOrderState, FoodOrderEvent> finishPreparation(Long id) {
        StateMachine<FoodOrderState, FoodOrderEvent> stateMachine = build(id);
        sendEvent(id, stateMachine, FoodOrderEvent.FINISHPREPARATION);
        return stateMachine;
    }

    @Transactional
    public StateMachine<FoodOrderState, FoodOrderEvent> confirmPayment(Long id) {
        StateMachine<FoodOrderState, FoodOrderEvent> stateMachine = build(id);
        FoodOrder foodOrder = foodOrderRepository.findById(id).orElse(null);
        sendEvent(id, stateMachine, FoodOrderEvent.CONFIRMPAYMENT);
        assert foodOrder != null;
        foodOrder.setPaymentTimestamp(LocalDateTime.now());
        foodOrderRepository.save(foodOrder);
        return stateMachine;
    }

    public StateMachine<FoodOrderState, FoodOrderEvent> cancel(Long id) {
        StateMachine<FoodOrderState, FoodOrderEvent> stateMachine = build(id);
        sendEvent(id, stateMachine, FoodOrderEvent.CANCEL);
        return stateMachine;
    }

    public StateMachine<FoodOrderState, FoodOrderEvent> reject(Long id) {
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
        FoodOrder order = foodOrderRepository.findById(id).orElse(null);
        assert order != null;
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

    public List<FoodOrderDTO> findAllFoodOrdersByStateDTO(FoodOrderState state) {
        return foodOrderRepository.findAllFoodOrdersByState(state).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<FoodOrder> findAllFoodOrdersByState(FoodOrderState state) {
        return foodOrderRepository.findAllFoodOrdersByState(state);
    }

    public FoodOrderDTO convertToDTO(FoodOrder order) {
        return FoodOrderDTO.builder()
                .id(order.getId())
                .creationTimestamp(order.getCreationTimestamp())
                .paymentTimestamp(order.getPaymentTimestamp())
                .formattedState(FormattingUtils.formatState(order.getState()))
                .branchName(order.getBranch() != null ? order.getBranch().getName() : "Unknown Branch")
                .total(order.calculateTotal())
                .foodOrderDetails(order.getFoodOrderDetails().stream()
                        .map(this::convertToDetailDTO)
                        .collect(Collectors.toList()))
                .build();
    }

    private FoodOrderDetailDTO convertToDetailDTO(FoodOrderDetail detail) {
        return FoodOrderDetailDTO.builder()
                .id(detail.getId())
                .itemName(detail.getItem() != null ? detail.getItem().getName() : "Unknown Item")
                .historicPrice(detail.getHistoricPrice())
                .quantity(detail.getQuantity())
                .subtotal(detail.calculateSubtotal())
                .build();
    }
}
