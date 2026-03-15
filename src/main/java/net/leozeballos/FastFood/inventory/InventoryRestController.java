package net.leozeballos.FastFood.inventory;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import net.leozeballos.FastFood.auth.CustomUserDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.access.AccessDeniedException;
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
    public List<Inventory> getByBranch(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Parameter(description = "ID of the branch") @PathVariable Long branchId) {
        
        Long effectiveBranchId = getEffectiveBranchId(userDetails);
        if (effectiveBranchId != null && !effectiveBranchId.equals(branchId)) {
            throw new AccessDeniedException("User does not have access to this branch's inventory");
        }

        return inventoryService.findByBranch(branchId);
    }

    @PostMapping("/update")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update stock", description = "Updates the quantity or availability of an item in a branch")
    @ApiResponse(responseCode = "200", description = "Stock updated successfully")
    public Inventory updateStock(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody Inventory inventory) {
        
        Long branchId = (inventory.getBranch() != null) ? inventory.getBranch().getId() : null;
        Long effectiveBranchId = getEffectiveBranchId(userDetails);
        
        if (effectiveBranchId != null && !effectiveBranchId.equals(branchId)) {
            throw new AccessDeniedException("User does not have access to this branch's inventory");
        }
        
        return inventoryService.save(inventory);
    }

    private Long getEffectiveBranchId(CustomUserDetails userDetails) {
        if (isAdmin(userDetails)) {
            return null; // Admins see everything
        }
        return userDetails != null ? userDetails.getBranchId() : null;
    }

    private boolean isAdmin(CustomUserDetails userDetails) {
        return userDetails != null && userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }
}
