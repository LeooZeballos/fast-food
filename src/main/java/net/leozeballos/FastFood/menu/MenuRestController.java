package net.leozeballos.FastFood.menu;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import net.leozeballos.FastFood.mapper.MenuMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/menus")
@RequiredArgsConstructor
@Tag(name = "Menus", description = "Management of predefined combinations of items")
public class MenuRestController {

    private final MenuService menuService;
    private final MenuMapper menuMapper;

    @GetMapping
    @Operation(summary = "Get all menus", description = "Returns a list of all available food menus")
    public List<MenuDTO> getAll() {
        return menuService.findAllDTO();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get menu by ID", description = "Returns detailed information about a specific menu")
    @ApiResponse(responseCode = "200", description = "Menu found")
    @ApiResponse(responseCode = "404", description = "Menu not found")
    public MenuDTO getOne(@Parameter(description = "ID of the menu to be retrieved") @PathVariable Long id) {
        Menu menu = menuService.findById(id);
        return menuMapper.toDTO(menu);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new menu", description = "Registers a new food menu in the system")
    @ApiResponse(responseCode = "201", description = "Menu created successfully")
    @ApiResponse(responseCode = "400", description = "Invalid input data")
    public MenuDTO create(@jakarta.validation.Valid @RequestBody MenuCreateDTO menuData) {
        Menu menu = menuMapper.toEntity(menuData);
        return menuMapper.toDTO(menuService.save(menu));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update a menu", description = "Updates an existing food menu's details")
    @ApiResponse(responseCode = "200", description = "Menu updated successfully")
    @ApiResponse(responseCode = "404", description = "Menu not found")
    public MenuDTO update(
            @Parameter(description = "ID of the menu to be updated") @PathVariable Long id,
            @jakarta.validation.Valid @RequestBody MenuUpdateDTO menuData) {
        Menu menu = menuService.findById(id);
        menuMapper.updateEntity(menuData, menu);
        return menuMapper.toDTO(menuService.save(menu));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete a menu", description = "Permanently removes a menu from the system")
    @ApiResponse(responseCode = "204", description = "Menu deleted successfully")
    public void delete(@Parameter(description = "ID of the menu to be deleted") @PathVariable Long id) {
        menuService.deleteById(id);
    }

    @PatchMapping("/{id}/disable")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Disable a menu", description = "Sets the menu status to inactive")
    public MenuDTO disable(@Parameter(description = "ID of the menu to be disabled") @PathVariable Long id) {
        menuService.disableItem(id);
        return menuMapper.toDTO(menuService.findById(id));
    }

    @PatchMapping("/{id}/enable")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Enable a menu", description = "Sets the menu status to active")
    public MenuDTO enable(@Parameter(description = "ID of the menu to be enabled") @PathVariable Long id) {
        menuService.enableItem(id);
        return menuMapper.toDTO(menuService.findById(id));
    }
}
