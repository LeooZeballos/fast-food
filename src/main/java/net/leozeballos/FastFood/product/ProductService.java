package net.leozeballos.FastFood.product;

import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import net.leozeballos.FastFood.error.ResourceNotFoundException;
import net.leozeballos.FastFood.foodorder.FoodOrderRepository;
import net.leozeballos.FastFood.mapper.ProductMapper;
import net.leozeballos.FastFood.menu.Menu;
import net.leozeballos.FastFood.menu.MenuRepository;
import net.leozeballos.FastFood.util.AuditService;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;
    private final MenuRepository menuRepository;
    private final FoodOrderRepository foodOrderRepository;
    private final ProductMapper productMapper;
    private final AuditService auditService;

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
        Specification<Product> spec = Specification.allOf(
                ProductSpecifications.hasName(name),
                ProductSpecifications.isPriceLessThan(maxPrice),
                ProductSpecifications.isActive(active)
        );

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

    @Timed(value = "product.save", description = "Time to save a product")
    @Transactional
    public Product save(Product product) {
        return productRepository.save(product);
    }

    @Timed(value = "product.delete", description = "Time to delete a product")
    @Transactional
    public void delete(Product product) {
        auditService.logAction("DELETE_PRODUCT", "ID=" + product.getId() + ", Name=" + product.getName());
        productRepository.delete(product);
    }

    @Transactional
    public void disableItem(Long id) {
        Product product = findById(id);

        // check if there are any active orders containing this product
        long activeOrders = foodOrderRepository.countActiveOrdersByItemId(id);
        if (activeOrders > 0) {
            throw new IllegalStateException("Cannot disable product with " + activeOrders + " active orders. Please process or cancel them first.");
        }

        // remove product from all menus that contain it
        ArrayList<Menu> menus = new ArrayList<>(menuRepository.findAll());
        for (Menu menu : menus) {
            menu.getItems().remove(product);
        }

        // disable product
        product.disable();
        auditService.logAction("DISABLE_PRODUCT", "ID=" + id + ", Name=" + product.getName());
        productRepository.save(product);
    }

    @Transactional
    public void enableItem(Long id) {
        Product product = findById(id);
        product.enable();
        auditService.logAction("ENABLE_PRODUCT", "ID=" + id + ", Name=" + product.getName());
        productRepository.save(product);
    }

    @Timed(value = "product.deleteById", description = "Time to delete a product by id")
    @Transactional
    public void deleteById(Long id) {
        Product product = findById(id);
        auditService.logAction("DELETE_PRODUCT", "ID=" + id + ", Name=" + product.getName());
        productRepository.deleteById(id);
    }

    @Transactional
    public void deleteAll() {
        productRepository.deleteAll();
    }

}
