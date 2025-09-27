// Archivo: org.springshop.api.controller.product.ProductController.java (Refinado)
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
    // Eliminamos BASE_URL ya que la URI puede construirse directamente.

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    // -------------------- PRODUCTO GENÉRICO (CRUD completo) --------------------
    
    // ✅ 1. Crear Producto (POST /api/products)
    @PostMapping
    public ResponseEntity<ProductResponseDTO> createProduct(@RequestBody ProductRequestDTO dto) {
        ProductResponseDTO response = productService.createProduct(dto);
        
        // Uso de URI.create para crear el path absoluto de forma más dinámica.
        URI location = URI.create("/api/products/" + response.getId()); 
        return ResponseEntity.created(location).body(response);
    }

    // ✅ 2. Obtener Todos (GET /api/products)
    @GetMapping
    public ResponseEntity<List<ProductResponseDTO>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    // ✅ 3. Obtener por ID (GET /api/products/{id})
    @GetMapping("/{id:\\d+}")
    public ResponseEntity<ProductResponseDTO> getProductById(@PathVariable Integer id) {
        // Mejor práctica: usar el método auxiliar para Optional.
        return wrapOrNotFound(productService.getProductById(id));
    }

    // ✅ 4. Actualizar (PUT /api/products/{id})
    @PutMapping("/{id:\\d+}")
    public ResponseEntity<ProductResponseDTO> updateProduct(
            @PathVariable Integer id,
            @RequestBody ProductRequestDTO dto) {
        
        // Dado que el servicio lanza EntityNotFoundException si el ID no existe, 
        // no necesitamos envolverlo en Optional aquí.
        // Si el servicio lanza una excepción (404), Spring lo manejará con @ControllerAdvice.
        ProductResponseDTO updatedDto = productService.updateProduct(id, dto);
        return ResponseEntity.ok(updatedDto);
    }

    // ✅ 5. Eliminar (DELETE /api/products/{id})
    @DeleteMapping("/{id:\\d+}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Integer id) {
        // El servicio lanza 404 si no existe (ya que usa findProductOrThrow), 
        // y si tiene éxito, devolvemos 204.
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    // -------------------- HELPER --------------------
    
    /**
     * Convierte un Optional<T> en una respuesta HTTP, devolviendo 200 OK si está presente 
     * o 404 Not Found si está vacío.
     */
    private <T> ResponseEntity<T> wrapOrNotFound(Optional<T> maybeResponse) {
        return maybeResponse.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}