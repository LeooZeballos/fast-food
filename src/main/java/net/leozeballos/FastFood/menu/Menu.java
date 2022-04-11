package net.leozeballos.FastFood.menu;

import lombok.*;
import net.leozeballos.FastFood.item.Item;
import net.leozeballos.FastFood.product.Product;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Menu implements Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The discount of the menu. This is a percentage. For example, a discount of 10% would be represented as 0.1.
     */
    @Column(nullable = false, precision = 2, scale = 1)
    private double discount;

    /**
     * The name of the menu. String of length 1 to 50. Must be unique.
     */
    @Column(nullable = false, length = 50, unique = true)
    private String name;

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
    private Set<Product> products = new HashSet<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Menu menu = (Menu) o;
        return id != null && Objects.equals(id, menu.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    /**
     * Calculates the price of the menu. This is the sum of the prices of all products in the menu times 1 - discount.
     * @return double The price of the menu.
     */
    public double calculatePrice() {
        double price = 0;
        for (Product product : products) { price += product.getPrice(); }
        return price * (1 - discount);
    }

    /**
     * Formats the total price of the menu. Example: $5.00
     * @return String The formatted total price of the menu.
     */
    public String getFormattedTotal() {
        return "$" + String.format("%.2f", calculatePrice());
    }

    /**
     * Formats the discount of the menu. Example: 0.1 -> 10%
     * @return String The formatted discount percentage of the menu.
     */
    public String getFormattedDiscount() {
        return String.format("%.0f", discount * 100) + "%";
    }

    /**
     * Returns a list of all products in the menu separated by a comma. Returns "None" if the menu is empty.
     * @return String The list of all products in the menu.
     */
    public String listProducts() {
        // Lists all products in the menu separated by a comma without the last comma
        StringBuilder sb = new StringBuilder();
        for (Product product : products) {
            sb.append(product.getName()).append(", ");
        }
        if (sb.length() > 0) {
            sb.delete(sb.length() - 2, sb.length());
        } else {
            sb.append("None");
        }
        return sb.toString();
    }

}
