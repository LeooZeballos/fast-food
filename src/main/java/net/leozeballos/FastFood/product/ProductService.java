package net.leozeballos.FastFood.product;

import net.leozeballos.FastFood.menu.Menu;
import net.leozeballos.FastFood.menu.MenuRepository;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final MenuRepository menuRepository;

    public ProductService(ProductRepository productRepository, MenuRepository menuRepository) {
        this.productRepository = productRepository;
        this.menuRepository = menuRepository;
    }

    /**
     * Find all products as DTOs, without filters.
     * @return List of ProductDTOs.
     */
    public List<ProductDTO> findAllDTO() {
        return findAllDTO(null, null, null);
    }

    /**
     * Find all products as DTOs with dynamic filtering.
     * @param name Optional partial name match (case-insensitive).
     * @param maxPrice Optional maximum price filter.
     * @param active Optional active status filter.
     * @return Filtered list of ProductDTOs.
     */
    public List<ProductDTO> findAllDTO(String name, Double maxPrice, Boolean active) {
        Specification<Product> spec = Specification.where(ProductSpecifications.hasName(name))
                .and(ProductSpecifications.isPriceLessThan(maxPrice))
                .and(ProductSpecifications.isActive(active));

        return productRepository.findAll(spec).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<Product> findAll() {
        return productRepository.findAll();
    }

    public ProductDTO convertToDTO(Product product) {
        return ProductDTO.builder()
                .id(product.getId())
                .name(product.getName())
                .price(product.getPrice())
                .active(product.isActive())
                .build();
    }

    public Product findById(Long id) {
        return productRepository.findById(id).orElse(null);
    }

    public Product save(Product product) {
        return productRepository.save(product);
    }

    public void delete(Product product) {
        productRepository.delete(product);
    }

    public void disableItem(Long id) {
        Product product = productRepository.findById(id).orElse(null);

        // remove product from all menus that contain it
        ArrayList<Menu> menus = new ArrayList<>(menuRepository.findAll());
        for (Menu menu : menus) {
            menu.getProducts().remove(product);
        }

        // disable product
        assert product != null;
        product.disable();
        productRepository.save(product);
    }

    public void enableItem(Long id) {
        Product product = productRepository.findById(id).orElse(null);
        assert product != null;
        product.enable();
        productRepository.save(product);
    }

    public void deleteById(Long id) {
        productRepository.deleteById(id);
    }

    public void deleteAll() {
        productRepository.deleteAll();
    }

}
