// Archivo: org.springshop.api.controller.product.ApparelController.java (Refinado)
package org.springshop.api.controller.product;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springshop.api.dto.product.apparel.ApparelRequestDTO;
import org.springshop.api.dto.product.apparel.ApparelResponseDTO;
import org.springshop.api.service.product.ApparelService;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import jakarta.validation.Valid; // Importamos la validación

@RestController
@RequestMapping("/api/products/apparels")
public class ApparelController {

    private final ApparelService apparelService;
    private static final String BASE_URL = "/api/products/apparels";

    public ApparelController(ApparelService apparelService) {
        this.apparelService = apparelService;
    }

    // -------------------- APPAREL ESPECÍFICO --------------------
    
    // ✅ 1. Crear Producto (POST /api/products/apparels)
    @PostMapping
    public ResponseEntity<ApparelResponseDTO> createApparel(@Valid @RequestBody ApparelRequestDTO dto) {
        // Añadimos @Valid para activar las validaciones del DTO.
        ApparelResponseDTO response = apparelService.createApparel(dto);
        URI location = URI.create(BASE_URL + "/" + response.getId());
        return ResponseEntity.created(location).body(response);
    }

    // ✅ 2. Obtener Todos / Filtrar (GET /api/products/apparels?categoryId=N)
    // El uso de @RequestParam para filtrar es la solución más RESTful para colecciones.
    @GetMapping
    public ResponseEntity<List<ApparelResponseDTO>> getAllOrFilteredApparels(
        @RequestParam(required = false) Integer categoryId
    ) {
        if (categoryId != null) {
            return ResponseEntity.ok(apparelService.getApparelsByCategoryId(categoryId));
        }
        return ResponseEntity.ok(apparelService.getAllApparels());
    }

    // ✅ 3. Obtener por ID (GET /api/products/apparels/{id})
    @GetMapping("/{id:\\d+}") // Añadimos la validación de ruta numérica
    public ResponseEntity<ApparelResponseDTO> getApparelById(@PathVariable("id") Integer id) {
        return wrapOrNotFound(apparelService.getApparelById(id));
    }

    // ✅ 4. Actualizar (PUT /api/products/apparels/{id})
    @PutMapping("/{id:\\d+}") // Añadimos la validación de ruta numérica
    public ResponseEntity<ApparelResponseDTO> updateApparel(
            @PathVariable("id") Integer id,
            @Valid @RequestBody ApparelRequestDTO dto) { // Añadimos @Valid
        
        // CONSISTENCIA: Confiamos en que el Servicio lance EntityNotFoundException (404)
        // en lugar de usar Optional.of() y el helper. Esto es más limpio para el PUT.
        ApparelResponseDTO updatedDto = apparelService.updateApparel(id, dto);
        return ResponseEntity.ok(updatedDto);
    }

    // ✅ 5. Eliminar (DELETE /api/products/apparels/{id})
    @DeleteMapping("/{id:\\d+}") // Añadimos la validación de ruta numérica
    public ResponseEntity<Void> deleteApparel(@PathVariable("id") Integer id) {
        apparelService.deleteApparel(id);
        return ResponseEntity.noContent().build();
    }

    // -------------------- HELPER --------------------
    private <T> ResponseEntity<T> wrapOrNotFound(Optional<T> maybeResponse) {
        return maybeResponse.map(ResponseEntity::ok)
                            .orElse(ResponseEntity.notFound().build());
    }
}