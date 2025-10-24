package org.springshop.product_service.controller.product;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springshop.product_service.dto.product.workoutaccessory.WorkoutAccessoryRequest;
import org.springshop.product_service.dto.product.workoutaccessory.WorkoutAccessoryResponse;
import org.springshop.product_service.service.product.WorkoutAccessoryService;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import jakarta.validation.Valid; 

@RestController
@RequestMapping("/api/v2/products/workout-accessories")
public class WorkoutAccessoryController {

    private final WorkoutAccessoryService accessoryService;
    private static final String BASE_URL = "/api/v2/products/workout-accessories";

    public WorkoutAccessoryController(WorkoutAccessoryService accessoryService) {
        this.accessoryService = accessoryService;
    }

    @PostMapping
    public ResponseEntity<WorkoutAccessoryResponse> createWorkoutAccessory(
            @Valid @RequestBody WorkoutAccessoryRequest dto) {
        
        WorkoutAccessoryResponse response = accessoryService.createWorkoutAccessory(dto);
        URI location = URI.create(BASE_URL + "/" + response.getId());

        return ResponseEntity.created(location).body(response);
    }

    @GetMapping
    public ResponseEntity<List<WorkoutAccessoryResponse>> getAllOrFilteredWorkoutAccessories(
        @RequestParam(required = false) Integer categoryId
    ) {

        if (categoryId != null) {
            return ResponseEntity.ok(accessoryService.getWorkoutAccessoriesByCategoryId(categoryId));
        }

        return ResponseEntity.ok(accessoryService.getAllWorkoutAccessories());
    }


    @GetMapping("/{id:\\d+}")
    public ResponseEntity<WorkoutAccessoryResponse> getWorkoutAccessoryById(@PathVariable("id") Integer id) {
        return wrapOrNotFound(accessoryService.getWorkoutAccessoryById(id));
    }

    @PutMapping("/{id:\\d+}") 
    public ResponseEntity<WorkoutAccessoryResponse> updateWorkoutAccessory(
            @PathVariable Integer id,
            @Valid @RequestBody WorkoutAccessoryRequest dto) { 
        
        WorkoutAccessoryResponse updatedDto = accessoryService.updateWorkoutAccessory(id, dto);

        return ResponseEntity.ok(updatedDto);
    }

    @DeleteMapping("/{id:\\d+}") 
    public ResponseEntity<Void> deleteWorkoutAccessory(@PathVariable Integer id) {

        accessoryService.deleteWorkoutAccessory(id);

        return ResponseEntity.noContent().build();
    }
    private <T> ResponseEntity<T> wrapOrNotFound(Optional<T> maybeResponse) {
        
        return maybeResponse.map(ResponseEntity::ok)
                            .orElse(ResponseEntity.notFound().build());
    }
}