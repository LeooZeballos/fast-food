package net.leozeballos.FastFood.orderstate;


import net.leozeballos.FastFood.order.FoodOrder;

public class Rejected extends FoodOrderState {

    @Override
    public void startPreparation(FoodOrder order) throws IllegalStateException {
        throw new IllegalStateException("Order is already rejected");
    }

    @Override
    public void finishPreparation(FoodOrder order) throws IllegalStateException {
        throw new IllegalStateException("Order is already rejected");
    }

    @Override
    public void cancelOrder(FoodOrder order) throws IllegalStateException {
        throw new IllegalStateException("Order is already rejected");
    }

    @Override
    public void confirmPayment(FoodOrder order) throws IllegalStateException {
        throw new IllegalStateException("Order is already rejected");
    }

    @Override
    public void rejectOrder(FoodOrder order) throws IllegalStateException {
        throw new IllegalStateException("Order is already rejected");
    }

}
