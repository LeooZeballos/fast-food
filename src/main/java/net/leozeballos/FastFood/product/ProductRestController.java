package net.leozeballos.FastFood.product;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductRestController {

    private final ProductService productService;

    @GetMapping
    public List<ProductDTO> getAll() {
        return productService.findAllDTO();
    }

    @GetMapping("/{id}")
    public ProductDTO getOne(@PathVariable Long id) {
        Product product = productService.findById(id);
        return product != null ? productService.convertToDTO(product) : null;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductDTO create(@Valid @RequestBody Product product) {
        return productService.convertToDTO(productService.save(product));
    }

    @PutMapping("/{id}")
    public ProductDTO update(@PathVariable Long id, @Valid @RequestBody Product productData) {
        Product product = productService.findById(id);
        if (product != null) {
            product.setName(productData.getName());
            product.setPrice(productData.getPrice());
            return productService.convertToDTO(productService.save(product));
        }
        return null;
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        productService.deleteById(id);
    }

    @PatchMapping("/{id}/disable")
    public ProductDTO disable(@PathVariable Long id) {
        productService.disableItem(id);
        return productService.convertToDTO(productService.findById(id));
    }

    @PatchMapping("/{id}/enable")
    public ProductDTO enable(@PathVariable Long id) {
        productService.enableItem(id);
        return productService.convertToDTO(productService.findById(id));
    }
}
