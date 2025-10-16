package org.springshop.product_service.controller.product;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springshop.product_service.dto.product.ProductResponse;
import org.springshop.product_service.dto.product.ProductRequest;
import org.springshop.product_service.service.product.ProductService; 

import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(@RequestBody ProductRequest dto) {

        ProductResponse response = productService.createProduct(dto);
        URI location = URI.create("/api/products/" + response.getId()); 

        return ResponseEntity.created(location).body(response);
    }

    @GetMapping
    public ResponseEntity<List<ProductResponse>> getAllProducts() {

        return ResponseEntity.ok(productService.getAllProducts());
    }

    @GetMapping("/{id:\\d+}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable Integer id) {

        return wrapOrNotFound(productService.getProductById(id));
    }

    @PutMapping("/{id:\\d+}")
    public ResponseEntity<ProductResponse> updateProduct(
            @PathVariable Integer id,
            @RequestBody ProductRequest dto) {
        
        ProductResponse updatedDto = productService.updateProduct(id, dto);

        return ResponseEntity.ok(updatedDto);
    }

    @DeleteMapping("/{id:\\d+}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Integer id) {

        productService.deleteProduct(id);

        return ResponseEntity.noContent().build();
    }
    
    private <T> ResponseEntity<T> wrapOrNotFound(Optional<T> maybeResponse) {
        return maybeResponse.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}