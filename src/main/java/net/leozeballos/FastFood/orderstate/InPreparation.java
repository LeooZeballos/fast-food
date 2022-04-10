package net.leozeballos.FastFood.orderstate;


import net.leozeballos.FastFood.foodorder.FoodOrder;

public class InPreparation extends FoodOrderState {

    @Override
    public void startPreparation(FoodOrder order) throws IllegalStateException {
        throw new IllegalStateException("Order is already in preparation.");
    }

    @Override
    public void finishPreparation(FoodOrder order) {
        order.setState(new Done());
    }

    @Override
    public void cancelOrder(FoodOrder order) {
        order.setState(new Cancelled());
    }

    @Override
    public void confirmPayment(FoodOrder order) throws IllegalStateException {
        throw new IllegalStateException("Order is still in preparation.");
    }

    @Override
    public void rejectOrder(FoodOrder order) throws IllegalStateException {
        throw new IllegalStateException("Order is still in preparation.");
    }

}
