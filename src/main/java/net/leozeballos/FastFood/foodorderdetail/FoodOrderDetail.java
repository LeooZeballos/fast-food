package net.leozeballos.FastFood.foodorderdetail;

import lombok.*;
import net.leozeballos.FastFood.item.Item;
import net.leozeballos.FastFood.menu.Menu;
import net.leozeballos.FastFood.product.Product;
import org.hibernate.Hibernate;

import javax.persistence.*;
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
     */
    @Column(nullable = false, precision = 2, updatable = false)
    private double historicPrice;

    /**
     * The amount of the item that is being ordered.
     */
    @Column(nullable = false)
    private int amount;

    /**
     * The item that is being ordered. Can be a product or a menu.
     */
    @Transient
    private Item item;

    @Convert(converter= Product.class)
    @Column(name="item_id_product")
    private Item itemProduct;

    @Convert(converter= Menu.class)
    @Column(name="role_id_menu")
    private Item itemMenu;

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

    public double calculateSubtotal() {
        return historicPrice * amount;
    }

}
