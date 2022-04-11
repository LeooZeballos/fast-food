package net.leozeballos.FastFood.foodorderstate;


import net.leozeballos.FastFood.foodorder.FoodOrder;

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
