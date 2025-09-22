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
    CategoryRepository categoryRepository;
    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }
    // --------------------Categorias de producto--------------------------------
    //Listar todas las categorias
    public List<CategoryResponseDTO> getAllCategories() {
        return categoryRepository.findAll().stream().map(CategoryMapper::toResponseDTO).collect(Collectors.toList());
    }
    // Obtener una categoria por ID
    public Optional<CategoryResponseDTO> getCategoryById(Integer id) {
        return categoryRepository.findById(id).map(CategoryMapper::toResponseDTO);
    }
    // Crear una nueva categoria
    public CategoryResponseDTO createCategory(CategoryRequestDTO dto) {
        Category category = CategoryMapper.toEntity(dto);
        Category saved = categoryRepository.save(category);
        return CategoryMapper.toResponseDTO(saved);
    }
    // Actualizar una categoria existente
    public CategoryResponseDTO updateCategory(Integer id, CategoryRequestDTO dto) {
        Category existing = categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Category not found with id " + id));
        existing.setName(dto.getName());
        return CategoryMapper.toResponseDTO(existing);
    }
    // Eliminar una categoria
    public void deleteCategory(Integer id) {
        if (!categoryRepository.existsById(id)) {
            throw new EntityNotFoundException("Category not found with id " + id);
        }
        categoryRepository.deleteById(id);
    }
}
