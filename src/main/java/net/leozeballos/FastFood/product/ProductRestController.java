package net.leozeballos.FastFood.product;

import lombok.RequiredArgsConstructor;
import net.leozeballos.FastFood.mapper.ProductMapper;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductRestController {

    private final ProductService productService;
    private final ProductMapper productMapper;

    @GetMapping
    public List<ProductDTO> getAll(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) Boolean active) {
        return productService.findAllDTO(name, maxPrice, active);
    }

    @GetMapping("/{id}")
    public ProductDTO getOne(@PathVariable Long id) {
        Product product = productService.findById(id);
        return productMapper.toDTO(product);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductDTO create(@Valid @RequestBody Product product) {
        return productMapper.toDTO(productService.save(product));
    }

    @PutMapping("/{id}")
    public ProductDTO update(@PathVariable Long id, @Valid @RequestBody Product productData) {
        Product product = productService.findById(id);
        product.setName(productData.getName());
        product.setPrice(productData.getPrice());
        return productMapper.toDTO(productService.save(product));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        productService.deleteById(id);
    }

    @PatchMapping("/{id}/disable")
    public ProductDTO disable(@PathVariable Long id) {
        productService.disableItem(id);
        return productMapper.toDTO(productService.findById(id));
    }

    @PatchMapping("/{id}/enable")
    public ProductDTO enable(@PathVariable Long id) {
        productService.enableItem(id);
        return productMapper.toDTO(productService.findById(id));
    }
}
