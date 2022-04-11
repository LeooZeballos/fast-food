package net.leozeballos.FastFood.foodorderstate;

import net.leozeballos.FastFood.foodorder.FoodOrder;

public class Cancelled extends FoodOrderState {

    @Override
    public void startPreparation(FoodOrder order) throws IllegalStateException {
        throw new IllegalStateException("Order is already canceled");
    }

    @Override
    public void finishPreparation(FoodOrder order) throws IllegalStateException {
        throw new IllegalStateException("Order is already canceled");
    }

    @Override
    public void cancelOrder(FoodOrder order) throws IllegalStateException {
        throw new IllegalStateException("Order is already canceled");
    }

    @Override
    public void confirmPayment(FoodOrder order) throws IllegalStateException {
        throw new IllegalStateException("Order is already canceled");
    }

    @Override
    public void rejectOrder(FoodOrder order) throws IllegalStateException {
        throw new IllegalStateException("Order is already canceled");
    }

}