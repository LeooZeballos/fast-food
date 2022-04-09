package net.leozeballos.FastFood.menu;

import net.leozeballos.FastFood.interfaces.Item;
import net.leozeballos.FastFood.product.Product;

import javax.persistence.*;
import java.util.List;

@Entity
public class Menu implements Item {

    private Long id;
    private double discount; // between 0 and 1
    private String name;
    private List<Product> products;

    public Menu() {
    }

    public Menu(String name, double discount) {
        this.name = name;
        this.discount = discount;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(nullable = false, precision = 2, scale = 1)
    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    @Column(nullable = false, length = 50)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @ManyToMany(cascade = CascadeType.PERSIST)
    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
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

    public boolean addProduct(Product product) {
        return products.add(product);
    }

    public boolean removeProduct(Product product) {
        return products.remove(product);
    }

}
