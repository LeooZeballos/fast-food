package net.leozeballos.FastFood.foodorder;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.leozeballos.FastFood.foodorderdetail.FoodOrderDetailDTO;
import net.leozeballos.FastFood.util.FormattingUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FoodOrderDTO {
    private Long id;
    private LocalDateTime creationTimestamp;
    private LocalDateTime paymentTimestamp;
    private String formattedState;
    private String branchName;
    private List<FoodOrderDetailDTO> foodOrderDetails;
    private double total;

    public String getFormattedCreationTimestamp() {
        return FormattingUtils.formatDateTime(creationTimestamp);
    }

    public String getFormattedPaymentTimestamp() {
        return FormattingUtils.formatDateTime(paymentTimestamp);
    }

    public String getFormattedTotal() {
        return FormattingUtils.formatPrice(total);
    }

    public String getFormattedFoodOrderDetails() {
        if (foodOrderDetails == null) return "";
        return foodOrderDetails.stream()
                .map(detail -> detail.getQuantity() + " x " + detail.getItemName())
                .collect(Collectors.joining(", "));
    }
}
