package net.leozeballos.FastFood.foodorder;

import net.leozeballos.FastFood.branch.Branch;

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

    @Enumerated(EnumType.STRING)
    private FoodOrderState state;

    @ManyToOne(optional = false)
    @JoinColumn(name = "branch_id", nullable = false)
    private Branch branch;

    public FoodOrder() {
        this.creationTimestamp = java.sql.Timestamp.valueOf(LocalDateTime.now());
    }

    public FoodOrder(Branch branch) {
        this();
        this.branch = branch;
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

}