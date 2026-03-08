package net.leozeballos.FastFood.mapper;

import net.leozeballos.FastFood.menu.Menu;
import net.leozeballos.FastFood.menu.MenuDTO;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class MenuMapper {

    public MenuDTO toDTO(Menu menu) {
        if (menu == null) {
            return null;
        }
        return MenuDTO.builder()
                .id(menu.getId())
                .name(menu.getName())
                .price(menu.calculatePrice())
                .discountPercentage(menu.getDiscount().doubleValue() * 100)
                .productsList(menu.getProducts().stream()
                        .map(net.leozeballos.FastFood.product.Product::getName)
                        .collect(Collectors.joining(", ")))
                .active(menu.isActive())
                .build();
    }
}
