package net.leozeballos.FastFood.product;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(properties = "spring.datasource.url=jdbc:h2:mem:testdb")
class ProductFilterIntegrationTest {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    @BeforeEach
    void setUp() {
        Product p1 = new Product();
        p1.setName("Burger");
        p1.setPrice(10.0);
        p1.enable();

        Product p2 = new Product();
        p2.setName("Cheese Burger");
        p2.setPrice(15.0);
        p2.enable();

        Product p3 = new Product();
        p3.setName("Salad");
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
        List<ProductDTO> results = productService.findAllDTO("burger", null, null);
        assertThat(results).hasSize(2);
        assertThat(results).extracting(ProductDTO::name)
                .containsExactlyInAnyOrder("Burger", "Cheese Burger");
    }

    @Test
    void shouldFilterByMaxPrice() {
        List<ProductDTO> results = productService.findAllDTO(null, 10.0, null);
        assertThat(results).hasSize(2);
        assertThat(results).extracting(ProductDTO::name)
                .containsExactlyInAnyOrder("Burger", "Salad");
    }

    @Test
    void shouldFilterByActiveStatus() {
        List<ProductDTO> results = productService.findAllDTO(null, null, true);
        assertThat(results).hasSize(2);
        assertThat(results).extracting(ProductDTO::name)
                .containsExactlyInAnyOrder("Burger", "Cheese Burger");
    }

    @Test
    void shouldCombineFilters() {
        List<ProductDTO> results = productService.findAllDTO("burger", 12.0, true);
        assertThat(results).hasSize(1);
        assertThat(results.get(0).name()).isEqualTo("Burger");
    }

    @Test
    void shouldReturnEmptyWhenNoMatch() {
        List<ProductDTO> results = productService.findAllDTO("Pizza", null, null);
        assertThat(results).isEmpty();
    }
}
