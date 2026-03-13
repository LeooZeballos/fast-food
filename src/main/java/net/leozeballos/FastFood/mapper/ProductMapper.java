package net.leozeballos.FastFood.mapper;

import net.leozeballos.FastFood.product.Product;
import net.leozeballos.FastFood.product.ProductDTO;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {

    public ProductDTO toDTO(Product product) {
        if (product == null) {
            return null;
        }
        return ProductDTO.builder()
                .id(product.getId())
                .name(product.getName())
                .nameEs(product.getNameEs())
                .price(product.getPrice())
                .icon(product.getIcon())
                .active(product.isActive())
                .build();
    }
}
