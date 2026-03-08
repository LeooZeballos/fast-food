package net.leozeballos.FastFood.menu;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import net.leozeballos.FastFood.item.Item;
import net.leozeballos.FastFood.product.Product;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Menu extends Item {

    /**
     * The discount of the menu. This is a percentage. For example, a discount of 10% would be represented as 0.1.
     */
    @Column(nullable = false, precision = 2, scale = 1)
    private BigDecimal discount;

    /**
     * The products that are in the menu. This is a many-to-many relationship.
     */
    @ManyToMany(cascade = CascadeType.PERSIST)
    @JoinTable(
            name = "menu_product",
            joinColumns = @JoinColumn(name = "menu_id"),
            inverseJoinColumns = @JoinColumn(name = "product_id")
    )
    @ToString.Exclude
    @Builder.Default
    private List<Product> products = new ArrayList<>();

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    /**
     * Calculates the price of the menu. This is the sum of the prices of all products in the menu times 1 - discount.
     * @return double The price of the menu.
     */
    @Override
    public double calculatePrice() {
        double price = 0;
        for (Product product : products) { price += product.calculatePrice(); }
        return price * (1 - discount.doubleValue());
    }

}
