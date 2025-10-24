package org.springshop.product_service.controller.product;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springshop.product_service.dto.product.ProductResponse;
import org.springshop.product_service.dto.product.ProductUpdateStockRequest;
import org.springshop.product_service.dto.product.ProductRequest;
import org.springshop.product_service.service.product.ProductService;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v2/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(@RequestBody ProductRequest dto) {

        ProductResponse response = productService.createProduct(dto);
        URI location = URI.create("/api/v2/products/" + response.getId());

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

    @PatchMapping("/{productId:\\d+}/stock")
    public ResponseEntity<ProductResponse> updateStock(
            @PathVariable Integer productId,
            @RequestBody ProductUpdateStockRequest request) {
        // Llama al servicio con el ID y la cantidad de cambio
        ProductResponse response = productService.updateStock(
                productId,
                request.getQuantityChange());

        // Devuelve 200 OK con el recurso actualizado
        return ResponseEntity.ok(response);
    }

    private <T> ResponseEntity<T> wrapOrNotFound(Optional<T> maybeResponse) {
        return maybeResponse.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}