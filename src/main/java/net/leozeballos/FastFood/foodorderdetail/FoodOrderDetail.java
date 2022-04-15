package net.leozeballos.FastFood.foodorderdetail;

import lombok.*;
import net.leozeballos.FastFood.item.Item;
import net.leozeballos.FastFood.menu.Menu;
import net.leozeballos.FastFood.product.Product;
import org.hibernate.Hibernate;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.boot.context.properties.bind.DefaultValue;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.Objects;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class FoodOrderDetail {

    /**
     * Unique identifier for the food order detail.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The price of the item that is being ordered at the time of the order.
     * Equals item.calculatePrice() at the time of the order.
     */
    @Column(nullable = false, precision = 2, updatable = false)
    private double historicPrice;

    /**
     * The quantity of the item that is being ordered.
     */
    @Column(nullable = false)
    @ColumnDefault(value = "1")
    @Min(value = 1)
    @Max(value = 99)
    private Integer quantity;

    /**
     * The item that is being ordered. Can be a product or a menu.
     */
    @ManyToOne
    @JoinColumn(nullable = false)
    private Item item;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        FoodOrderDetail that = (FoodOrderDetail) o;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    /**
     * Calculates the subtotal of the order detail.
     * @return double The subtotal price of the order detail that is being ordered.
     */
    public double calculateSubtotal() {
        if (historicPrice == 0) {
            this.setHistoricPrice(item.calculatePrice());
        }
        return historicPrice * quantity;
    }

}
