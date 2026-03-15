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
                .imageUrl(product.getImageUrl())
                .active(product.isActive())
                .build();
    }

    public Product toEntity(ProductDTO dto) {
        if (dto == null) {
            return null;
        }
        Product product = new Product();
        product.setId(dto.id());
        product.setName(dto.name());
        product.setNameEs(dto.nameEs());
        product.setPrice(dto.price());
        product.setIcon(dto.icon());
        product.setImageUrl(dto.imageUrl());
        product.setActive(dto.active());
        return product;
    }
}
