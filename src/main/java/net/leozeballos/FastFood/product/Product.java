package net.leozeballos.FastFood.product;

import lombok.*;
import net.leozeballos.FastFood.item.Item;
import net.leozeballos.FastFood.menu.Menu;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.util.Collection;
import java.util.Objects;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Product implements Item {

    /**
     * The unique identifier of the product.
     */
    @Id
    @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
    private Long id;

    /**
     * The name of the product.
     */
    @Column(nullable = false, length = 50)
    private String name;

    /**
     * The price of the product.
     */
    @Column(nullable = false)
    private double price;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Product product = (Product) o;
        return id != null && Objects.equals(id, product.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    /**
     * Item's interface implementation
     * @return double Product's price
     */
    @Override
    public double calculatePrice() {
        return price;
    }

    /**
     * Formats the Product's price. Example: $1.00
     * @return String Product's price formatted
     */
    public String getFormattedPrice() {
        return "$" + String.format("%.2f", price);
    }

}
