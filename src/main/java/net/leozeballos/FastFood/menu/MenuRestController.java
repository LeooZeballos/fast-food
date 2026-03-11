package net.leozeballos.FastFood.menu;

import lombok.RequiredArgsConstructor;
import net.leozeballos.FastFood.mapper.MenuMapper;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/menus")
@RequiredArgsConstructor
public class MenuRestController {

    private final MenuService menuService;
    private final MenuMapper menuMapper;

    @GetMapping
    public List<MenuDTO> getAll() {
        return menuService.findAllDTO();
    }

    @GetMapping("/{id}")
    public MenuDTO getOne(@PathVariable Long id) {
        Menu menu = menuService.findById(id);
        return menuMapper.toDTO(menu);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MenuDTO create(@RequestBody java.util.Map<String, Object> menuData) {
        Menu menu = new Menu();
        menu.setName((String) menuData.get("name"));
        if (menuData.containsKey("discountPercentage")) {
            double dp = ((Number) menuData.get("discountPercentage")).doubleValue();
            menu.setDiscount(java.math.BigDecimal.valueOf(dp / 100.0));
        } else {
            menu.setDiscount(java.math.BigDecimal.ZERO);
        }
        if (menuData.containsKey("active")) {
            menu.setActive((Boolean) menuData.get("active"));
        } else {
            menu.setActive(true);
        }
        return menuMapper.toDTO(menuService.save(menu));
    }

    @PutMapping("/{id}")
    public MenuDTO update(@PathVariable Long id, @RequestBody java.util.Map<String, Object> menuData) {
        Menu menu = menuService.findById(id);
        if (menuData.containsKey("name")) {
            menu.setName((String) menuData.get("name"));
        }
        if (menuData.containsKey("discountPercentage")) {
            double dp = ((Number) menuData.get("discountPercentage")).doubleValue();
            menu.setDiscount(java.math.BigDecimal.valueOf(dp / 100.0));
        }
        return menuMapper.toDTO(menuService.save(menu));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        menuService.deleteById(id);
    }

    @PatchMapping("/{id}/disable")
    public MenuDTO disable(@PathVariable Long id) {
        menuService.disableItem(id);
        return menuMapper.toDTO(menuService.findById(id));
    }

    @PatchMapping("/{id}/enable")
    public MenuDTO enable(@PathVariable Long id) {
        menuService.enableItem(id);
        return menuMapper.toDTO(menuService.findById(id));
    }
}
