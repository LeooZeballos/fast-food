package net.leozeballos.FastFood.product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.leozeballos.FastFood.item.Item;

import javax.persistence.Column;
import javax.persistence.Entity;

@Getter
@Setter
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

    public Product(String name, double price) {
        super(name);
        this.price = price;
    }

    @Override
    public String toString() {
        return "Product{" +
                "name='" + getName() + '\'' +
                ", price=" + getPrice() +
                '}';
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
