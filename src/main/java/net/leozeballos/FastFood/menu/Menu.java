package net.leozeballos.FastFood.menu;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import net.leozeballos.FastFood.item.Item;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
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
@DiscriminatorValue("MENU")
public class Menu extends Item {

    /**
     * The discount of the menu. This is a percentage. For example, a discount of 10% would be represented as 0.1.
     */
    @Column(nullable = true, precision = 2, scale = 1)
    private BigDecimal discount;

    /**
     * The items that are in the menu. This can be Products or other Menus.
     */
    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "menu_item",
            joinColumns = @JoinColumn(name = "menu_id"),
            inverseJoinColumns = @JoinColumn(name = "item_id")
    )
    @ToString.Exclude
    @Builder.Default
    private List<Item> items = new ArrayList<>();

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    /**
     * Calculates the price of the menu recursively.
     * @return double The price of the menu.
     */
    @Override
    public double calculatePrice() {
        double price = 0;
        for (Item item : items) { price += item.calculatePrice(); }
        return price * (1 - discount.doubleValue());
    }

}
