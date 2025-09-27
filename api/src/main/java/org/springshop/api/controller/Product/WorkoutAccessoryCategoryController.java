package org.springshop.api.controller.product;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*; // Usamos el RequestMapping de Spring
import org.springshop.api.dto.product.workoutaccessory.WorkoutAccessoryCategoryRequestDTO;
import org.springshop.api.dto.product.workoutaccessory.WorkoutAccessoryCategoryResponseDTO;
import org.springshop.api.dto.product.workoutaccessory.WorkoutAccessoryResponseDTO; // Para la lista de accesorios
import org.springshop.api.service.product.WorkoutAccessoryCategoryService; // Inyectamos el servicio
import org.springshop.api.service.product.WorkoutAccessoryService; // Para obtener accesorios por categoría

import java.net.URI;
import java.util.List;
import java.util.Optional;

import jakarta.validation.Valid; // Para la validación

@RestController
// Ruta RESTful, asumiendo que el recurso existe bajo /products/workoutaccessories
@RequestMapping("/api/products/workoutaccessories/categories")
public class WorkoutAccessoryCategoryController {
    
    // Inyectamos el servicio, no el repositorio
    private final WorkoutAccessoryCategoryService categoryService;
    // Inyectamos el servicio especializado para obtener accesorios por categoría
    private final WorkoutAccessoryService accessoryService; 
    
    private final static String BASE_URL = "/api/products/workoutaccessories/categories";

    WorkoutAccessoryCategoryController(
            WorkoutAccessoryCategoryService categoryService,
            WorkoutAccessoryService accessoryService) { // Inyectamos el servicio
        this.categoryService = categoryService;
        this.accessoryService = accessoryService;
    }

    // -------------------- CRUD de WorkoutAccessoryCategory --------------------

    // ✅ 1. Crear Categoría (POST /...)
    @PostMapping
    public ResponseEntity<WorkoutAccessoryCategoryResponseDTO> createWorkoutAccessoryCategory(
            @Valid @RequestBody WorkoutAccessoryCategoryRequestDTO requestDTO) { // Usamos @Valid
        
        // DELEGAMOS AL SERVICIO
        WorkoutAccessoryCategoryResponseDTO responseDto = categoryService.createWorkoutAccessoryCategory(requestDTO);
        
        return ResponseEntity.created(
                        URI.create(BASE_URL + "/" + responseDto.getId()))
                .body(responseDto);
    }

    // ✅ 2. Obtener Todas (GET /...)
    @GetMapping
    public ResponseEntity<List<WorkoutAccessoryCategoryResponseDTO>> getAllWorkoutAccessoryCategories() {
        // DELEGAMOS AL SERVICIO
        return ResponseEntity.ok(categoryService.getAllWorkoutAccessoryCategories());
    }
    
    // ✅ 3. Obtener por ID (GET /.../{id})
    @GetMapping("/{id:\\d+}")
    public ResponseEntity<WorkoutAccessoryCategoryResponseDTO> getWorkoutAccessoryCategoryById(@PathVariable Integer id) {
        // Usamos el helper para manejar el Optional/404
        return wrapOrNotFound(categoryService.getWorkoutAccessoryCategoryById(id));
    }

    // ✅ 4. Actualizar Categoría (PUT /.../{id})
    @PutMapping("/{id:\\d+}")
    public ResponseEntity<WorkoutAccessoryCategoryResponseDTO> updateWorkoutAccessoryCategory(
            @PathVariable Integer id,
            @Valid @RequestBody WorkoutAccessoryCategoryRequestDTO requestDto) { // Usamos @Valid
        
        // DELEGAMOS AL SERVICIO (el servicio maneja el 404)
        WorkoutAccessoryCategoryResponseDTO responseDto = categoryService.updateWorkoutAccessoryCategory(id, requestDto);
        return ResponseEntity.ok(responseDto);
    }

    // ✅ 5. Eliminar Categoría (DELETE /.../{id})
    @DeleteMapping("/{id:\\d+}")
    public ResponseEntity<Void> deleteWorkoutAccessoryCategory(@PathVariable Integer id) {
        // DELEGAMOS AL SERVICIO (el servicio maneja el 404 y la lógica de eliminación)
        categoryService.deleteWorkoutAccessoryCategory(id); 
        return ResponseEntity.noContent().build();
    }
    
    // -------------------- Relaciones --------------------

    // ✅ Obtener Accesorios por Categoría (GET /.../{id}/workoutaccessories)
    @GetMapping("/{id:\\d+}/workoutaccessories")
    public ResponseEntity<List<WorkoutAccessoryResponseDTO>> getWorkoutAccessoriesByCategoryId(@PathVariable Integer id) {
        // CORREGIDO: Usar el servicio especializado para obtener los DTOs de producto.
        // ASUMIMOS que el WorkoutAccessoryService tiene este método:
        // List<WorkoutAccessoryResponseDTO> getWorkoutAccessoriesByWorkoutCategoryId(Integer categoryId)
        
        List<WorkoutAccessoryResponseDTO> accessories = accessoryService.getWorkoutAccessoriesByWorkoutCategoryId(id);
        
        if (accessories.isEmpty()) {
            // Es preferible devolver 200 OK con lista vacía en lugar de 404 si la categoría existe.
            // Si la categoría no existe, el servicio debería lanzar 404.
            // Para este ejemplo, si la lista es vacía, asumimos que la categoría existe.
            return ResponseEntity.ok(accessories);
        }
        return ResponseEntity.ok(accessories);
    }

    // -------------------- HELPER --------------------
    
    private <T> ResponseEntity<T> wrapOrNotFound(Optional<T> maybeResponse) {
        return maybeResponse.map(ResponseEntity::ok)
                            .orElse(ResponseEntity.notFound().build());
    }
}