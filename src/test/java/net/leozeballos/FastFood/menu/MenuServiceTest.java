package net.leozeballos.FastFood.menu;

import net.leozeballos.FastFood.mapper.MenuMapper;
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
class MenuServiceTest {

    @Mock private MenuRepository menuRepository;
    @Spy private MenuMapper menuMapper;
    private MenuService underTest;

    @BeforeEach
    void setUp() {
        underTest = new MenuService(menuRepository, menuMapper);
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
        Menu menu = new Menu();
        when(menuRepository.findById(id)).thenReturn(Optional.of(menu));

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
        when(menuRepository.existsById(id)).thenReturn(true);

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
        net.leozeballos.FastFood.product.Product product1 = new net.leozeballos.FastFood.product.Product();
        product1.setName("Product 1");
        product1.setPrice(10.0);
        
        net.leozeballos.FastFood.product.Product product2 = new net.leozeballos.FastFood.product.Product();
        product2.setName("Product 2");
        product2.setPrice(20.0);

        Menu menu = new Menu();
        menu.setDiscount(java.math.BigDecimal.valueOf(0.1));
        menu.setProducts(java.util.List.of(product1, product2));
        menu.setId(1L);
        menu.setName("Menu 1");
        menu.enable();

        // when
        MenuDTO dto = menuMapper.toDTO(menu);

        // then
        assertThat(dto.id()).isEqualTo(1L);
        assertThat(dto.name()).isEqualTo("Menu 1");
        assertThat(dto.price()).isEqualTo(27.0); // (10 + 20) * (1 - 0.1)
        assertThat(dto.discountPercentage()).isEqualTo(10.0);
        assertThat(dto.productsList()).isEqualTo("Product 1, Product 2");
        assertThat(dto.active()).isTrue();
    }

}
