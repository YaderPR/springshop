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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.validation.Valid; // Importamos Valid para usar la validación @NotBlank en el DTO

@RestController
// Mantener la ruta como subrecurso de products es RESTful para un recurso de catálogo
@RequestMapping("/api/products/categories") 
public class CategoryController {
    
    private final CategoryService categoryService;
    // La constante BASE_URL se ajusta para la creación de URI
    private static final String BASE_URL = "/api/products/categories"; 

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService; 
    }

    // -------------------- CRUD DE CATEGORÍAS --------------------------------

    // ✅ Listar todas las categorias (GET /api/products/categories)
    @GetMapping
    public ResponseEntity<List<CategoryResponseDTO>> getAllCategories() {
        return ResponseEntity.ok(categoryService.getAllCategories());
    }

    // ✅ Obtener una categoria por ID (GET /api/products/categories/{id})
    @GetMapping("/{id:\\d+}")
    public ResponseEntity<CategoryResponseDTO> getCategoryById(@PathVariable Integer id) {
        // CORREGIDO: Usar @PathVariable
        return wrapOrNotFound(categoryService.getCategoryById(id));
    }
    
    // ✅ Crear una nueva categoria (POST /api/products/categories)
    @PostMapping
    public ResponseEntity<CategoryResponseDTO> createCategory(@Valid @RequestBody CategoryRequestDTO dto) {
        // Usamos @Valid para activar la validación @NotBlank del DTO
        CategoryResponseDTO response = categoryService.createCategory(dto);
        URI location = URI.create(BASE_URL + "/" + response.getId());
        // CORREGIDO: BASE_URL ahora es consistente
        return ResponseEntity.created(location).body(response);
    }
    
    // ✅ Actualizar una categoria existente (PUT /api/products/categories/{id})
    @PutMapping("/{id:\\d+}")
    public ResponseEntity<CategoryResponseDTO> updateCategory(
            @PathVariable Integer id, // CORREGIDO: Añadir @PathVariable
            @Valid @RequestBody CategoryRequestDTO dto) { // CORREGIDO: Añadir @RequestBody y @Valid
        
        // Dado que el servicio CategoryService ya lanza EntityNotFoundException (404)
        // en caso de no encontrar el ID, podemos devolver 200 OK directamente.
        CategoryResponseDTO response = categoryService.updateCategory(id, dto);
        return ResponseEntity.ok(response);
    }
    
    // ✅ Eliminar una categoria (DELETE /api/products/categories/{id})
    @DeleteMapping("/{id:\\d+}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Integer id) { // CORREGIDO: Añadir @PathVariable
        // El servicio maneja el 404. Si es exitoso, devuelve 204 No Content.
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }
    
    // -------------------- HELPER --------------------
    
    private <T> ResponseEntity<T> wrapOrNotFound(Optional<T> maybeResponse) {
        return maybeResponse.map(ResponseEntity::ok)
                            .orElse(ResponseEntity.notFound().build());
    }
}