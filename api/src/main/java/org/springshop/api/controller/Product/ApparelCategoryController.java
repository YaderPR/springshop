package org.springshop.api.controller.product;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springshop.api.repository.product.ApparelCategoryRepository;

import jakarta.persistence.EntityNotFoundException;

import org.springshop.api.dto.product.apparel.ApparelCategoryRequestDTO;
import org.springshop.api.dto.product.apparel.ApparelCategoryResponseDTO;
import org.springshop.api.dto.product.apparel.ApparelResponseDTO;
import org.springshop.api.mapper.product.ApparelCategoryMapper;
import org.springshop.api.mapper.product.ApparelMapper;
import org.springshop.api.model.product.ApparelCategory;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("/api/products/apparel/categories")
public class ApparelCategoryController {
    private final static String BASE_URL = "/api/products/apparel/categories";
    ApparelCategoryRepository acr;

    ApparelCategoryController(ApparelCategoryRepository acr) {
        this.acr = acr;
    }

    @PostMapping
    public ResponseEntity<ApparelCategoryResponseDTO> createApparelCategory(
            @RequestBody ApparelCategoryRequestDTO requestDto) {

        ApparelCategory entity = ApparelCategoryMapper.toEntity(requestDto);
        ApparelCategory savedEntity = acr.save(entity);
        ApparelCategoryResponseDTO responseDto = ApparelCategoryMapper.toResponseDTO(savedEntity);

        return ResponseEntity
                .created(URI.create(BASE_URL + "/" + responseDto.getId()))
                .body(responseDto);
    }

    @GetMapping
    public ResponseEntity<List<ApparelCategoryResponseDTO>> getAllApparelCategories() {
        return ResponseEntity.ok(acr.findAll().stream()
                .map(ApparelCategoryMapper::toResponseDTO)
                .collect(Collectors.toList()));
    }

    @GetMapping("/{id:\\d+}/apparels")
    public ResponseEntity<List<ApparelResponseDTO>> getApparelsByCategoryId(
            @PathVariable Integer id) {
        ApparelCategory category = acr.findById(id).orElse(null);
        if (category == null) {
            return ResponseEntity.notFound().build();
        }
        List<ApparelResponseDTO> apparels = category.getApparels().stream()
                .map(apparel -> ApparelMapper.toDTO(apparel)).collect(Collectors.toList());
        return ResponseEntity.ok(apparels);
    }
    @PutMapping("/{id}")
    public ResponseEntity<ApparelCategoryResponseDTO> updateApparelCategoryById(@PathVariable Integer id, @RequestBody ApparelCategoryRequestDTO dto) {
        ApparelCategory apparelCategory = acr.findById(id).orElseThrow(() -> new EntityNotFoundException("Apparel category not found with id " + id));
        ApparelCategoryMapper.updateEntity(apparelCategory, dto);
        ApparelCategory updatedApparelCategory = acr.save(apparelCategory);
        ApparelCategoryResponseDTO responseDto = ApparelCategoryMapper.toResponseDTO(updatedApparelCategory);
        return ResponseEntity.ok(responseDto);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteApparelCategoryById(@PathVariable Integer id) {
        if (!acr.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        acr.deleteById(id);
        return ResponseEntity.noContent().build();
    }

}
