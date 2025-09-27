package org.springshop.api.controller.product;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springshop.api.dto.product.workoutaccessory.WorkoutAccessoryRequestDTO;
import org.springshop.api.dto.product.workoutaccessory.WorkoutAccessoryResponseDTO;
import org.springshop.api.service.product.WorkoutAccessoryService;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import jakarta.validation.Valid; // Importamos la validación

@RestController
@RequestMapping("/api/products/workout-accessories")
public class WorkoutAccessoryController {

    private final WorkoutAccessoryService accessoryService;
    private static final String BASE_URL = "/api/products/workout-accessories";

    public WorkoutAccessoryController(WorkoutAccessoryService accessoryService) {
        this.accessoryService = accessoryService;
    }

    // -------------------- WORKOUT ACCESSORIES ESPECÍFICO --------------------
    
    // ✅ 1. Crear Producto (POST /api/products/workout-accessories)
    @PostMapping
    public ResponseEntity<WorkoutAccessoryResponseDTO> createWorkoutAccessory(
            @Valid @RequestBody WorkoutAccessoryRequestDTO dto) { // Añadimos @Valid
        
        WorkoutAccessoryResponseDTO response = accessoryService.createWorkoutAccessory(dto);
        URI location = URI.create(BASE_URL + "/" + response.getId());
        return ResponseEntity.created(location).body(response);
    }

    // ✅ 2. Obtener Todos y Filtrados (GET /.../?categoryId=N)
    // Soportamos el filtrado por la Categoría Genérica
    @GetMapping
    public ResponseEntity<List<WorkoutAccessoryResponseDTO>> getAllOrFilteredWorkoutAccessories(
        @RequestParam(required = false) Integer categoryId
    ) {
        if (categoryId != null) {
            // Usamos el nuevo método de filtrado por categoría genérica
            return ResponseEntity.ok(accessoryService.getWorkoutAccessoriesByCategoryId(categoryId));
        }
        return ResponseEntity.ok(accessoryService.getAllWorkoutAccessories());
    }

    // ✅ 3. Obtener por ID (GET /.../{id})
    @GetMapping("/{id:\\d+}") // Añadimos la validación de ruta numérica
    public ResponseEntity<WorkoutAccessoryResponseDTO> getWorkoutAccessoryById(@PathVariable("id") Integer id) {
        return wrapOrNotFound(accessoryService.getWorkoutAccessoryById(id));
    }

    // ✅ 4. Actualizar (PUT /.../{id})
    @PutMapping("/{id:\\d+}") // Añadimos la validación de ruta numérica
    public ResponseEntity<WorkoutAccessoryResponseDTO> updateWorkoutAccessory(
            @PathVariable("id") Integer id,
            @Valid @RequestBody WorkoutAccessoryRequestDTO dto) { // Añadimos @Valid
        
        // CONSISTENCIA: Confiamos en que el Servicio lance EntityNotFoundException (404)
        WorkoutAccessoryResponseDTO updatedDto = accessoryService.updateWorkoutAccessory(id, dto);
        return ResponseEntity.ok(updatedDto);
    }

    // ✅ 5. Eliminar (DELETE /.../{id})
    @DeleteMapping("/{id:\\d+}") // Añadimos la validación de ruta numérica
    public ResponseEntity<Void> deleteWorkoutAccessory(@PathVariable("id") Integer id) {
        accessoryService.deleteWorkoutAccessory(id);
        return ResponseEntity.noContent().build();
    }

    // -------------------- HELPER --------------------
    private <T> ResponseEntity<T> wrapOrNotFound(Optional<T> maybeResponse) {
        return maybeResponse.map(ResponseEntity::ok)
                            .orElse(ResponseEntity.notFound().build());
    }
}