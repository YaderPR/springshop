// Archivo: org.springshop.api.controller.product.SupplementController.java (Refactorizado)
package org.springshop.api.controller.product;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springshop.api.dto.product.supplement.SupplementRequestDTO;
import org.springshop.api.dto.product.supplement.SupplementResponseDTO;
import org.springshop.api.service.product.SupplementService;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import jakarta.validation.Valid; // Importamos la validación

@RestController
@RequestMapping("/api/products/supplements")
public class SupplementController {

    private final SupplementService supplementService;
    private static final String BASE_URL = "/api/products/supplements";

    public SupplementController(SupplementService supplementService) {
        this.supplementService = supplementService;
    }

    // -------------------- SUPPLEMENTS ESPECÍFICO --------------------
    
    // ✅ 1. Crear Producto (POST /api/products/supplements)
    @PostMapping
    public ResponseEntity<SupplementResponseDTO> createSupplement(
            @Valid @RequestBody SupplementRequestDTO dto) { // Añadimos @Valid
        
        // CORRECCIÓN: Usamos el nombre de método corregido en el servicio
        SupplementResponseDTO response = supplementService.createSupplement(dto);
        URI location = URI.create(BASE_URL + "/" + response.getId());
        return ResponseEntity.created(location).body(response);
    }

    // ✅ 2. Obtener Todos y Filtrados (GET /.../?categoryId=N)
    @GetMapping
    public ResponseEntity<List<SupplementResponseDTO>> getAllOrFilteredSupplements(
        @RequestParam(required = false) Integer categoryId // Añadimos RequestParam para filtrar
    ) {
        if (categoryId != null) {
            // Usamos el nuevo método de filtrado por categoría genérica
            return ResponseEntity.ok(supplementService.getSupplementsByCategoryId(categoryId));
        }
        // CORRECCIÓN: Usamos el nombre de método corregido en el servicio
        return ResponseEntity.ok(supplementService.getAllSupplements());
    }

    // ✅ 3. Obtener por ID (GET /.../{id})
    @GetMapping("/{id:\\d+}") // Añadimos la validación de ruta numérica
    public ResponseEntity<SupplementResponseDTO> getSupplementById(@PathVariable("id") Integer id) {
        // CORRECCIÓN: Usamos el nombre de método corregido en el servicio
        return wrapOrNotFound(supplementService.getSupplementById(id));
    }

    // ✅ 4. Actualizar (PUT /.../{id})
    @PutMapping("/{id:\\d+}") // Añadimos la validación de ruta numérica
    public ResponseEntity<SupplementResponseDTO> updateSupplement(
            @PathVariable("id") Integer id,
            @Valid @RequestBody SupplementRequestDTO dto) { // Añadimos @Valid
        
        // CORRECCIÓN: Usamos el nombre de método corregido y confiamos en la excepción 404
        SupplementResponseDTO updatedDto = supplementService.updateSupplement(id, dto);
        return ResponseEntity.ok(updatedDto);
    }

    // ✅ 5. Eliminar (DELETE /.../{id})
    @DeleteMapping("/{id:\\d+}") // Añadimos la validación de ruta numérica
    public ResponseEntity<Void> deleteSupplement(@PathVariable("id") Integer id) {
        // CORRECCIÓN: Usamos el nombre de método corregido
        supplementService.deleteSupplement(id);
        return ResponseEntity.noContent().build();
    }

    // -------------------- HELPER --------------------
    private <T> ResponseEntity<T> wrapOrNotFound(Optional<T> maybeResponse) {
        return maybeResponse.map(ResponseEntity::ok)
                            .orElse(ResponseEntity.notFound().build());
    }
}