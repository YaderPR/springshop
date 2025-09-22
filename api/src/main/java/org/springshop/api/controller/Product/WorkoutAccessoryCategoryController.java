package org.springshop.api.controller.product;

import java.net.URI;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springshop.api.dto.product.workoutaccessory.WorkoutAccessoryCategoryRequestDTO;
import org.springshop.api.dto.product.workoutaccessory.WorkoutAccessoryCategoryResponseDTO;
import org.springshop.api.mapper.product.WorkoutAccessoryCategoryMapper;
import org.springshop.api.model.product.WorkoutAccessory;
import org.springshop.api.model.product.WorkoutAccessoryCategory;
import org.springshop.api.repository.product.WorkoutAccessoryCategoryRepository;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import jakarta.persistence.EntityNotFoundException;

@RestController
@RequestMapping("/api/products/workoutaccesories/categories")
public class WorkoutAccessoryCategoryController {
    private final WorkoutAccessoryCategoryRepository wacr;
    private final static String BASE_URL = "/api/products/workoutaccesories/categories";

    WorkoutAccessoryCategoryController(WorkoutAccessoryCategoryRepository wacr) {
        this.wacr = wacr;
    }

    @GetMapping
    public ResponseEntity<List<WorkoutAccessoryCategoryResponseDTO>> getAllWorkoutAccessoryCategories() {
        return ResponseEntity.ok(wacr.findAll().stream().map(WorkoutAccessoryCategoryMapper::toResponseDTO)
                .collect(Collectors.toList()));
    }

    @PostMapping
    public ResponseEntity<WorkoutAccessoryCategoryResponseDTO> createWorkoutAccessoryCategory(
            @RequestBody WorkoutAccessoryCategoryRequestDTO requestDTO) {
        WorkoutAccessoryCategory savedCategory = wacr.save(WorkoutAccessoryCategoryMapper.toEntity(requestDTO));
        WorkoutAccessoryCategoryResponseDTO responseDto = WorkoutAccessoryCategoryMapper.toResponseDTO(savedCategory);
        return ResponseEntity.created(
                URI.create(BASE_URL + "/" + responseDto.getId()))
                .body(responseDto);
    }

    @GetMapping("/{id:\\d+}/workoutaccessories")
    public ResponseEntity<Set<WorkoutAccessory>> getWorkoutAccessoriesByCategoryId(@PathVariable Integer id) {
        Set<WorkoutAccessory> accessories = wacr.findById(id)
                .map(WorkoutAccessoryCategory::getWorkoutAccessories)
                .orElseThrow(() -> new EntityNotFoundException("Category not found with id " + id));
        return ResponseEntity.ok(accessories);
    }

    @PutMapping("/{id}")
    public ResponseEntity<WorkoutAccessoryCategoryResponseDTO> updateWorkoutAccessoryCategory(@PathVariable Integer id,
            @RequestBody WorkoutAccessoryCategoryRequestDTO requestDto) {
        WorkoutAccessoryCategory existingCategory = wacr.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Category not found with id " + id));
        WorkoutAccessoryCategoryMapper.updateEntity(existingCategory, requestDto);
        WorkoutAccessoryCategory updatedCategory = wacr.save(existingCategory);
        WorkoutAccessoryCategoryResponseDTO responseDto = WorkoutAccessoryCategoryMapper.toResponseDTO(updatedCategory);
        return ResponseEntity.ok(responseDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWorkoutAccessoryCategoryById(@PathVariable Integer id) {
        if (!wacr.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        wacr.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
