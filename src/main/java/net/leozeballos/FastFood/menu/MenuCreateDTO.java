package net.leozeballos.FastFood.menu;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record MenuCreateDTO(
    @NotBlank(message = "Menu name is required")
    @Size(min = 1, max = 50, message = "Menu name must be between 1 and 50 characters")
    String name,
    
    @Size(max = 50)
    String nameEs,
    
    @DecimalMin(value = "0.0", message = "Discount percentage cannot be negative")
    @DecimalMax(value = "100.0", message = "Discount percentage cannot exceed 100")
    double discountPercentage,
    
    String icon,
    String imageUrl,
    boolean active
) {}
