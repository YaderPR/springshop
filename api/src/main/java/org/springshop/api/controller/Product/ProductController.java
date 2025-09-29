package org.springshop.api.controller.product;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springshop.api.dto.product.ProductResponseDTO;
import org.springshop.api.dto.product.ProductRequestDTO;
import org.springshop.api.service.product.ProductService; 

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
    public ResponseEntity<ProductResponseDTO> createProduct(@RequestBody ProductRequestDTO dto) {

        ProductResponseDTO response = productService.createProduct(dto);
        URI location = URI.create("/api/products/" + response.getId()); 

        return ResponseEntity.created(location).body(response);
    }

    @GetMapping
    public ResponseEntity<List<ProductResponseDTO>> getAllProducts() {

        return ResponseEntity.ok(productService.getAllProducts());
    }

    @GetMapping("/{id:\\d+}")
    public ResponseEntity<ProductResponseDTO> getProductById(@PathVariable Integer id) {

        return wrapOrNotFound(productService.getProductById(id));
    }

    @PutMapping("/{id:\\d+}")
    public ResponseEntity<ProductResponseDTO> updateProduct(
            @PathVariable Integer id,
            @RequestBody ProductRequestDTO dto) {
        
        ProductResponseDTO updatedDto = productService.updateProduct(id, dto);

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