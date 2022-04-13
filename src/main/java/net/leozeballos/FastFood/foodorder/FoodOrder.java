package net.leozeballos.FastFood.foodorder;

import lombok.*;
import net.leozeballos.FastFood.foodorderdetail.FoodOrderDetail;
import net.leozeballos.FastFood.foodorderstatemachine.FoodOrderState;
import net.leozeballos.FastFood.branch.Branch;
import org.hibernate.Hibernate;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class FoodOrder {

    /**
     * The unique identifier of the food order.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The creation date and time of the food order.
     */
    @Column(nullable = false)
    @CreationTimestamp
    private LocalDateTime creationTimestamp;

    /**
     * The payment date and time of the food order.
     */
    @Column
    private LocalDateTime paymentTimestamp;

    /**
     * The status of the food order.
     */
    @Enumerated(EnumType.STRING)
    private FoodOrderState state;

    /**
     * The branch that the food order is for.
     */
    @ManyToOne(optional = false)
    @JoinColumn(name = "branch_id", nullable = false)
    private Branch branch;

    /**
     * The food order details of the food order.
     */
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private List<FoodOrderDetail> foodOrderDetails = new ArrayList<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        FoodOrder foodOrder = (FoodOrder) o;
        return id != null && Objects.equals(id, foodOrder.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    /**
     * Returns the total price of the food order.
     * @return double The total price of the food order
     */
    public double calculateTotal() {
        double total = 0.0;
        for (FoodOrderDetail foodOrderDetail : foodOrderDetails) {
            total += foodOrderDetail.getQuantity() * foodOrderDetail.getItem().calculatePrice();
        }
        return total;
    }

}