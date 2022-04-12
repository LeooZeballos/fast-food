package net.leozeballos.FastFood.foodorder;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;

import java.util.EnumSet;

@Slf4j
@EnableStateMachineFactory
@Configuration
public class StateMachineConfig extends StateMachineConfigurerAdapter<FoodOrderState, FoodOrderEvent> {

    @Override
    public void configure(StateMachineStateConfigurer<FoodOrderState, FoodOrderEvent> states) throws Exception {
        states.withStates()
                .initial(FoodOrderState.CREATED)
                .states(EnumSet.allOf(FoodOrderState.class))
                .end(FoodOrderState.CANCELLED)
                .end(FoodOrderState.REJECTED)
                .end(FoodOrderState.PAID);
        super.configure(states);
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<FoodOrderState, FoodOrderEvent> transitions) throws Exception {
        transitions
                .withExternal().source(FoodOrderState.CREATED).target(FoodOrderState.CREATED).event(FoodOrderEvent.UPDATE).and()
                .withExternal().source(FoodOrderState.CREATED).target(FoodOrderState.INPREPARATION).event(FoodOrderEvent.STARTPREPARATION).and()
                .withExternal().source(FoodOrderState.INPREPARATION).target(FoodOrderState.DONE).event(FoodOrderEvent.FINISHPREPARATION).and()
                .withExternal().source(FoodOrderState.DONE).target(FoodOrderState.PAID).event(FoodOrderEvent.CONFIRMPAYMENT).and()
                .withExternal().source(FoodOrderState.CREATED).target(FoodOrderState.CANCELLED).event(FoodOrderEvent.CANCEL).and()
                .withExternal().source(FoodOrderState.INPREPARATION).target(FoodOrderState.CANCELLED).event(FoodOrderEvent.CANCEL).and()
                .withExternal().source(FoodOrderState.DONE).target(FoodOrderState.REJECTED).event(FoodOrderEvent.REJECT);
    }
}
