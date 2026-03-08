package net.leozeballos.FastFood.product;

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
    private String name;
    private double price;
    private boolean active;

    public String getFormattedPrice() {
        return FormattingUtils.formatPrice(price);
    }
}
