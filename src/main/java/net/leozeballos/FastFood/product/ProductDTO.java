package net.leozeballos.FastFood.product;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.leozeballos.FastFood.util.FormattingUtils;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductDTO {
    private Long id;

    @NotBlank(message = "Product name is required")
    @Size(min = 1, max = 50, message = "Product name must be between 1 and 50 characters")
    private String name;

    @DecimalMin(value = "0.0", inclusive = true, message = "Price must be greater than or equal to 0")
    private double price;

    private boolean active;

    public String getFormattedPrice() {
        return FormattingUtils.formatPrice(price);
    }
}
