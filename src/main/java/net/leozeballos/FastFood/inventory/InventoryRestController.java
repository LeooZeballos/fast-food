package net.leozeballos.FastFood.inventory;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/inventory")
@RequiredArgsConstructor
@Tag(name = "Inventory", description = "Stock management across branches")
public class InventoryRestController {

    private final InventoryService inventoryService;

    @GetMapping("/branch/{branchId}")
    @Operation(summary = "Get inventory by branch", description = "Returns a list of all stock items for a specific branch")
    @ApiResponse(responseCode = "200", description = "Inventory list retrieved")
    public List<Inventory> getByBranch(@Parameter(description = "ID of the branch") @PathVariable Long branchId) {
        return inventoryService.findByBranch(branchId);
    }

    @PostMapping("/update")
    @Operation(summary = "Update stock", description = "Updates the quantity or availability of an item in a branch")
    @ApiResponse(responseCode = "200", description = "Stock updated successfully")
    public Inventory updateStock(@RequestBody Inventory inventory) {
        return inventoryService.save(inventory);
    }
}
