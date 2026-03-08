package net.leozeballos.FastFood.menu;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/menus")
@RequiredArgsConstructor
public class MenuRestController {

    private final MenuService menuService;

    @GetMapping
    public List<MenuDTO> getAll() {
        return menuService.findAllDTO();
    }

    @GetMapping("/{id}")
    public MenuDTO getOne(@PathVariable Long id) {
        Menu menu = menuService.findById(id);
        return menu != null ? menuService.convertToDTO(menu) : null;
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        menuService.deleteById(id);
    }

    @PatchMapping("/{id}/disable")
    public MenuDTO disable(@PathVariable Long id) {
        menuService.disableItem(id);
        return menuService.convertToDTO(menuService.findById(id));
    }

    @PatchMapping("/{id}/enable")
    public MenuDTO enable(@PathVariable Long id) {
        menuService.enableItem(id);
        return menuService.convertToDTO(menuService.findById(id));
    }
}
