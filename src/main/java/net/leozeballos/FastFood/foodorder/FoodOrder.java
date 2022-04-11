package net.leozeballos.FastFood.foodorder;

import lombok.*;
import net.leozeballos.FastFood.branch.Branch;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class FoodOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private final java.sql.Timestamp creationTimestamp = java.sql.Timestamp.valueOf(LocalDateTime.now());

    @Column
    private java.sql.Timestamp paymentTimestamp;

    @Enumerated(EnumType.STRING)
    private FoodOrderState state;

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