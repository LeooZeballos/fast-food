package net.leozeballos.FastFood.foodorderdetail;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.leozeballos.FastFood.util.FormattingUtils;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FoodOrderDetailDTO {
    private Long id;
    private String itemName;
    private double historicPrice;
    private Integer quantity;
    private double subtotal;

    public String getFormattedPrice() {
        return FormattingUtils.formatPrice(historicPrice);
    }

    public String getFormattedSubtotal() {
        return FormattingUtils.formatPrice(subtotal);
    }
}
