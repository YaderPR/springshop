package org.springshop.api.controller.product;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springshop.api.dto.product.apparel.ApparelRequestDTO;
import org.springshop.api.dto.product.apparel.ApparelResponseDTO;
import org.springshop.api.service.product.ApparelService;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/products/apparels")
public class ApparelController {

    private final ApparelService apparelService;
    private static final String BASE_URL = "/api/products/apparels";

    public ApparelController(ApparelService apparelService) {
        this.apparelService = apparelService;
    }

    @PostMapping
    public ResponseEntity<ApparelResponseDTO> createApparel(@Valid @RequestBody ApparelRequestDTO dto) {

        ApparelResponseDTO response = apparelService.createApparel(dto);
        URI location = URI.create(BASE_URL + "/" + response.getId());

        return ResponseEntity.created(location).body(response);
    }

    @GetMapping
    public ResponseEntity<List<ApparelResponseDTO>> getAllOrFilteredApparels(
            @RequestParam(required = false) Integer categoryId) {
        if (categoryId != null) {
            return ResponseEntity.ok(apparelService.getApparelsByCategoryId(categoryId));
        }
        return ResponseEntity.ok(apparelService.getAllApparels());
    }

    @GetMapping("/{id:\\d+}")
    public ResponseEntity<ApparelResponseDTO> getApparelById(@PathVariable Integer id) {

        return wrapOrNotFound(apparelService.getApparelById(id));
    }

    @PutMapping("/{id:\\d+}")
    public ResponseEntity<ApparelResponseDTO> updateApparel(
            @PathVariable Integer id,
            @Valid @RequestBody ApparelRequestDTO dto) {

        ApparelResponseDTO updatedDto = apparelService.updateApparel(id, dto);

        return ResponseEntity.ok(updatedDto);
    }

    @DeleteMapping("/{id:\\d+}")
    public ResponseEntity<Void> deleteApparel(@PathVariable("id") Integer id) {
        apparelService.deleteApparel(id);
        return ResponseEntity.noContent().build();
    }

    private <T> ResponseEntity<T> wrapOrNotFound(Optional<T> maybeResponse) {
        return maybeResponse.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}