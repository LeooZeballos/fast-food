package net.leozeballos.FastFood.foodorder;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.List;

@Builder
public record CreateOrderDTO(
    @NotNull(message = "Branch ID is required")
    Long branchId,
    @NotEmpty(message = "Order must contain at least one item")
    @Valid
    List<OrderDetailItemDTO> items
) {
    @Builder
    public record OrderDetailItemDTO(
        @NotNull(message = "Item ID is required")
        Long itemId,
        @NotNull(message = "Quantity is required")
        @Min(value = 1, message = "Quantity must be at least 1")
        Integer quantity
    ) {}
}
