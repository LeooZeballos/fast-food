package net.leozeballos.FastFood.foodorder;

import lombok.*;
import net.leozeballos.FastFood.FoodOrderStateMachine.FoodOrderState;
import net.leozeballos.FastFood.branch.Branch;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.time.LocalDateTime;
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
    private final java.sql.Timestamp creationTimestamp = java.sql.Timestamp.valueOf(LocalDateTime.now());

    /**
     * The payment date and time of the food order.
     */
    @Column
    private java.sql.Timestamp paymentTimestamp;

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

}