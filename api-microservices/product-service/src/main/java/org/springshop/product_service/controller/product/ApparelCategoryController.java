package org.springshop.product_service.controller.product;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springshop.product_service.service.product.ApparelCategoryService;
import org.springshop.product_service.dto.product.apparel.ApparelCategoryRequest;
import org.springshop.product_service.dto.product.apparel.ApparelCategoryResponse;

import java.net.URI;
import java.util.List;
import java.util.Optional; 

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import jakarta.validation.Valid; 

@RestController
@RequestMapping("/api/products/apparels/categories")
public class ApparelCategoryController {
    
    private final static String BASE_URL = "/api/products/apparels/categories";
    private final ApparelCategoryService apparelCategoryService;
    
    ApparelCategoryController(ApparelCategoryService apparelCategoryService) {
        this.apparelCategoryService = apparelCategoryService;
    }

    @PostMapping
    public ResponseEntity<ApparelCategoryResponse> createApparelCategory(
            @Valid @RequestBody ApparelCategoryRequest requestDto) { 

        ApparelCategoryResponse responseDto = apparelCategoryService.createApparelCategory(requestDto);
        
        return ResponseEntity
                .created(URI.create(BASE_URL + "/" + responseDto.getId()))
                .body(responseDto);
    }

    @GetMapping
    public ResponseEntity<List<ApparelCategoryResponse>> getAllApparelCategories() {
        
        return ResponseEntity.ok(apparelCategoryService.getAllApparelCategories());
    }

    @GetMapping("/{id:\\d+}") 
    public ResponseEntity<ApparelCategoryResponse> getApparelCategoryById(@PathVariable Integer id) {

        return wrapOrNotFound(apparelCategoryService.getApparelCategoryById(id));
    }

    @PutMapping("/{id:\\d+}") 
    public ResponseEntity<ApparelCategoryResponse> updateApparelCategory(
            @PathVariable Integer id, 
            @Valid @RequestBody ApparelCategoryRequest dto) { 

        ApparelCategoryResponse responseDto = apparelCategoryService.updateApparelCategory(id, dto);
        
        return ResponseEntity.ok(responseDto);
    }
    
    @DeleteMapping("/{id:\\d+}") 
    public ResponseEntity<Void> deleteApparelCategory(@PathVariable Integer id) {

        apparelCategoryService.deleteApparelCategory(id);

        return ResponseEntity.noContent().build();
    }
    
    private <T> ResponseEntity<T> wrapOrNotFound(Optional<T> maybeResponse) {

        return maybeResponse.map(ResponseEntity::ok)
                            .orElse(ResponseEntity.notFound().build());
    }
}