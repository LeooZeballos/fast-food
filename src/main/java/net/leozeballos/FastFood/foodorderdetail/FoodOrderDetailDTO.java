package net.leozeballos.FastFood.foodorderdetail;

import lombok.Builder;
import net.leozeballos.FastFood.util.FormattingUtils;

@Builder
public record FoodOrderDetailDTO(
    Long id,
    String itemName,
    double historicPrice,
    Integer quantity,
    double subtotal
) {
    public String getFormattedPrice() {
        return FormattingUtils.formatPrice(historicPrice);
    }

    public String getFormattedSubtotal() {
        return FormattingUtils.formatPrice(subtotal);
    }
}
