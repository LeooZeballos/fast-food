package net.leozeballos.FastFood.menu;

import net.leozeballos.FastFood.product.Product;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MenuService {

    private final MenuRepository menuRepository;

    public MenuService(MenuRepository menuRepository) {
        this.menuRepository = menuRepository;
    }

    public List<MenuDTO> findAllDTO() {
        return menuRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<Menu> findAll() {
        return menuRepository.findAll();
    }

    public MenuDTO convertToDTO(Menu menu) {
        String productsList = menu.getProducts().stream()
                .map(Product::getName)
                .collect(Collectors.joining(", "));
        if (productsList.isEmpty()) {
            productsList = "None";
        }

        return MenuDTO.builder()
                .id(menu.getId())
                .name(menu.getName())
                .price(menu.calculatePrice())
                .discountPercentage(menu.getDiscount().doubleValue() * 100)
                .productsList(productsList)
                .active(menu.isActive())
                .build();
    }

    public Menu findById(Long id) {
        return menuRepository.findById(id).orElse(null);
    }

    public Menu save(Menu menu) {
        return menuRepository.save(menu);
    }

    public void disableItem(Long id) {
        Menu menu = menuRepository.findById(id).orElse(null);
        assert menu != null;
        menu.disable();
        menuRepository.save(menu);
    }

    public void enableItem(Long id) {
        Menu menu = menuRepository.findById(id).orElse(null);
        assert menu != null;
        menu.enable();
        menuRepository.save(menu);
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
