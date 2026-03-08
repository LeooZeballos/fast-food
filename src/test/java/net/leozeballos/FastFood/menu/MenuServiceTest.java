package net.leozeballos.FastFood.menu;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class MenuServiceTest {

    @Mock private MenuRepository menuRepository;
    private MenuService underTest;

    @BeforeEach
    void setUp() {
        underTest = new MenuService(menuRepository);
    }

    @Test
    void canFindAllMenus() {
        // when
        underTest.findAll();

        // then
        verify(menuRepository).findAll();
    }

    @Test
    void canFindMenuById() {
        // given
        Long id = 1L;

        // when
        underTest.findById(id);

        // then
        verify(menuRepository).findById(id);
    }

    @Test
    void canSaveMenu() {
        // given
        Menu menu = new Menu();

        // when
        underTest.save(menu);

        // then
        verify(menuRepository).save(menu);
    }

    @Test
    void canDeleteMenu() {
        // given
        Menu menu = new Menu();

        // when
        underTest.delete(menu);

        // then
        ArgumentCaptor<Menu> captor = ArgumentCaptor.forClass(Menu.class);
        verify(menuRepository).delete(captor.capture());
        assertThat(captor.getValue()).isEqualTo(menu);
    }

    @Test
    void canDeleteMenuById() {
        // given
        Long id = 1L;

        // when
        underTest.deleteById(id);

        // then
        verify(menuRepository).deleteById(id);
    }

    @Test
    void canDeleteAllMenus() {
        // when
        underTest.deleteAll();

        // then
        verify(menuRepository).deleteAll();
    }

    @Test
    void canConvertToDTO() {
        // given
        net.leozeballos.FastFood.product.Product product1 = net.leozeballos.FastFood.product.Product.builder().price(10.0).build();
        product1.setName("Product 1");
        net.leozeballos.FastFood.product.Product product2 = net.leozeballos.FastFood.product.Product.builder().price(20.0).build();
        product2.setName("Product 2");

        Menu menu = Menu.builder()
                .discount(java.math.BigDecimal.valueOf(0.1))
                .products(java.util.List.of(product1, product2))
                .build();
        menu.setId(1L);
        menu.setName("Menu 1");
        menu.enable();

        // when
        MenuDTO dto = underTest.convertToDTO(menu);

        // then
        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getName()).isEqualTo("Menu 1");
        assertThat(dto.getPrice()).isEqualTo(27.0); // (10 + 20) * (1 - 0.1)
        assertThat(dto.getDiscountPercentage()).isEqualTo(10.0);
        assertThat(dto.getProductsList()).isEqualTo("Product 1, Product 2");
        assertThat(dto.isActive()).isTrue();
    }

}
