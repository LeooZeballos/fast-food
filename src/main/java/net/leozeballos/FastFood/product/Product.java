package net.leozeballos.FastFood.product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.leozeballos.FastFood.item.Item;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.DecimalMin;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@DiscriminatorValue("PRODUCT")
public class Product extends Item {

    /**
     * The price of the product.
     */
    @Column(nullable = true)
    @DecimalMin(value = "0.0")
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

}
