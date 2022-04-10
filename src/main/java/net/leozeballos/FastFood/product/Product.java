package net.leozeballos.FastFood.product;

import net.leozeballos.FastFood.item.Item;
import net.leozeballos.FastFood.menu.Menu;

import javax.persistence.*;
import java.util.Collection;

@Entity
public class Product implements Item {

    @Id
    @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false)
    private double price;

    @ManyToMany(mappedBy = "products")
    private Collection<Menu> menus;

    public Product() {
    }

    public Product(String name, double price) {
        this.name = name;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Collection<Menu> getMenus() {
        return menus;
    }

    public void setMenus(Collection<Menu> menus) {
        this.menus = menus;
    }

    @Override
    public String toString() {
        return "Product [name=" + name + ", price=" + price + ", id=" + id + "]";
    }

    public double calculatePrice() {
        return price;
    }

    public String getFormattedPrice() {
        return "$" + String.format("%.2f", price);
    }

}
