package org.springshop.api.controller.product;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springshop.api.dto.product.supplement.SupplementRequestDTO;
import org.springshop.api.dto.product.supplement.SupplementResponseDTO;
import org.springshop.api.service.product.SupplementService;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/products/supplements")
public class SupplementController {

    private final SupplementService supplementService;
    private static final String BASE_URL = "/api/products/supplements";

    public SupplementController(SupplementService supplementService) {
        this.supplementService = supplementService;
    }

    @PostMapping
    public ResponseEntity<SupplementResponseDTO> createSupplement(
            @Valid @RequestBody SupplementRequestDTO dto) {

        SupplementResponseDTO response = supplementService.createSupplement(dto);
        URI location = URI.create(BASE_URL + "/" + response.getId());

        return ResponseEntity.created(location).body(response);
    }

    @GetMapping
    public ResponseEntity<List<SupplementResponseDTO>> getAllOrFilteredSupplements(
            @RequestParam(required = false) Integer categoryId) {

        if (categoryId != null) {
            return ResponseEntity.ok(supplementService.getSupplementsByCategoryId(categoryId));
        }

        return ResponseEntity.ok(supplementService.getAllSupplements());
    }

    @GetMapping("/{id:\\d+}")
    public ResponseEntity<SupplementResponseDTO> getSupplementById(@PathVariable("id") Integer id) {

        return wrapOrNotFound(supplementService.getSupplementById(id));
    }

    @PutMapping("/{id:\\d+}")
    public ResponseEntity<SupplementResponseDTO> updateSupplement(
            @PathVariable Integer id,
            @Valid @RequestBody SupplementRequestDTO dto) {

        SupplementResponseDTO updatedDto = supplementService.updateSupplement(id, dto);

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