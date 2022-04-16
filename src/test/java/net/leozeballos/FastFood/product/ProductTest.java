package net.leozeballos.FastFood.product;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProductTest {

    @Test
    void canCreateUsingEmptyConstructor() {
        Product product = new Product();
        assertNull(product.getId());
        assertNull(product.getName());
        assertEquals(0.0, product.getPrice());
    }

    @Test
    void shouldCreateProductWithAttr() {
        Product product = new Product("Product", 1.0);
        assertNotNull(product);
        assertEquals("Product", product.getName());
        assertEquals(1.0, product.getPrice());
    }

    @Test
    void shouldCreateProductWithAttrUsingBuilder() {
        Product product = Product.builder().build();
        product.setName("Product");
        product.setPrice(1.0);
        assertNotNull(product);
        assertEquals("Product", product.getName());
        assertEquals(1.0, product.getPrice());
    }

    @Test
    void testHashCode() {
        // given
        Product product1 = Product.builder().build();
        product1.setName("Product");
        product1.setPrice(1.0);
        Product product2 = Product.builder().build();
        product2.setName("Product");
        product2.setPrice(1.0);

        // when
        int result = product1.hashCode();

        // then
        assertEquals(product2.hashCode(), result);
    }

    @Test
    void canCalculatePrice() {
        // given
        Product product = Product.builder().build();
        product.setName("Product");
        double price = 1.0;
        product.setPrice(price);

        // when
        double result = product.calculatePrice();

        // then
        assertEquals(price, result);
    }

    @Test
    void canGetFormattedPrice() {
        // given
        Product product = Product.builder().build();
        product.setName("Product");
        product.setPrice(1.0);

        // when
        String result = product.getFormattedPrice();

        // then
        assertEquals("$1,00", result);
    }

    @Test
    void canGetPrice() {
        // given
        Product product = Product.builder().build();
        product.setName("Product");
        product.setPrice(1.0);

        // when
        double result = product.getPrice();

        // then
        assertEquals(1.0, result);
    }

    @Test
    void canSetPrice() {
        // given
        Product product = Product.builder().build();
        product.setName("Product");
        double price = 1.0;

        // when
        product.setPrice(price);

        // then
        assertEquals(price, product.getPrice());
    }

    @Test
    void testToString() {
        // given
        Product product = Product.builder().build();
        product.setName("Product");
        product.setPrice(1.0);

        // when
        String result = product.toString();

        // then
        String expected = "Product{name='Product', price=1.0}";
        assertEquals(expected, result);
    }

}