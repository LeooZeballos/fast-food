package net.leozeballos.FastFood.product;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import net.leozeballos.FastFood.mapper.ProductMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
@Tag(name = "Products", description = "Management of individual food items")
public class ProductRestController {

    private final ProductService productService;
    private final ProductMapper productMapper;

    @GetMapping
    @Operation(summary = "Get all products", description = "Returns a list of all products, with optional filtering by name, price, and active status")
    public List<ProductDTO> getAll(
            @Parameter(description = "Filter by product name") @RequestParam(required = false) String name,
            @Parameter(description = "Filter by maximum price") @RequestParam(required = false) Double maxPrice,
            @Parameter(description = "Filter by active status") @RequestParam(required = false) Boolean active) {
        return productService.findAllDTO(name, maxPrice, active);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get product by ID", description = "Returns detailed information about a specific product")
    @ApiResponse(responseCode = "200", description = "Product found")
    @ApiResponse(responseCode = "404", description = "Product not found")
    public ProductDTO getOne(@Parameter(description = "ID of the product to be retrieved") @PathVariable Long id) {
        Product product = productService.findById(id);
        return productMapper.toDTO(product);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new product", description = "Registers a new individual food item in the system")
    @ApiResponse(responseCode = "201", description = "Product created successfully")
    @ApiResponse(responseCode = "400", description = "Invalid input data")
    public ProductDTO create(@Valid @RequestBody ProductDTO productDTO) {
        Product product = productMapper.toEntity(productDTO);
        return productMapper.toDTO(productService.save(product));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update a product", description = "Updates an existing product's details")
    @ApiResponse(responseCode = "200", description = "Product updated successfully")
    @ApiResponse(responseCode = "404", description = "Product not found")
    public ProductDTO update(
            @Parameter(description = "ID of the product to be updated") @PathVariable Long id,
            @Valid @RequestBody ProductDTO productData) {
        Product product = productService.findById(id);
        product.setName(productData.name());
        product.setNameEs(productData.nameEs());
        product.setPrice(productData.price());
        product.setIcon(productData.icon());
        product.setImageUrl(productData.imageUrl());
        product.setActive(productData.active());
        return productMapper.toDTO(productService.save(product));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete a product", description = "Permanently removes a product from the system")
    @ApiResponse(responseCode = "204", description = "Product deleted successfully")
    @ApiResponse(responseCode = "404", description = "Product not found")
    public void delete(@Parameter(description = "ID of the product to be deleted") @PathVariable Long id) {
        productService.deleteById(id);
    }

    @PatchMapping("/{id}/disable")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Disable a product", description = "Sets the product status to inactive")
    public ProductDTO disable(@Parameter(description = "ID of the product to be disabled") @PathVariable Long id) {
        productService.disableItem(id);
        return productMapper.toDTO(productService.findById(id));
    }

    @PatchMapping("/{id}/enable")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Enable a product", description = "Sets the product status to active")
    public ProductDTO enable(@Parameter(description = "ID of the product to be enabled") @PathVariable Long id) {
        productService.enableItem(id);
        return productMapper.toDTO(productService.findById(id));
    }
}
