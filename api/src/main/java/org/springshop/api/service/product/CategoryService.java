// Archivo: org.springshop.api.service.product.CategoryService.java (Refactorizado)

package org.springshop.api.service.product;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springshop.api.dto.product.CategoryRequestDTO;
import org.springshop.api.dto.product.CategoryResponseDTO;
import org.springshop.api.mapper.product.CategoryMapper;
import org.springshop.api.model.product.Category;
import org.springshop.api.repository.product.CategoryRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
@Transactional
public class CategoryService {
    
    private final CategoryRepository categoryRepository;
    
    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }
    
    // -------------------- CRUD DE CATEGORIAS --------------------------------
    
    /**
     * Lista todas las categorías.
     */
    @Transactional(readOnly = true)
    public List<CategoryResponseDTO> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(CategoryMapper::toResponseDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Obtiene una categoría por ID.
     */
    @Transactional(readOnly = true)
    public Optional<CategoryResponseDTO> getCategoryById(Integer id) {
        return categoryRepository.findById(id).map(CategoryMapper::toResponseDTO);
    }
    
    /**
     * Crea una nueva categoría.
     */
    public CategoryResponseDTO createCategory(CategoryRequestDTO dto) {
        Category saved = categoryRepository.save(CategoryMapper.toEntity(dto));
        return CategoryMapper.toResponseDTO(saved);
    }
    
    /**
     * Actualiza una categoría existente.
     */
    public CategoryResponseDTO updateCategory(Integer id, CategoryRequestDTO dto) {
        // 1. Centralizamos la búsqueda del recurso
        Category existing = findCategoryOrThrow(id);
        
        // 2. Delegamos la lógica de mapeo a la clase Mapper.
        // Asumimos que el Mapper tiene el método updateCategory(existing, dto)
        CategoryMapper.updateCategory(existing, dto); 
        
        // No es estrictamente necesario llamar a save() si solo se modificó una
        // propiedad primitiva y la transacción está abierta, pero lo mantenemos
        // para asegurar la persistencia explícita si el Mapper hiciera algo más complejo.
        Category updatedCategory = categoryRepository.save(existing);
        return CategoryMapper.toResponseDTO(updatedCategory);
    }
    
    /**
     * Elimina una categoría.
     */
    public void deleteCategory(Integer id) {
        // Optimizamos la eliminación: buscar y eliminar (una consulta)
        // en lugar de verificar existencia y luego eliminar por ID (dos consultas).
        Category category = findCategoryOrThrow(id);
        
        // NOTA: En un sistema real, aquí se debe verificar si la categoría
        // tiene productos asociados y decidir la política (lanzar error, mover a 'Uncategorized', etc.).
        
        categoryRepository.delete(category);
    }
    
    // -------------------- MÉTODOS AUXILIARES Y DE BÚSQUEDA --------------------
    
    /**
     * Busca una Categoría por ID o lanza EntityNotFoundException.
     * Método público para ser usado por otros servicios (como ProductService).
     */
    public Category findCategoryOrThrow(Integer categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new EntityNotFoundException("Category not found with id: " + categoryId));
    }
}