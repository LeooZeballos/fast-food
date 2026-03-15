package net.leozeballos.FastFood.mapper;

import net.leozeballos.FastFood.item.Item;
import net.leozeballos.FastFood.menu.Menu;
import net.leozeballos.FastFood.menu.MenuCreateDTO;
import net.leozeballos.FastFood.menu.MenuDTO;
import net.leozeballos.FastFood.menu.MenuUpdateDTO;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.stream.Collectors;

@Component
public class MenuMapper {

    public MenuDTO toDTO(Menu menu) {
        if (menu == null) {
            return null;
        }
        double discount = (menu.getDiscount() != null) ? menu.getDiscount().doubleValue() : 0.0;
        return MenuDTO.builder()
                .id(menu.getId())
                .name(menu.getName())
                .nameEs(menu.getNameEs())
                .price(menu.calculatePrice())
                .discountPercentage(discount * 100)
                .productsList(menu.getItems().stream()
                        .map(Item::getName)
                        .collect(Collectors.joining(", ")))
                .icon(menu.getIcon())
                .imageUrl(menu.getImageUrl())
                .active(menu.isActive())
                .build();
    }

    public Menu toEntity(MenuCreateDTO dto) {
        if (dto == null) {
            return null;
        }
        Menu menu = new Menu();
        menu.setName(dto.name());
        menu.setNameEs(dto.nameEs());
        menu.setDiscount(BigDecimal.valueOf(dto.discountPercentage() / 100.0));
        menu.setIcon(dto.icon());
        menu.setImageUrl(dto.imageUrl());
        menu.setActive(dto.active());
        return menu;
    }

    public void updateEntity(MenuUpdateDTO dto, Menu menu) {
        if (dto == null || menu == null) {
            return;
        }
        if (dto.name() != null) {
            menu.setName(dto.name());
        }
        if (dto.nameEs() != null) {
            menu.setNameEs(dto.nameEs());
        }
        if (dto.discountPercentage() != null) {
            menu.setDiscount(BigDecimal.valueOf(dto.discountPercentage() / 100.0));
        }
        if (dto.icon() != null) {
            menu.setIcon(dto.icon());
        }
        if (dto.imageUrl() != null) {
            menu.setImageUrl(dto.imageUrl());
        }
        if (dto.active() != null) {
            menu.setActive(dto.active());
        }
    }
}
