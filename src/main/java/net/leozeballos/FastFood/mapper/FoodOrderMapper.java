package net.leozeballos.FastFood.mapper;

import net.leozeballos.FastFood.foodorder.FoodOrder;
import net.leozeballos.FastFood.foodorder.FoodOrderDTO;
import net.leozeballos.FastFood.foodorderdetail.FoodOrderDetail;
import net.leozeballos.FastFood.foodorderdetail.FoodOrderDetailDTO;
import net.leozeballos.FastFood.util.FormattingUtils;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class FoodOrderMapper {

    public FoodOrderDTO toDTO(FoodOrder order) {
        if (order == null) {
            return null;
        }
        return FoodOrderDTO.builder()
                .id(order.getId())
                .creationTimestamp(order.getCreationTimestamp())
                .paymentTimestamp(order.getPaymentTimestamp())
                .formattedState(FormattingUtils.formatState(order.getState()))
                .branchName(order.getBranch() != null ? order.getBranch().getName() : "Unknown Branch")
                .total(order.calculateTotal())
                .foodOrderDetails(order.getFoodOrderDetails().stream()
                        .map(this::toDetailDTO)
                        .collect(Collectors.toList()))
                .build();
    }

    public FoodOrderDetailDTO toDetailDTO(FoodOrderDetail detail) {
        if (detail == null) {
            return null;
        }
        return FoodOrderDetailDTO.builder()
                .id(detail.getId())
                .itemName(detail.getItem() != null ? detail.getItem().getName() : "Unknown Item")
                .historicPrice(detail.getHistoricPrice())
                .quantity(detail.getQuantity())
                .subtotal(detail.calculateSubtotal())
                .build();
    }
}
