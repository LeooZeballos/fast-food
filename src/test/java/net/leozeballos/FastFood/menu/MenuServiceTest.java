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

}