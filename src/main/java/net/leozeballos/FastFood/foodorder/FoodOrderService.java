package net.leozeballos.FastFood.foodorder;

import lombok.RequiredArgsConstructor;
import net.leozeballos.FastFood.foodorderstatemachine.FoodOrderEvent;
import net.leozeballos.FastFood.foodorderstatemachine.FoodOrderState;
import net.leozeballos.FastFood.foodorderstatemachine.FoodOrderStateChangeInterceptor;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.support.DefaultStateMachineContext;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class FoodOrderService {

    public static final String FOOD_ORDER_ID_HEADER = "food_order_id";

    private final FoodOrderRepository foodOrderRepository;
    private final StateMachineFactory<FoodOrderState, FoodOrderEvent> stateMachineFactory;
    private final FoodOrderStateChangeInterceptor stateChangeInterceptor;

    public List<FoodOrder> findAll() {
        return foodOrderRepository.findAll();
    }

    public FoodOrder findById(Long id) {
        return foodOrderRepository.findById(id).orElse(null);
    }

    public FoodOrder save(FoodOrder order) {
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
        stateMachine.sendEvent(msg);
    }

    private StateMachine<FoodOrderState, FoodOrderEvent> build(Long id) {
        FoodOrder order = foodOrderRepository.findById(id).orElse(null);
        assert order != null;
        StateMachine<FoodOrderState, FoodOrderEvent> stateMachine = stateMachineFactory.getStateMachine(Long.toString(order.getId()));
        stateMachine.stop();
        stateMachine.getStateMachineAccessor()
                .doWithAllRegions(sma -> {
                    sma.addStateMachineInterceptor(stateChangeInterceptor);
                    sma.resetStateMachine(new DefaultStateMachineContext<>(order.getState(), null, null, null));
                });
        stateMachine.start();
        return stateMachine;
    }

    public List<FoodOrder> findByState(FoodOrderState state) {
        List<FoodOrder> orders = foodOrderRepository.findAll();
        orders.removeIf(order -> order.getState() != state);
        return orders;
    }
}
