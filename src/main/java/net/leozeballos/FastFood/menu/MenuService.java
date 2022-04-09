package net.leozeballos.FastFood.menu;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MenuService {

    private final MenuRepository menuRepository;

    @Autowired
    public MenuService(MenuRepository menuRepository) {
        this.menuRepository = menuRepository;
    }

    public List<Menu> findAll() {
        return menuRepository.findAll();
    }

    public Menu findById(Long id) {
        return menuRepository.findById(id).orElse(null);
    }

    public Menu save(Menu menu) {
        return menuRepository.save(menu);
    }

    public void delete(Menu menu) {
        menuRepository.delete(menu);
    }

    public void deleteById(Long id) {
        menuRepository.deleteById(id);
    }

    public void deleteAll() {
        menuRepository.deleteAll();
    }

}