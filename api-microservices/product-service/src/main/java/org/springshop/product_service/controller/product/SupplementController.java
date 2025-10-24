package org.springshop.product_service.controller.product;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springshop.product_service.dto.product.supplement.SupplementRequest;
import org.springshop.product_service.dto.product.supplement.SupplementResponse;
import org.springshop.product_service.service.product.SupplementService;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v2/products/supplements")
public class SupplementController {

    private final SupplementService supplementService;
    private static final String BASE_URL = "/api/v2/products/supplements";

    public SupplementController(SupplementService supplementService) {
        this.supplementService = supplementService;
    }

    @PostMapping
    public ResponseEntity<SupplementResponse> createSupplement(
            @Valid @RequestBody SupplementRequest dto) {

        SupplementResponse response = supplementService.createSupplement(dto);
        URI location = URI.create(BASE_URL + "/" + response.getId());

        return ResponseEntity.created(location).body(response);
    }

    @GetMapping
    public ResponseEntity<List<SupplementResponse>> getAllOrFilteredSupplements(
            @RequestParam(required = false) Integer categoryId) {

        if (categoryId != null) {
            return ResponseEntity.ok(supplementService.getSupplementsByCategoryId(categoryId));
        }

        return ResponseEntity.ok(supplementService.getAllSupplements());
    }

    @GetMapping("/{id:\\d+}")
    public ResponseEntity<SupplementResponse> getSupplementById(@PathVariable("id") Integer id) {

        return wrapOrNotFound(supplementService.getSupplementById(id));
    }

    @PutMapping("/{id:\\d+}")
    public ResponseEntity<SupplementResponse> updateSupplement(
            @PathVariable Integer id,
            @Valid @RequestBody SupplementRequest dto) {

        SupplementResponse updatedDto = supplementService.updateSupplement(id, dto);

        return ResponseEntity.ok(updatedDto);
    }

    @DeleteMapping("/{id:\\d+}")
    public ResponseEntity<Void> deleteSupplement(@PathVariable Integer id) {

        supplementService.deleteSupplement(id);

        return ResponseEntity.noContent().build();
    }

    private <T> ResponseEntity<T> wrapOrNotFound(Optional<T> maybeResponse) {
        
        return maybeResponse.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}