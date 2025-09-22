package org.springshop.api.controller.product;

import org.springframework.web.bind.annotation.RestController;
import org.springshop.api.dto.product.CategoryRequestDTO;
import org.springshop.api.dto.product.CategoryResponseDTO;
import org.springshop.api.service.product.CategoryService;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping("/api/products/categories")  
public class CategoryController {
    private final CategoryService categoryService;
    private static final String BASE_URL = "/api/product/categories";

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService; 
        
    }
    //Listar todas las categorias
    @GetMapping
    public ResponseEntity<List<CategoryResponseDTO>> getAllCategories() {
        return ResponseEntity.ok(categoryService.getAllCategories());
    }
    //Crear una nueva categoria
    @PostMapping
    public ResponseEntity<CategoryResponseDTO> createCategory(@RequestBody CategoryRequestDTO dto) {
        CategoryResponseDTO response = categoryService.createCategory(dto);
        return ResponseEntity.created(URI.create(BASE_URL + "/" + response.getId()))
                             .body(response);
    }
    //Actualizar una categoria existente
    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponseDTO> updateCategory(Integer id, CategoryRequestDTO dto) {
        return wrapOrNotFound(Optional.of(categoryService.updateCategory(id, dto)));
    }
    //Eliminar una categoria
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(Integer id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }
    //Buscar una categoria por ID
    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponseDTO> getCategoryById(@RequestBody Integer id) {
        return wrapOrNotFound(categoryService.getCategoryById(id));
    }
        // -------------------- HELPER --------------------
    private <T> ResponseEntity<T> wrapOrNotFound(Optional<T> maybeResponse) {
        return maybeResponse.map(ResponseEntity::ok)
                            .orElse(ResponseEntity.notFound().build());
    }
}