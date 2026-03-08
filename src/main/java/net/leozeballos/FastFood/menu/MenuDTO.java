package net.leozeballos.FastFood.menu;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.leozeballos.FastFood.util.FormattingUtils;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MenuDTO {
    private Long id;
    private String name;
    private double price;
    private double discountPercentage;
    private String productsList;
    private boolean active;

    public String getFormattedPrice() {
        return FormattingUtils.formatPrice(price);
    }

    public String getFormattedDiscount() {
        return String.format("%.0f", discountPercentage) + "%";
    }
}
