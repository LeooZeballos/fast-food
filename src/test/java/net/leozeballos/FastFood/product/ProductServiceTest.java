package net.leozeballos.FastFood.product;

import net.leozeballos.FastFood.menu.MenuRepository;
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
    @Mock private MenuRepository menuRepository;
    private ProductService underTest;

    @BeforeEach
    void setUp() {
        underTest = new ProductService(productRepository, menuRepository);
    }

    @Test
    void canFindAllProducts() {
        // when
        underTest.findAll();

        // then
        verify(productRepository).findAll();
    }

    @Test
    void canFindProductById() {
        // given
        Long id = 1L;

        // when
        underTest.findById(id);

        // then
        verify(productRepository).findById(id);
    }

    @Test
    void canSaveProduct() {
        // given
        Product product = Product.builder().build();

        // when
        underTest.save(product);

        // then
        ArgumentCaptor<Product> captor = ArgumentCaptor.forClass(Product.class);
        verify(productRepository).save(captor.capture());
        assertThat(captor.getValue()).isEqualTo(product);
    }

    @Test
    void canDeleteProduct() {
        // given
        Product product = Product.builder().build();

        // when
        underTest.delete(product);

        // then
        ArgumentCaptor<Product> captor = ArgumentCaptor.forClass(Product.class);
        verify(productRepository).delete(captor.capture());
        assertThat(captor.getValue()).isEqualTo(product);
    }

    @Test
    void canDeleteProductById() {
        // given
        Long id = 1L;

        // when
        underTest.deleteById(id);

        // then
        verify(productRepository).deleteById(id);
    }

    @Test
    void canDeleteAllProducts() {
        // when
        underTest.deleteAll();

        // then
        verify(productRepository).deleteAll();
    }

}