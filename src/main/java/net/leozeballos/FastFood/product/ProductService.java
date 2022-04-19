package net.leozeballos.FastFood.product;

import net.leozeballos.FastFood.menu.Menu;
import net.leozeballos.FastFood.menu.MenuRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final MenuRepository menuRepository;

    @Autowired
    public ProductService(ProductRepository productRepository, MenuRepository menuRepository) {
        this.productRepository = productRepository;
        this.menuRepository = menuRepository;
    }

    public List<Product> findAll() {
        return productRepository.findAll();
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
