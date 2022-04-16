package net.leozeballos.FastFood.menu;

import net.leozeballos.FastFood.product.Product;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MenuTest {

    @Test
    void shouldCreateMenuWithBuilder() {
        Menu menu = Menu.builder().discount(0.1).products(new ArrayList<>()).build();
        assertNotNull(menu);
        assertNull(menu.getId());
        assertNull(menu.getName());
        assertEquals(0.1, menu.getDiscount());
        assertEquals(0, menu.getProducts().size());
    }

    @Test
    void shouldCreateMenuUsingEmptyConstructor() {
        Menu menu = new Menu();
        assertNotNull(menu);
        assertNull(menu.getId());
        assertNull(menu.getName());
        assertEquals(0.0, menu.getDiscount());
        assertEquals(0, menu.getProducts().size());
    }

    @Test
    void testHashCode() {
        // given
        Menu menu1 = Menu.builder().build();
        Menu menu2 = Menu.builder().build();

        // when
        int hashCode1 = menu1.hashCode();
        int hashCode2 = menu2.hashCode();

        // then
        assertEquals(hashCode1, hashCode2);
    }

    @Test
    void canCalculatePrice() {
        // given
        Menu menu = Menu.builder().build();
        Product product1 = Product.builder().price(10.0).build();
        Product product2 = Product.builder().price(20.0).build();
        Product product3 = Product.builder().price(30.0).build();
        List<Product> products = new ArrayList<>();
        products.add(product1);
        products.add(product2);
        products.add(product3);
        menu.setProducts(products);

        // when
        double price = menu.calculatePrice();

        // then
        assertEquals(60.0, price);
    }

    @Test
    void getFormattedTotal() {
        // given
        Menu menu = Menu.builder().build();
        Product product1 = Product.builder().price(10.0).build();
        Product product2 = Product.builder().price(20.0).build();
        Product product3 = Product.builder().price(30.0).build();
        List<Product> products = new ArrayList<>();
        products.add(product1);
        products.add(product2);
        products.add(product3);
        menu.setProducts(products);

        // when
        String total = menu.getFormattedTotal();

        // then
        assertEquals("$60,00", total);
    }

    @Test
    void getFormattedDiscount() {
        // given
        Menu menu = Menu.builder()
                .discount(0.1)
                .build();

        // when
        String discount = menu.getFormattedDiscount();

        // then
        assertEquals("10%", discount);
    }

    @Test
    void canGetAListOfProducts() {
        // given
        Menu menu = Menu.builder().build();
        Product product1 = Product.builder().price(10.0).build();
        product1.setName("Product 1");
        Product product2 = Product.builder().price(20.0).build();
        product2.setName("Product 2");
        Product product3 = Product.builder().price(30.0).build();
        product3.setName("Product 3");
        List<Product> products = new ArrayList<>();
        products.add(product1);
        products.add(product2);
        products.add(product3);
        menu.setProducts(products);

        // when
        String listProducts = menu.listProducts();

        // then
        assertEquals("Product 1, Product 2, Product 3", listProducts);
    }

    @Test
    void cantGetAListOfProductsWithEmptyList() {
        // given
        Menu menu = new Menu();

        // when
        String listProducts = menu.listProducts();

        // then
        assertEquals("None", listProducts);
    }

    @Test
    void canGetDiscount() {
        // given
        Menu menu = Menu.builder()
                .discount(0.1)
                .build();

        // when
        double discount = menu.getDiscount();

        // then
        assertEquals(0.1, discount);
    }

    @Test
    void canGetProducts() {
        // given
        Menu menu = Menu.builder().build();
        Product product1 = Product.builder().price(10.0).build();
        Product product2 = Product.builder().price(20.0).build();
        Product product3 = Product.builder().price(30.0).build();
        List<Product> products = new ArrayList<>();
        products.add(product1);
        products.add(product2);
        products.add(product3);
        menu.setProducts(products);

        // when
        List<Product> products1 = menu.getProducts();

        // then
        assertEquals(products, products1);
    }

    @Test
    void canSetDiscount() {
        // given
        Menu menu = Menu.builder().build();

        // when
        menu.setDiscount(0.1);

        // then
        assertEquals(0.1, menu.getDiscount());
    }

    @Test
    void canSetProducts() {
        // given
        Menu menu = Menu.builder().build();
        Product product1 = Product.builder().price(10.0).build();
        Product product2 = Product.builder().price(20.0).build();
        Product product3 = Product.builder().price(30.0).build();
        List<Product> products = new ArrayList<>();
        products.add(product1);
        products.add(product2);
        products.add(product3);

        // when
        menu.setProducts(products);

        // then
        assertEquals(products, menu.getProducts());
    }

    @Test
    void testToString() {
        // given
        Menu menu = Menu.builder().build();
        Product product1 = Product.builder().price(10.0).build();
        product1.setName("Product 1");
        Product product2 = Product.builder().price(20.0).build();
        product2.setName("Product 2");
        Product product3 = Product.builder().price(30.0).build();
        product3.setName("Product 3");
        List<Product> products = new ArrayList<>();
        products.add(product1);
        products.add(product2);
        products.add(product3);
        menu.setProducts(products);

        // when
        String toString = menu.toString();

        // then
        assertEquals("Menu(discount=0.0)", toString);
    }

}