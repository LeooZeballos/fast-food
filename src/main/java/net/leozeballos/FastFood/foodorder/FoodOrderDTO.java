package net.leozeballos.FastFood.foodorder;

import lombok.Builder;
import net.leozeballos.FastFood.foodorderdetail.FoodOrderDetailDTO;
import net.leozeballos.FastFood.util.FormattingUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Builder
public record FoodOrderDTO(
    Long id,
    LocalDateTime creationTimestamp,
    LocalDateTime preparationStartTimestamp,
    LocalDateTime paymentTimestamp,
    String formattedState,
    String branchName,
    List<FoodOrderDetailDTO> foodOrderDetails,
    double total
) {
    public String getFormattedCreationTimestamp() {
        return FormattingUtils.formatDateTime(creationTimestamp);
    }

    public String getFormattedPreparationStartTimestamp() {
        return FormattingUtils.formatDateTime(preparationStartTimestamp);
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
                .map(detail -> detail.quantity() + " x " + detail.itemName())
                .collect(Collectors.joining(", "));
    }
}
