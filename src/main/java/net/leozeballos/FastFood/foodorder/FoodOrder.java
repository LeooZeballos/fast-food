package net.leozeballos.FastFood.foodorder;

import net.leozeballos.FastFood.orderstate.FoodOrderState;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
public class FoodOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private final java.sql.Timestamp creationTimestamp;

    @Column
    private java.sql.Timestamp paymentTimestamp;

    public FoodOrder() {
        this.creationTimestamp = java.sql.Timestamp.valueOf(LocalDateTime.now());
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public java.sql.Timestamp getCreationTimestamp() {
        return creationTimestamp;
    }

    public java.sql.Timestamp getPaymentTimestamp() {
        return paymentTimestamp;
    }

    public void setPaymentTimestamp(java.sql.Timestamp paymentTimestamp) {
        this.paymentTimestamp = paymentTimestamp;
    }

    public void setState(FoodOrderState state) {

    }

}