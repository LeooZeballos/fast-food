package net.leozeballos.FastFood.product;

import net.leozeballos.FastFood.error.ResourceNotFoundException;
import net.leozeballos.FastFood.mapper.ProductMapper;
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
    private final ProductMapper productMapper;

    public ProductService(ProductRepository productRepository, MenuRepository menuRepository, ProductMapper productMapper) {
        this.productRepository = productRepository;
        this.menuRepository = menuRepository;
        this.productMapper = productMapper;
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
                .map(productMapper::toDTO)
                .collect(Collectors.toList());
    }

    public List<Product> findAll() {
        return productRepository.findAll();
    }

    public Product findById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
    }

    public Product save(Product product) {
        return productRepository.save(product);
    }

    public void delete(Product product) {
        productRepository.delete(product);
    }

    public void disableItem(Long id) {
        Product product = findById(id);

        // remove product from all menus that contain it
        ArrayList<Menu> menus = new ArrayList<>(menuRepository.findAll());
        for (Menu menu : menus) {
            menu.getItems().remove(product);
        }

        // disable product
        product.disable();
        productRepository.save(product);
    }

    public void enableItem(Long id) {
        Product product = findById(id);
        product.enable();
        productRepository.save(product);
    }

    public void deleteById(Long id) {
        if (!productRepository.existsById(id)) {
            throw new ResourceNotFoundException("Product not found with id: " + id);
        }
        productRepository.deleteById(id);
    }

    public void deleteAll() {
        productRepository.deleteAll();
    }

}
