package org.springshop.product_service.controller.product;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springshop.product_service.dto.product.workoutaccessory.WorkoutAccessoryCategoryRequest;
import org.springshop.product_service.dto.product.workoutaccessory.WorkoutAccessoryCategoryResponse;
import org.springshop.product_service.dto.product.workoutaccessory.WorkoutAccessoryResponse;
import org.springshop.product_service.service.product.WorkoutAccessoryCategoryService;
import org.springshop.product_service.service.product.WorkoutAccessoryService;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v2/products/workoutaccessories/categories")
public class WorkoutAccessoryCategoryController {

    private final WorkoutAccessoryCategoryService categoryService;
    private final WorkoutAccessoryService accessoryService;

    private final static String BASE_URL = "/api/v2/products/workoutaccessories/categories";

    WorkoutAccessoryCategoryController(
            WorkoutAccessoryCategoryService categoryService,
            WorkoutAccessoryService accessoryService) {
        this.categoryService = categoryService;
        this.accessoryService = accessoryService;
    }

    @PostMapping
    public ResponseEntity<WorkoutAccessoryCategoryResponse> createWorkoutAccessoryCategory(
            @Valid @RequestBody WorkoutAccessoryCategoryRequest requestDTO) {

        WorkoutAccessoryCategoryResponse responseDto = categoryService.createWorkoutAccessoryCategory(requestDTO);

        return ResponseEntity.created(
                URI.create(BASE_URL + "/" + responseDto.getId()))
                .body(responseDto);
    }

    @GetMapping
    public ResponseEntity<List<WorkoutAccessoryCategoryResponse>> getAllWorkoutAccessoryCategories() {

        return ResponseEntity.ok(categoryService.getAllWorkoutAccessoryCategories());
    }

    @GetMapping("/{id:\\d+}")
    public ResponseEntity<WorkoutAccessoryCategoryResponse> getWorkoutAccessoryCategoryById(
            @PathVariable Integer id) {

        return wrapOrNotFound(categoryService.getWorkoutAccessoryCategoryById(id));
    }

    @PutMapping("/{id:\\d+}")
    public ResponseEntity<WorkoutAccessoryCategoryResponse> updateWorkoutAccessoryCategory(
            @PathVariable Integer id,
            @Valid @RequestBody WorkoutAccessoryCategoryRequest requestDto) { 

        WorkoutAccessoryCategoryResponse responseDto = categoryService.updateWorkoutAccessoryCategory(id,
                requestDto);

        return ResponseEntity.ok(responseDto);
    }

    @DeleteMapping("/{id:\\d+}")
    public ResponseEntity<Void> deleteWorkoutAccessoryCategory(@PathVariable Integer id) {

        categoryService.deleteWorkoutAccessoryCategory(id);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id:\\d+}/workoutaccessories")
    public ResponseEntity<List<WorkoutAccessoryResponse>> getWorkoutAccessoriesByCategoryId(
            @PathVariable Integer id) {

        List<WorkoutAccessoryResponse> accessories = accessoryService.getWorkoutAccessoriesByWorkoutCategoryId(id);

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