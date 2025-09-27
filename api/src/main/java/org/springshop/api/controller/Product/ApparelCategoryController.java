package org.springshop.api.controller.product;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springshop.api.service.product.ApparelCategoryService;
import org.springshop.api.dto.product.apparel.ApparelCategoryRequestDTO;
import org.springshop.api.dto.product.apparel.ApparelCategoryResponseDTO;

import java.net.URI;
import java.util.List;
import java.util.Optional; // Importación necesaria para el helper

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import jakarta.validation.Valid; // Importación necesaria para la validación

@RestController
@RequestMapping("/api/products/apparels/categories")
public class ApparelCategoryController {
    
    private final static String BASE_URL = "/api/products/apparels/categories";
    private final ApparelCategoryService apparelCategoryService;
    
    ApparelCategoryController(ApparelCategoryService apparelCategoryService) {
        this.apparelCategoryService = apparelCategoryService;
    }

    // -------------------- CRUD de ApparelCategory --------------------
    
    // ✅ 1. Crear Categoría (POST)
    @PostMapping
    public ResponseEntity<ApparelCategoryResponseDTO> createApparelCategory(
            @Valid @RequestBody ApparelCategoryRequestDTO requestDto) { // Añadimos @Valid

        ApparelCategoryResponseDTO responseDto = apparelCategoryService.createApparelCategory(requestDto);
        return ResponseEntity
                .created(URI.create(BASE_URL + "/" + responseDto.getId()))
                .body(responseDto);
    }

    // ✅ 2. Obtener Todas (GET)
    @GetMapping
    public ResponseEntity<List<ApparelCategoryResponseDTO>> getAllApparelCategories() {
        return ResponseEntity.ok(apparelCategoryService.getAllApparelCategories());
    }

    // ✅ 3. Obtener por ID (GET)
    @GetMapping("/{id:\\d+}") // Añadimos validación numérica
    public ResponseEntity<ApparelCategoryResponseDTO> getApparelCategoryById(@PathVariable Integer id) {
        // Usamos el helper para manejar el Optional
        return wrapOrNotFound(apparelCategoryService.getApparelCategoryById(id));
    }

    // ✅ 4. Actualizar Categoría (PUT)
    @PutMapping("/{id:\\d+}") // Añadimos validación numérica
    public ResponseEntity<ApparelCategoryResponseDTO> updateApparelCategory(
            @PathVariable Integer id, 
            @Valid @RequestBody ApparelCategoryRequestDTO dto) { // Añadimos @Valid

        // ERROR CRÍTICO CORREGIDO: Llamar al método de ACTUALIZACIÓN, no al de OBTENCIÓN.
        // Confiamos en que el servicio lance 404 si no existe.
        ApparelCategoryResponseDTO responseDto = apparelCategoryService.updateApparelCategory(id, dto);
        return ResponseEntity.ok(responseDto);
    }
    
    // ✅ 5. Eliminar Categoría (DELETE)
    @DeleteMapping("/{id:\\d+}") // Añadimos validación numérica
    public ResponseEntity<Void> deleteApparelCategory(@PathVariable Integer id) {
        // CORREGIDO: Llamar al método refactorizado. El servicio maneja el 404.
        apparelCategoryService.deleteApparelCategory(id);
        return ResponseEntity.noContent().build();
    }
    
    // -------------------- HELPER --------------------
    
    // Añadimos el helper para el manejo de Optional/404
    private <T> ResponseEntity<T> wrapOrNotFound(Optional<T> maybeResponse) {
        return maybeResponse.map(ResponseEntity::ok)
                            .orElse(ResponseEntity.notFound().build());
    }
}