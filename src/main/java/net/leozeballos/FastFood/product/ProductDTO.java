package net.leozeballos.FastFood.product;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import net.leozeballos.FastFood.util.FormattingUtils;

@Builder
public record ProductDTO(
    Long id,
    @NotBlank(message = "Product name is required")
    @Size(min = 1, max = 50, message = "Product name must be between 1 and 50 characters")
    String name,
    @DecimalMin(value = "0.0", inclusive = true, message = "Price must be greater than or equal to 0")
    double price,
    String icon,
    boolean active
) {
    public String getFormattedPrice() {
        return FormattingUtils.formatPrice(price);
    }
}
