package net.leozeballos.FastFood.foodorder;

import lombok.*;
import net.leozeballos.FastFood.foodorderdetail.FoodOrderDetail;
import net.leozeballos.FastFood.foodorderstatemachine.FoodOrderState;
import net.leozeballos.FastFood.branch.Branch;
import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
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
    @Builder.Default
    private List<FoodOrderDetail> foodOrderDetails = new ArrayList<>();

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
            total += foodOrderDetail.calculateSubtotal();
        }
        return total;
    }

}
