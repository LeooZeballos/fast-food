package net.leozeballos.FastFood.inventory;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.leozeballos.FastFood.auth.CustomUserDetails;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    @Operation(summary = "Update stock", description = "Updates the quantity or availability of an item in a branch")
    @ApiResponse(responseCode = "200", description = "Stock updated successfully")
    public Inventory updateStock(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody Inventory inventory) {
        
        Long branchId = (inventory.getBranch() != null) ? inventory.getBranch().getId() : null;
        if (branchId == null) {
            throw new IllegalArgumentException("Branch ID is required for inventory update");
        }

        Long effectiveBranchId = getEffectiveBranchId(userDetails);
        
        // If it's not an admin (effectiveBranchId != null), they must match the branch
        if (effectiveBranchId != null) {
            if (!effectiveBranchId.equals(branchId)) {
                throw new AccessDeniedException("User does not have access to this branch's inventory");
            }
        } else if (!isAdmin(userDetails)) {
            // If it's not an admin and has no branch assigned, they can't update anything
            throw new AccessDeniedException("User has no branch assigned and is not an admin");
        }
        
        return inventoryService.save(inventory);
    }

    @PostMapping("/branch/{branchId}/restock")
    @Operation(summary = "Restock item", description = "Increments the stock quantity for an item at a specific branch")
    @ApiResponse(responseCode = "200", description = "Stock restocked successfully")
    @ApiResponse(responseCode = "403", description = "Access denied")
    public void restock(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Parameter(description = "ID of the branch") @PathVariable Long branchId,
            @Valid @RequestBody RestockDTO restockData) {
        
        Long effectiveBranchId = getEffectiveBranchId(userDetails);
        if (effectiveBranchId != null && !effectiveBranchId.equals(branchId)) {
            throw new AccessDeniedException("User does not have access to restock this branch");
        }

        inventoryService.incrementStock(branchId, restockData.itemId(), restockData.quantity());
    }

    private Long getEffectiveBranchId(CustomUserDetails userDetails) {
        if (isAdmin(userDetails)) {
            return null; // Admins see everything
        }
        return userDetails != null ? userDetails.getBranchId() : null;
    }

    private boolean isAdmin(CustomUserDetails userDetails) {
        if (userDetails != null) {
            return userDetails.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        }
        // Fallback for WithMockUser or other authentication types
        var auth = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        return auth != null && auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }
}
