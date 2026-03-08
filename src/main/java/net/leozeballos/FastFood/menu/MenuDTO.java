package net.leozeballos.FastFood.menu;

import lombok.Builder;
import net.leozeballos.FastFood.util.FormattingUtils;

@Builder
public record MenuDTO(
    Long id,
    String name,
    double price,
    double discountPercentage,
    String productsList,
    boolean active
) {
    public String getFormattedPrice() {
        return FormattingUtils.formatPrice(price);
    }

    public String getFormattedDiscount() {
        return String.format("%.0f", discountPercentage) + "%";
    }
}
