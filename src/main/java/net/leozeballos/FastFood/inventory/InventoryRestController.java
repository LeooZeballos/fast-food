package net.leozeballos.FastFood.inventory;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/inventory")
@RequiredArgsConstructor
public class InventoryRestController {

    private final InventoryService inventoryService;

    @GetMapping("/branch/{branchId}")
    public List<Inventory> getByBranch(@PathVariable Long branchId) {
        return inventoryService.findByBranch(branchId);
    }

    @PostMapping("/update")
    public Inventory updateStock(@RequestBody Inventory inventory) {
        return inventoryService.save(inventory);
    }
}
