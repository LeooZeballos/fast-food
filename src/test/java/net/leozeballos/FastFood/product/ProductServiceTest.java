package net.leozeballos.FastFood.product;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock private ProductRepository productRepository;
    private AutoCloseable autoCloseable;
    private ProductService underTest;

    @BeforeEach
    void setUp() {
        underTest = new ProductService(productRepository);
    }

    @Test
    void canFindAllProducts() {
        // when
        underTest.findAll();

        // then
        verify(productRepository).findAll();
    }

    @Disabled
    @Test
    void findById() {
    }

    @Test
    void canSaveProduct() {
        // given
        Product product = Product.builder().build();
        product.setName("Test Product");
        product.setPrice(10.0);

        // when
        underTest.save(product);

        // then
        ArgumentCaptor<Product> captor = ArgumentCaptor.forClass(Product.class);
        verify(productRepository).save(captor.capture());
        assertThat(captor.getValue()).isEqualTo(product);
    }

    @Disabled
    @Test
    void delete() {
    }

    @Disabled
    @Test
    void deleteById() {
    }

    @Disabled
    @Test
    void deleteAll() {
    }
}