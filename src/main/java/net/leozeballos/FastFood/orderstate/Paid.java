package net.leozeballos.FastFood.orderstate;

import net.leozeballos.FastFood.foodorder.FoodOrder;

public class Paid extends FoodOrderState {

    @Override
    public void startPreparation(FoodOrder order) throws IllegalStateException {
        throw new IllegalStateException("Order is already paid for");
    }

    @Override
    public void finishPreparation(FoodOrder order) throws IllegalStateException {
        throw new IllegalStateException("Order is already paid for");
    }

    @Override
    public void cancelOrder(FoodOrder order) throws IllegalStateException {
        throw new IllegalStateException("Order is already paid for");
    }

    @Override
    public void confirmPayment(FoodOrder order) throws IllegalStateException {
        throw new IllegalStateException("Order is already paid for");
    }

    @Override
    public void rejectOrder(FoodOrder order) throws IllegalStateException {
        throw new IllegalStateException("Order is already paid for");
    }

}
