package net.leozeballos.FastFood.orderstate;

import net.leozeballos.FastFood.foodorder.FoodOrder;

import java.time.LocalDateTime;

public class Done extends FoodOrderState {

    @Override
    public void startPreparation(FoodOrder order) throws IllegalStateException {
        throw new IllegalStateException("Order is already done.");
    }

    @Override
    public void finishPreparation(FoodOrder order) throws IllegalStateException {
        throw new IllegalStateException("Order is already done.");
    }

    @Override
    public void cancelOrder(FoodOrder order) throws IllegalStateException {
        throw new IllegalStateException("Order is already done.");
    }

    @Override
    public void confirmPayment(FoodOrder order) {
        order.setPaymentTimestamp(java.sql.Timestamp.valueOf(LocalDateTime.now()));
        order.setState(new Paid());
    }

    @Override
    public void rejectOrder(FoodOrder order) {
        order.setState(new Rejected());
    }

}