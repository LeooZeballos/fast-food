package net.leozeballos.FastFood.menu;

import net.leozeballos.FastFood.error.ResourceNotFoundException;
import net.leozeballos.FastFood.mapper.MenuMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class MenuService {

    private final MenuRepository menuRepository;
    private final MenuMapper menuMapper;

    public MenuService(MenuRepository menuRepository, MenuMapper menuMapper) {
        this.menuRepository = menuRepository;
        this.menuMapper = menuMapper;
    }

    public List<MenuDTO> findAllDTO() {
        return menuRepository.findAllWithItems().stream()
                .map(menuMapper::toDTO)
                .collect(Collectors.toList());
    }

    public List<Menu> findAll() {
        return menuRepository.findAll();
    }

    public Menu findById(Long id) {
        return menuRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Menu not found with id: " + id));
    }

    @Transactional
    public Menu save(Menu menu) {
        return menuRepository.save(menu);
    }

    @Transactional
    public void disableItem(Long id) {
        Menu menu = findById(id);
        menu.disable();
        menuRepository.save(menu);
    }

    @Transactional
    public void enableItem(Long id) {
        Menu menu = findById(id);
        menu.enable();
        menuRepository.save(menu);
    }

    @Transactional
    public void delete(Menu menu) {
        menuRepository.delete(menu);
    }

    @Transactional
    public void deleteById(Long id) {
        if (!menuRepository.existsById(id)) {
            throw new ResourceNotFoundException("Menu not found with id: " + id);
        }
        menuRepository.deleteById(id);
    }

    @Transactional
    public void deleteAll() {
        menuRepository.deleteAll();
    }

}
