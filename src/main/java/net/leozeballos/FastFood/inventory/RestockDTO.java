package net.leozeballos.FastFood.inventory;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record RestockDTO(
    @NotNull(message = "Item ID is required")
    Long itemId,
    
    @Min(value = 1, message = "Restock quantity must be at least 1")
    int quantity
) {}
