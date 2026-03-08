package net.leozeballos.FastFood.foodorder;

import lombok.RequiredArgsConstructor;
import net.leozeballos.FastFood.foodorderstatemachine.FoodOrderState;
import net.leozeballos.FastFood.mapper.FoodOrderMapper;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class FoodOrderRestController {

    private final FoodOrderService foodOrderService;
    private final FoodOrderMapper foodOrderMapper;

    @GetMapping
    public List<FoodOrderDTO> getAll(@RequestParam(required = false) String type) {
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
    public FoodOrderDTO getOne(@PathVariable Long id) {
        return foodOrderService.findDTOById(id);
    }

    @PostMapping
    public FoodOrderDTO create(@Valid @RequestBody CreateOrderDTO createOrderDTO) {
        FoodOrder order = foodOrderService.createOrder(createOrderDTO);
        return foodOrderMapper.toDTO(order);
    }

    @PostMapping("/{id}/start-preparation")
    public FoodOrderDTO startPreparation(@PathVariable Long id) {
        foodOrderService.startPreparation(id);
        return foodOrderService.findDTOById(id);
    }

    @PostMapping("/{id}/finish-preparation")
    public FoodOrderDTO finishPreparation(@PathVariable Long id) {
        foodOrderService.finishPreparation(id);
        return foodOrderService.findDTOById(id);
    }

    @PostMapping("/{id}/confirm-payment")
    public FoodOrderDTO confirmPayment(@PathVariable Long id) {
        foodOrderService.confirmPayment(id);
        return foodOrderService.findDTOById(id);
    }

    @PostMapping("/{id}/cancel")
    public FoodOrderDTO cancel(@PathVariable Long id) {
        foodOrderService.cancel(id);
        return foodOrderService.findDTOById(id);
    }

    @PostMapping("/{id}/reject")
    public FoodOrderDTO reject(@PathVariable Long id) {
        foodOrderService.reject(id);
        return foodOrderService.findDTOById(id);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        foodOrderService.deleteById(id);
    }
}
