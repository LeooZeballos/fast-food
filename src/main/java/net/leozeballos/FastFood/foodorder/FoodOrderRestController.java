package net.leozeballos.FastFood.foodorder;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import net.leozeballos.FastFood.foodorderstatemachine.FoodOrderState;
import net.leozeballos.FastFood.mapper.FoodOrderMapper;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
@Tag(name = "Orders", description = "Management of food orders and their lifecycle")
public class FoodOrderRestController {

    private final FoodOrderService foodOrderService;
    private final FoodOrderMapper foodOrderMapper;

    @GetMapping
    @Operation(summary = "Get all orders", description = "Returns a list of all orders, optionally filtered by status")
    public List<FoodOrderDTO> getAll(
            @Parameter(description = "Filter by order status (created, in_preparation, finished, paid, cancelled, rejected)")
            @RequestParam(required = false) String type) {
        if (type == null || type.equals("all")) {
            return foodOrderService.findAllDTO();
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

        return state != null ? foodOrderService.findAllFoodOrdersByStateDTO(state) : List.of();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get order by ID", description = "Returns detailed information about a specific order")
    @ApiResponse(responseCode = "200", description = "Order found")
    @ApiResponse(responseCode = "404", description = "Order not found")
    public FoodOrderDTO getOne(@Parameter(description = "ID of the order to be retrieved") @PathVariable Long id) {
        return foodOrderService.findDTOById(id);
    }

    @PostMapping
    @Operation(summary = "Create a new order", description = "Places a new food order in the system")
    @ApiResponse(responseCode = "201", description = "Order created successfully")
    @ApiResponse(responseCode = "400", description = "Invalid input data")
    public FoodOrderDTO create(@Valid @RequestBody CreateOrderDTO createOrderDTO) {
        FoodOrder order = foodOrderService.createOrder(createOrderDTO);
        return foodOrderMapper.toDTO(order);
    }

    @PostMapping("/{id}/start-preparation")
    @Operation(summary = "Start order preparation", description = "Transitions the order state to IN_PREPARATION")
    public FoodOrderDTO startPreparation(@Parameter(description = "ID of the order to start") @PathVariable Long id) {
        foodOrderService.startPreparation(id);
        return foodOrderService.findDTOById(id);
    }

    @PostMapping("/{id}/finish-preparation")
    @Operation(summary = "Finish order preparation", description = "Transitions the order state to DONE (Finished)")
    public FoodOrderDTO finishPreparation(@Parameter(description = "ID of the order to finish") @PathVariable Long id) {
        foodOrderService.finishPreparation(id);
        return foodOrderService.findDTOById(id);
    }

    @PostMapping("/{id}/confirm-payment")
    @Operation(summary = "Confirm order payment", description = "Transitions the order state to PAID")
    public FoodOrderDTO confirmPayment(@Parameter(description = "ID of the order to pay") @PathVariable Long id) {
        foodOrderService.confirmPayment(id);
        return foodOrderService.findDTOById(id);
    }

    @PostMapping("/{id}/cancel")
    @Operation(summary = "Cancel order", description = "Transitions the order state to CANCELLED")
    public FoodOrderDTO cancel(@Parameter(description = "ID of the order to cancel") @PathVariable Long id) {
        foodOrderService.cancel(id);
        return foodOrderService.findDTOById(id);
    }

    @PostMapping("/{id}/reject")
    @Operation(summary = "Reject order", description = "Transitions the order state to REJECTED")
    public FoodOrderDTO reject(@Parameter(description = "ID of the order to reject") @PathVariable Long id) {
        foodOrderService.reject(id);
        return foodOrderService.findDTOById(id);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete order", description = "Permanently removes an order from the system")
    @ApiResponse(responseCode = "204", description = "Order deleted successfully")
    public void delete(@Parameter(description = "ID of the order to delete") @PathVariable Long id) {
        foodOrderService.deleteById(id);
    }
}
