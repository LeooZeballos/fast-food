package net.leozeballos.FastFood.config;

import lombok.RequiredArgsConstructor;
import net.leozeballos.FastFood.address.Address;
import net.leozeballos.FastFood.branch.Branch;
import net.leozeballos.FastFood.branch.BranchRepository;
import net.leozeballos.FastFood.menu.Menu;
import net.leozeballos.FastFood.menu.MenuRepository;
import net.leozeballos.FastFood.product.Product;
import net.leozeballos.FastFood.product.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Configuration
@Profile("dev")
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final BranchRepository branchRepository;
    private final ProductRepository productRepository;
    private final MenuRepository menuRepository;
    private final net.leozeballos.FastFood.inventory.InventoryRepository inventoryRepository;

    @Override
    @Transactional
    public void run(String... args) {
        if (branchRepository.count() == 0) {
            // ... (keep branch logic)
        }

        // --- INVENTORY INITIALIZATION ---
        if (inventoryRepository.count() == 0) {
            List<Branch> branches = branchRepository.findAll();
            List<net.leozeballos.FastFood.item.Item> items = productRepository.findAll().stream().map(p -> (net.leozeballos.FastFood.item.Item)p).collect(Collectors.toList());
            items.addAll(menuRepository.findAll());

            for (Branch branch : branches) {
                for (net.leozeballos.FastFood.item.Item item : items) {
                    inventoryRepository.save(net.leozeballos.FastFood.inventory.Inventory.builder()
                            .branch(branch)
                            .item(item)
                            .stockQuantity(100) // Initial stock for all items
                            .isAvailable(true)
                            .build());
                }
            }
            System.out.println("--- Inventory Initialized successfully ---");
        }

        if (productRepository.count() == 0) {
            // ... (rest of the file)

        }

        if (menuRepository.count() == 0) {
            // --- PRODUCTS (Fetch existing if needed) ---
            List<Product> products = productRepository.findAll();
            if (products.isEmpty()) {
                // Initialize products if they don't exist
                Product p1 = new Product("Classic Burger", 8.50);
                p1.setIcon("burger");
                Product p2 = new Product("Cheese Deluxe", 10.00);
                p2.setIcon("burger");
                Product p3 = new Product("Bacon King", 12.50);
                p3.setIcon("burger");
                Product p4 = new Product("French Fries (L)", 4.00);
                p4.setIcon("fries");
                Product p5 = new Product("Onion Rings", 4.50);
                p5.setIcon("fries");
                Product p6 = new Product("Coca-Cola 500ml", 3.00);
                p6.setIcon("drink");
                Product p7 = new Product("Craft Beer", 6.00);
                p7.setIcon("beer");
                Product p8 = new Product("Vanilla Shake", 5.50);
                p8.setIcon("shake");

                products = productRepository.saveAll(List.of(p1, p2, p3, p4, p5, p6, p7, p8));
            }

            Product p1 = products.stream().filter(p -> p.getName().contains("Classic Burger")).findFirst().orElse(null);
            Product p2 = products.stream().filter(p -> p.getName().contains("Cheese Deluxe")).findFirst().orElse(null);
            Product p3 = products.stream().filter(p -> p.getName().contains("Bacon King")).findFirst().orElse(null);
            Product p4 = products.stream().filter(p -> p.getName().contains("French Fries")).findFirst().orElse(null);
            Product p5 = products.stream().filter(p -> p.getName().contains("Onion Rings")).findFirst().orElse(null);
            Product p6 = products.stream().filter(p -> p.getName().contains("Coca-Cola")).findFirst().orElse(null);
            Product p7 = products.stream().filter(p -> p.getName().contains("Craft Beer")).findFirst().orElse(null);

            Menu m1 = new Menu();
            m1.setName("Classic Combo");
            m1.setIcon("combo");
            m1.setDiscount(new BigDecimal("0.1")); // 10% OFF
            if (p1 != null && p4 != null && p6 != null) m1.setItems(List.of(p1, p4, p6));
            
            Menu m2 = new Menu();
            m2.setName("Bacon Lovers Feast");
            m2.setIcon("combo");
            m2.setDiscount(new BigDecimal("0.15")); // 15% OFF
            if (p3 != null && p5 != null && p7 != null) m2.setItems(List.of(p3, p5, p7));

            Menu m3 = new Menu();
            m3.setName("Double Cheese Special");
            m3.setIcon("combo");
            m3.setDiscount(new BigDecimal("0.2")); // 20% OFF
            if (p2 != null && p4 != null && p6 != null) m3.setItems(List.of(p2, p2, p4, p6)); // Double burger!

            menuRepository.saveAll(List.of(m1, m2, m3));
            System.out.println("--- Test Menus Initialized successfully ---");
        }
    }
}
