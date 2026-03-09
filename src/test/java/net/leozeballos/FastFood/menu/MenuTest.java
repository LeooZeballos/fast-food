package net.leozeballos.FastFood.menu;

import net.leozeballos.FastFood.item.Item;
import net.leozeballos.FastFood.product.Product;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MenuTest {

    @Test
    void shouldCreateMenuWithBuilder() {
        Menu menu = Menu.builder().discount(BigDecimal.valueOf(0.1)).items(new ArrayList<>()).build();
        assertNotNull(menu);
        assertNull(menu.getId());
        assertNull(menu.getName());
        assertEquals(BigDecimal.valueOf(0.1), menu.getDiscount());
        assertEquals(0, menu.getItems().size());
    }

    @Test
    void shouldCreateMenuUsingEmptyConstructor() {
        Menu menu = new Menu();
        assertNotNull(menu);
        assertNull(menu.getId());
        assertNull(menu.getName());
        assertNull(menu.getDiscount());
        assertEquals(0, menu.getItems().size());
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
        Menu menu = Menu.builder().discount(BigDecimal.ZERO).build();
        Product product1 = Product.builder().price(10.0).build();
        Product product2 = Product.builder().price(20.0).build();
        Product product3 = Product.builder().price(30.0).build();
        List<Item> items = new ArrayList<>();
        items.add(product1);
        items.add(product2);
        items.add(product3);
        menu.setItems(items);

        // when
        double price = menu.calculatePrice();

        // then
        assertEquals(60.0, price);
    }

    @Test
    void canGetDiscount() {
        // given
        Menu menu = Menu.builder()
                .discount(BigDecimal.valueOf(0.1))
                .build();

        // when
        BigDecimal discount = menu.getDiscount();

        // then
        assertEquals(BigDecimal.valueOf(0.1), discount);
    }

    @Test
    void canGetItems() {
        // given
        Menu menu = Menu.builder().build();
        Product product1 = Product.builder().price(10.0).build();
        Product product2 = Product.builder().price(20.0).build();
        Product product3 = Product.builder().price(30.0).build();
        List<Item> items = new ArrayList<>();
        items.add(product1);
        items.add(product2);
        items.add(product3);
        menu.setItems(items);

        // when
        List<Item> items1 = menu.getItems();

        // then
        assertEquals(items, items1);
    }

    @Test
    void canSetDiscount() {
        // given
        Menu menu = Menu.builder().build();

        // when
        menu.setDiscount(BigDecimal.valueOf(0.1));

        // then
        assertEquals(BigDecimal.valueOf(0.1), menu.getDiscount());
    }

    @Test
    void canSetItems() {
        // given
        Menu menu = Menu.builder().build();
        Product product1 = Product.builder().price(10.0).build();
        Product product2 = Product.builder().price(20.0).build();
        Product product3 = Product.builder().price(30.0).build();
        List<Item> items = new ArrayList<>();
        items.add(product1);
        items.add(product2);
        items.add(product3);

        // when
        menu.setItems(items);

        // then
        assertEquals(items, menu.getItems());
    }

    @Test
    void testToString() {
        // given
        Menu menu = Menu.builder().discount(BigDecimal.ZERO).build();
        Product product1 = Product.builder().price(10.0).build();
        product1.setName("Product 1");
        Product product2 = Product.builder().price(20.0).build();
        product2.setName("Product 2");
        Product product3 = Product.builder().price(30.0).build();
        product3.setName("Product 3");
        List<Item> items = new ArrayList<>();
        items.add(product1);
        items.add(product2);
        items.add(product3);
        menu.setItems(items);

        // when
        String toString = menu.toString();

        // then
        assertTrue(toString.contains("discount=0"));
    }

}
