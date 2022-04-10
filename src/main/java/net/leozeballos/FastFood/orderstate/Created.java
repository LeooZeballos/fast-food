package net.leozeballos.FastFood.orderstate;

import net.leozeballos.FastFood.foodorder.FoodOrder;

import javax.persistence.Entity;

@Entity
public class Created extends FoodOrderState {

    public Created() {
        super("Created", "Order has been created");
    }

    @Override
    public void startPreparation(FoodOrder order) {
        order.setState(new InPreparation());
    }

    @Override
    public void finishPreparation(FoodOrder order) throws IllegalStateException {
        throw new IllegalStateException("Order is not in preparation");
    }

    @Override
    public void cancelOrder(FoodOrder order) {
        order.setState(new Cancelled());
    }

    @Override
    public void confirmPayment(FoodOrder order) throws IllegalStateException {
        throw new IllegalStateException("Order has not been prepared");
    }

    @Override
    public void rejectOrder(FoodOrder order) throws IllegalStateException {
        throw new IllegalStateException("Order has not been prepared");
    }

}
