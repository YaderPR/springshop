package org.springshop.product_service.service.product;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springshop.product_service.dto.product.workoutaccessory.WorkoutAccessoryCategoryRequest;
import org.springshop.product_service.dto.product.workoutaccessory.WorkoutAccessoryCategoryResponse;
import org.springshop.product_service.mapper.product.WorkoutAccessoryCategoryMapper;
import org.springshop.product_service.model.product.WorkoutAccessoryCategory;
import org.springshop.product_service.repository.product.WorkoutAccessoryCategoryRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
@Transactional
public class WorkoutAccessoryCategoryService {
    
    private final WorkoutAccessoryCategoryRepository categoryRepository;
    
    public WorkoutAccessoryCategoryService(WorkoutAccessoryCategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    // -------------------- LECTURA (READ) --------------------

    /**
     * Lista todas las categorías de accesorios de entrenamiento.
     */
    @Transactional(readOnly = true)
    public List<WorkoutAccessoryCategoryResponse> getAllWorkoutAccessoryCategories() {
        return categoryRepository.findAll().stream()
                .map(WorkoutAccessoryCategoryMapper::toResponseDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Obtiene una categoría por ID, devolviendo un Optional.
     */
    @Transactional(readOnly = true)
    public Optional<WorkoutAccessoryCategoryResponse> getWorkoutAccessoryCategoryById(Integer id) {
        return categoryRepository.findById(id)
                .map(WorkoutAccessoryCategoryMapper::toResponseDTO);
    }
    
    // -------------------- ESCRITURA (CREATE, UPDATE, DELETE) --------------------
    
    /**
     * Crea una nueva categoría de accesorio.
     */
    public WorkoutAccessoryCategoryResponse createWorkoutAccessoryCategory(WorkoutAccessoryCategoryRequest requestDTO) {
        WorkoutAccessoryCategory category = WorkoutAccessoryCategoryMapper.toEntity(requestDTO);
        WorkoutAccessoryCategory saved = categoryRepository.save(category);
        return WorkoutAccessoryCategoryMapper.toResponseDTO(saved);
    }
    
    /**
     * Actualiza una categoría existente.
     */
    public WorkoutAccessoryCategoryResponse updateWorkoutAccessoryCategory(Integer id, WorkoutAccessoryCategoryRequest requestDto) {
        // 1. Busca la entidad o lanza 404
        WorkoutAccessoryCategory category = findWorkoutAccessoryCategoryOrThrow(id);
        
        // 2. Delega la actualización al mapper (usando la convención updateWorkoutAccessoryCategory)
        WorkoutAccessoryCategoryMapper.updateWorkoutAccessoryCategory(category, requestDto); 
        
        // 3. Guarda y devuelve
        WorkoutAccessoryCategory updatedCategory = categoryRepository.save(category);
        return WorkoutAccessoryCategoryMapper.toResponseDTO(updatedCategory);
    }
    
    /**
     * Elimina una categoría.
     */
    public void deleteWorkoutAccessoryCategory(Integer id) {
        // Busca la entidad para asegurar su existencia y la elimina (eficiente)
        WorkoutAccessoryCategory category = findWorkoutAccessoryCategoryOrThrow(id);
        categoryRepository.delete(category);
    }

    // -------------------- MÉTODO AUXILIAR DE BÚSQUEDA --------------------

    /**
     * Busca una WorkoutAccessoryCategory por ID o lanza EntityNotFoundException.
     * Útil para los métodos de escritura y para otros servicios que dependan de esta entidad.
     */
    public WorkoutAccessoryCategory findWorkoutAccessoryCategoryOrThrow(Integer categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new EntityNotFoundException("WorkoutAccessory Category not found with id: " + categoryId));
    }
}