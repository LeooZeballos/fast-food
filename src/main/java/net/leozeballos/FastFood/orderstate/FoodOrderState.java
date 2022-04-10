package net.leozeballos.FastFood.orderstate;

import net.leozeballos.FastFood.foodorder.FoodOrder;

import javax.persistence.*;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class FoodOrderState {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    @Column(nullable = false)
    protected String name;

    @Column(nullable = false)
    protected String description;

    public FoodOrderState() {
    }

    public FoodOrderState(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void startPreparation(FoodOrder order) {}

    public void finishPreparation(FoodOrder order) {}

    public void cancelOrder(FoodOrder order) {}

    public void confirmPayment(FoodOrder order) {}

    public void rejectOrder(FoodOrder order) {}

}