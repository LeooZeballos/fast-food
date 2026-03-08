package net.leozeballos.FastFood.product;

import org.springframework.data.jpa.domain.Specification;

/**
 * Specifications for dynamic filtering of Product entities.
 */
public class ProductSpecifications {

    /**
     * Filter products by name (case-insensitive partial match).
     */
    public static Specification<Product> hasName(String name) {
        return (root, query, criteriaBuilder) -> 
            name == null ? null : criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + name.toLowerCase() + "%");
    }

    /**
     * Filter products with price less than or equal to the specified value.
     */
    public static Specification<Product> isPriceLessThan(Double price) {
        return (root, query, criteriaBuilder) -> 
            price == null ? null : criteriaBuilder.lessThanOrEqualTo(root.get("price"), price);
    }

    /**
     * Filter products by their active status.
     */
    public static Specification<Product> isActive(Boolean active) {
        return (root, query, criteriaBuilder) -> 
            active == null ? null : criteriaBuilder.equal(root.get("isActive"), active);
    }
}
