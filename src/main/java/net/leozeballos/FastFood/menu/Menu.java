package net.leozeballos.FastFood.menu;

import net.leozeballos.FastFood.item.Item;
import net.leozeballos.FastFood.product.Product;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Menu implements Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, precision = 2, scale = 1)
    private double discount; // between 0 and 1

    @Column(nullable = false, length = 50)
    private String name;

    @ManyToMany(cascade = CascadeType.PERSIST)
    @JoinTable(
            name = "menu_product",
            joinColumns = @JoinColumn(name = "menu_id"),
            inverseJoinColumns = @JoinColumn(name = "product_id")
    )
    private Set<Product> products = new HashSet<>();

    public Menu() {
    }

    public Menu(String name, double discount) {
        this.name = name;
        this.discount = discount;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Product> getProducts() {
        return products;
    }

    public void setProducts(Set<Product> products) {
        this.products = products;
    }


    @Override
    public String toString() {
        return "Menu{" +
                "id=" + id +
                ", discount=" + discount +
                ", name='" + name + '\'' +
                ", products=" + products +
                '}';
    }

    public double calculatePrice() {
        double price = 0;
        for (Product product : products) {
            price += product.getPrice();
        }
        return price * (1 - discount);
    }

    public String getFormattedTotal() {
        return "$" + String.format("%.2f", calculatePrice());
    }

    public String getFormattedDiscount() {
        return String.format("%.0f", discount * 100) + "%";
    }

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
