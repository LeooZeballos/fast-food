package net.leozeballos.FastFood.FoodOrderStateMachine;

import lombok.RequiredArgsConstructor;
import net.leozeballos.FastFood.foodorder.FoodOrder;
import net.leozeballos.FastFood.foodorder.FoodOrderRepository;
import net.leozeballos.FastFood.foodorder.FoodOrderService;
import org.springframework.messaging.Message;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.state.State;
import org.springframework.statemachine.support.StateMachineInterceptorAdapter;
import org.springframework.statemachine.transition.Transition;
import org.springframework.stereotype.Component;

import java.util.Optional;

@RequiredArgsConstructor
@Component
public class FoodOrderStateChangeInterceptor extends StateMachineInterceptorAdapter<FoodOrderState, FoodOrderEvent> {

    private final FoodOrderRepository foodOrderRepository;

    @Override
    public void preStateChange(State<FoodOrderState, FoodOrderEvent> state, Message<FoodOrderEvent> message,
            Transition<FoodOrderState, FoodOrderEvent> transition, StateMachine<FoodOrderState,
            FoodOrderEvent> stateMachine, StateMachine<FoodOrderState, FoodOrderEvent> rootStateMachine) {
        Optional.ofNullable(message).ifPresent(msg -> Optional.ofNullable(msg.getHeaders().getOrDefault(FoodOrderService.FOOD_ORDER_ID_HEADER, -1L))
                .ifPresent(foodOrderId -> {
                    FoodOrder foodOrder = foodOrderRepository.getOne((Long) foodOrderId);
                    foodOrder.setState(state.getId());
                    foodOrderRepository.save(foodOrder);
                }));
    }
}