package org.springshop.api.controller.product;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springshop.api.dto.product.workoutaccessory.WorkoutAccessoryCategoryRequestDTO;
import org.springshop.api.dto.product.workoutaccessory.WorkoutAccessoryCategoryResponseDTO;
import org.springshop.api.dto.product.workoutaccessory.WorkoutAccessoryResponseDTO;
import org.springshop.api.service.product.WorkoutAccessoryCategoryService;
import org.springshop.api.service.product.WorkoutAccessoryService;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/products/workoutaccessories/categories")
public class WorkoutAccessoryCategoryController {

    private final WorkoutAccessoryCategoryService categoryService;
    private final WorkoutAccessoryService accessoryService;

    private final static String BASE_URL = "/api/products/workoutaccessories/categories";

    WorkoutAccessoryCategoryController(
            WorkoutAccessoryCategoryService categoryService,
            WorkoutAccessoryService accessoryService) {
        this.categoryService = categoryService;
        this.accessoryService = accessoryService;
    }

    @PostMapping
    public ResponseEntity<WorkoutAccessoryCategoryResponseDTO> createWorkoutAccessoryCategory(
            @Valid @RequestBody WorkoutAccessoryCategoryRequestDTO requestDTO) {

        WorkoutAccessoryCategoryResponseDTO responseDto = categoryService.createWorkoutAccessoryCategory(requestDTO);

        return ResponseEntity.created(
                URI.create(BASE_URL + "/" + responseDto.getId()))
                .body(responseDto);
    }

    @GetMapping
    public ResponseEntity<List<WorkoutAccessoryCategoryResponseDTO>> getAllWorkoutAccessoryCategories() {

        return ResponseEntity.ok(categoryService.getAllWorkoutAccessoryCategories());
    }

    @GetMapping("/{id:\\d+}")
    public ResponseEntity<WorkoutAccessoryCategoryResponseDTO> getWorkoutAccessoryCategoryById(
            @PathVariable Integer id) {

        return wrapOrNotFound(categoryService.getWorkoutAccessoryCategoryById(id));
    }

    @PutMapping("/{id:\\d+}")
    public ResponseEntity<WorkoutAccessoryCategoryResponseDTO> updateWorkoutAccessoryCategory(
            @PathVariable Integer id,
            @Valid @RequestBody WorkoutAccessoryCategoryRequestDTO requestDto) { 

        WorkoutAccessoryCategoryResponseDTO responseDto = categoryService.updateWorkoutAccessoryCategory(id,
                requestDto);

        return ResponseEntity.ok(responseDto);
    }

    @DeleteMapping("/{id:\\d+}")
    public ResponseEntity<Void> deleteWorkoutAccessoryCategory(@PathVariable Integer id) {

        categoryService.deleteWorkoutAccessoryCategory(id);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id:\\d+}/workoutaccessories")
    public ResponseEntity<List<WorkoutAccessoryResponseDTO>> getWorkoutAccessoriesByCategoryId(
            @PathVariable Integer id) {

        List<WorkoutAccessoryResponseDTO> accessories = accessoryService.getWorkoutAccessoriesByWorkoutCategoryId(id);

        if (accessories.isEmpty()) {
            return ResponseEntity.ok(accessories);
        }

        return ResponseEntity.ok(accessories);
    }

    private <T> ResponseEntity<T> wrapOrNotFound(Optional<T> maybeResponse) {
        return maybeResponse.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}