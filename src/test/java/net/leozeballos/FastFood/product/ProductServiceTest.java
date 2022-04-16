package net.leozeballos.FastFood.product;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock private ProductRepository productRepository;
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

    @Test
    void findById() {
        // given
        Product product = Product.builder().build();
        product.setName("Test Product");
        product.setPrice(10.0);

        // when
        underTest.findById(1L);

        // then
        verify(productRepository).findById(1L);

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

    @Test
    void delete() {
        // given
        Product product = Product.builder().build();
        product.setName("Test Product");
        product.setPrice(10.0);

        // when
        underTest.delete(product);

        // then
        ArgumentCaptor<Product> captor = ArgumentCaptor.forClass(Product.class);
        verify(productRepository).delete(captor.capture());
        assertThat(captor.getValue()).isEqualTo(product);
    }

    @Test
    void deleteById() {
        // given
        Product product = Product.builder().build();
        product.setName("Test Product");
        product.setPrice(10.0);

        // when
        underTest.deleteById(1L);

        // then
        verify(productRepository).deleteById(1L);
    }

    @Test
    void deleteAll() {
        // when
        underTest.deleteAll();

        // then
        verify(productRepository).deleteAll();
    }

}