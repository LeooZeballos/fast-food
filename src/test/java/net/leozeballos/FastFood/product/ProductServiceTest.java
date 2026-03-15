package net.leozeballos.FastFood.product;

import net.leozeballos.FastFood.mapper.ProductMapper;
import net.leozeballos.FastFood.menu.MenuRepository;
import net.leozeballos.FastFood.foodorder.FoodOrderRepository;
import net.leozeballos.FastFood.util.AuditService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock private ProductRepository productRepository;
    @Mock private MenuRepository menuRepository;
    @Mock private FoodOrderRepository foodOrderRepository;
    @Mock private AuditService auditService;
    @Spy private ProductMapper productMapper;
    private ProductService underTest;

    @BeforeEach
    void setUp() {
        underTest = new ProductService(productRepository, menuRepository, foodOrderRepository, productMapper, auditService);
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
        Product product = new Product();
        when(productRepository.findById(id)).thenReturn(Optional.of(product));

        // when
        underTest.findById(id);

        // then
        verify(productRepository).findById(id);
    }

    @Test
    void canSaveProduct() {
        // given
        Product product = new Product();

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
        Product product = new Product();

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
        Product product = new Product();
        product.setId(id);
        product.setName("Test Product");
        when(productRepository.findById(id)).thenReturn(Optional.of(product));

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

    @Test
    void canConvertToDTO() {
        // given
        Product product = new Product();
        product.setId(1L);
        product.setName("Product 1");
        product.setPrice(10.0);
        product.enable();

        // when
        ProductDTO dto = productMapper.toDTO(product);

        // then
        assertThat(dto.id()).isEqualTo(1L);
        assertThat(dto.name()).isEqualTo("Product 1");
        assertThat(dto.price()).isEqualTo(10.0);
        assertThat(dto.active()).isTrue();
    }

}
