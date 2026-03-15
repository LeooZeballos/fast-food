package net.leozeballos.FastFood.foodorder;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.leozeballos.FastFood.auth.CustomUserDetails;
import net.leozeballos.FastFood.foodorderstatemachine.FoodOrderState;
import net.leozeballos.FastFood.mapper.FoodOrderMapper;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
@Tag(name = "Orders", description = "Management of food orders and their lifecycle")
public class FoodOrderRestController {

    private final FoodOrderService foodOrderService;
    private final FoodOrderMapper foodOrderMapper;

    @GetMapping
    @Operation(summary = "Get all orders", description = "Returns a list of all orders, optionally filtered by status and scoped to the user's branch")
    public List<FoodOrderDTO> getAll(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Parameter(description = "Filter by order status (created, in_preparation, finished, paid, cancelled, rejected)")
            @RequestParam(required = false) String type) {
        
        Long branchId = getEffectiveBranchId(userDetails);

        if (type == null || type.equals("all")) {
            return foodOrderService.findAllDTO(branchId);
        }
        
        FoodOrderState state = switch (type) {
            case "created" -> FoodOrderState.CREATED;
            case "in_preparation" -> FoodOrderState.INPREPARATION;
            case "finished" -> FoodOrderState.DONE;
            case "paid" -> FoodOrderState.PAID;
            case "cancelled" -> FoodOrderState.CANCELLED;
            case "rejected" -> FoodOrderState.REJECTED;
            default -> null;
        };

        return state != null ? foodOrderService.findAllFoodOrdersByStateDTO(state, branchId) : List.of();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get order by ID", description = "Returns detailed information about a specific order, strictly scoped to user branch")
    @ApiResponse(responseCode = "200", description = "Order found")
    @ApiResponse(responseCode = "404", description = "Order not found")
    @ApiResponse(responseCode = "403", description = "Access denied to branch")
    public FoodOrderDTO getOne(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Parameter(description = "ID of the order to be retrieved") @PathVariable Long id) {
        return foodOrderService.findDTOById(id, getEffectiveBranchId(userDetails));
    }

    @PostMapping
    @Operation(summary = "Create a new order", description = "Places a new food order in the system")
    @ApiResponse(responseCode = "201", description = "Order created successfully")
    @ApiResponse(responseCode = "400", description = "Invalid input data")
    @ApiResponse(responseCode = "403", description = "Access denied to branch")
    public FoodOrderDTO create(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody CreateOrderDTO createOrderDTO) {
        
        // Security check: if user is not ADMIN, they can only create orders for their branch
        if (!isAdmin(userDetails)) {
            Long userBranchId = (userDetails != null) ? userDetails.getBranchId() : null;
            if (userBranchId == null || !userBranchId.equals(createOrderDTO.branchId())) {
                throw new AccessDeniedException("User can only place orders for their assigned branch");
            }
        }

        FoodOrder order = foodOrderService.createOrder(createOrderDTO);
        return foodOrderMapper.toDTO(order);
    }

    @PostMapping("/{id}/start-preparation")
    @Operation(summary = "Start order preparation", description = "Transitions the order state to IN_PREPARATION")
    public FoodOrderDTO startPreparation(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Parameter(description = "ID of the order to start") @PathVariable Long id) {
        Long branchId = getEffectiveBranchId(userDetails);
        foodOrderService.startPreparation(id, branchId);
        return foodOrderService.findDTOById(id, branchId);
    }

    @PostMapping("/{id}/finish-preparation")
    @Operation(summary = "Finish order preparation", description = "Transitions the order state to DONE (Finished)")
    public FoodOrderDTO finishPreparation(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Parameter(description = "ID of the order to finish") @PathVariable Long id) {
        Long branchId = getEffectiveBranchId(userDetails);
        foodOrderService.finishPreparation(id, branchId);
        return foodOrderService.findDTOById(id, branchId);
    }

    @PostMapping("/{id}/confirm-payment")
    @Operation(summary = "Confirm order payment", description = "Transitions the order state to PAID")
    public FoodOrderDTO confirmPayment(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Parameter(description = "ID of the order to pay") @PathVariable Long id) {
        Long branchId = getEffectiveBranchId(userDetails);
        foodOrderService.confirmPayment(id, branchId);
        return foodOrderService.findDTOById(id, branchId);
    }

    @PostMapping("/{id}/cancel")
    @Operation(summary = "Cancel order", description = "Transitions the order state to CANCELLED")
    public FoodOrderDTO cancel(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Parameter(description = "ID of the order to cancel") @PathVariable Long id) {
        Long branchId = getEffectiveBranchId(userDetails);
        foodOrderService.cancel(id, branchId);
        return foodOrderService.findDTOById(id, branchId);
    }

    @PostMapping("/{id}/reject")
    @Operation(summary = "Reject order", description = "Transitions the order state to REJECTED")
    public FoodOrderDTO reject(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Parameter(description = "ID of the order to reject") @PathVariable Long id) {
        Long branchId = getEffectiveBranchId(userDetails);
        foodOrderService.reject(id, branchId);
        return foodOrderService.findDTOById(id, branchId);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete order", description = "Permanently removes an order from the system")
    @ApiResponse(responseCode = "204", description = "Order deleted successfully")
    public void delete(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Parameter(description = "ID of the order to delete") @PathVariable Long id) {
        foodOrderService.deleteById(id, getEffectiveBranchId(userDetails));
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
