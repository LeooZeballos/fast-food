package net.leozeballos.FastFood.product;

import lombok.*;
import net.leozeballos.FastFood.item.Item;
import org.hibernate.Hibernate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Objects;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class Product extends Item {

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
        return this.getId() != null && Objects.equals(this.getId(), product.getId());
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
