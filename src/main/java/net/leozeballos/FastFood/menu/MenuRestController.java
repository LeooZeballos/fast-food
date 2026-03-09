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
    public MenuDTO create(@RequestBody Menu menu) {
        return menuMapper.toDTO(menuService.save(menu));
    }

    @PutMapping("/{id}")
    public MenuDTO update(@PathVariable Long id, @RequestBody Menu menuData) {
        Menu menu = menuService.findById(id);
        menu.setName(menuData.getName());
        menu.setDiscount(menuData.getDiscount());
        menu.setItems(menuData.getItems());
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
