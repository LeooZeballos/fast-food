package net.leozeballos.FastFood.product;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(properties = "spring.datasource.url=jdbc:h2:mem:testdb")
@Transactional
class ProductFilterIntegrationTest {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    @BeforeEach
    void setUp() {
        Product p1 = new Product();
        p1.setName("Test Burger");
        p1.setPrice(10.0);
        p1.enable();

        Product p2 = new Product();
        p2.setName("Test Cheese Burger");
        p2.setPrice(15.0);
        p2.enable();

        Product p3 = new Product();
        p3.setName("Test Salad");
        p3.setPrice(8.0);
        p3.disable();

        productRepository.saveAll(List.of(p1, p2, p3));
    }

    @AfterEach
    void tearDown() {
        productRepository.deleteAll();
    }

    @Test
    void shouldFilterByName() {
        List<ProductDTO> results = productService.findAllDTO("test burger", null, null);
        assertThat(results).hasSize(1);
        assertThat(results.get(0).name()).isEqualTo("Test Burger");
    }

    @Test
    void shouldFilterByMaxPrice() {
        // Should find Test Burger (10) and Test Salad (8) plus seeded products <= 10
        List<ProductDTO> results = productService.findAllDTO("test", 10.0, null);
        assertThat(results).hasSize(2);
        assertThat(results).extracting(ProductDTO::name)
                .containsExactlyInAnyOrder("Test Burger", "Test Salad");
    }

    @Test
    void shouldFilterByActiveStatus() {
        List<ProductDTO> results = productService.findAllDTO("test", null, true);
        assertThat(results).hasSize(2);
        assertThat(results).extracting(ProductDTO::name)
                .containsExactlyInAnyOrder("Test Burger", "Test Cheese Burger");
    }

    @Test
    void shouldCombineFilters() {
        List<ProductDTO> results = productService.findAllDTO("test burger", 12.0, true);
        assertThat(results).hasSize(1);
        assertThat(results.get(0).name()).isEqualTo("Test Burger");
    }

    @Test
    void shouldReturnEmptyWhenNoMatch() {
        List<ProductDTO> results = productService.findAllDTO("Pizza", null, null);
        assertThat(results).isEmpty();
    }
}
