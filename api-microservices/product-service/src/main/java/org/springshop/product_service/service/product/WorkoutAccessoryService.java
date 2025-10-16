// Archivo: org.springshop.api.service.product.WorkoutAccessoryService.java (Actualizado)

package org.springshop.product_service.service.product;

import jakarta.persistence.EntityNotFoundException;
import org.springshop.product_service.dto.product.workoutaccessory.WorkoutAccessoryRequest;
import org.springshop.product_service.dto.product.workoutaccessory.WorkoutAccessoryResponse;
import org.springshop.product_service.mapper.product.WorkoutAccessoryMapper;
import org.springshop.product_service.model.product.Category;
import org.springshop.product_service.model.product.WorkoutAccessory;
import org.springshop.product_service.model.product.WorkoutAccessoryCategory;
import org.springshop.product_service.repository.product.CategoryRepository;
import org.springshop.product_service.repository.product.WorkoutAccessoryCategoryRepository;
import org.springshop.product_service.repository.product.WorkoutAccessoryRepository; 
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class WorkoutAccessoryService {

    private final WorkoutAccessoryRepository accessoryRepository;
    private final CategoryRepository categoryRepository;
    private final WorkoutAccessoryCategoryRepository accessoryCategoryRepository;

    public WorkoutAccessoryService(WorkoutAccessoryRepository accessoryRepository,
                                   CategoryRepository categoryRepository,
                                   WorkoutAccessoryCategoryRepository accessoryCategoryRepository) {
        this.accessoryRepository = accessoryRepository;
        this.categoryRepository = categoryRepository;
        this.accessoryCategoryRepository = accessoryCategoryRepository;
    }

    // -------------------- WORKOUT ACCESSORY ESPECÍFICO --------------------
    
    /**
     * Crea un nuevo accesorio de entrenamiento.
     */
    public WorkoutAccessoryResponse createWorkoutAccessory(WorkoutAccessoryRequest dto) {
        Category category = findCategoryIfPresentOrThrow(dto.getCategoryId());
        WorkoutAccessoryCategory accessoryCategory = findAccessoryCategoryIfPresentOrThrow(dto.getWorkoutAccessoryCategoryId());

        WorkoutAccessory accessory = WorkoutAccessoryMapper.toEntity(dto, category, accessoryCategory);
        WorkoutAccessory saved = accessoryRepository.save(accessory);
        
        return WorkoutAccessoryMapper.toResponseDTO(saved);
    }

    // -------------------- LECTURA Y FILTRADO (AÑADIDO) --------------------

    /**
     * Obtiene todos los accesorios de entrenamiento.
     */
    @Transactional(readOnly = true)
    public List<WorkoutAccessoryResponse> getAllWorkoutAccessories() {
        return accessoryRepository.findAll().stream()
                .map(WorkoutAccessoryMapper::toResponseDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Obtiene un accesorio de entrenamiento por ID.
     */
    @Transactional(readOnly = true)
    public Optional<WorkoutAccessoryResponse> getWorkoutAccessoryById(Integer id) {
        return accessoryRepository.findById(id)
                .map(WorkoutAccessoryMapper::toResponseDTO);
    }
    
    /**
     * Obtiene accesorios filtrados por la Categoría Genérica (para el controlador principal).
     * Requiere: List<WorkoutAccessory> findAllByCategoryId(Integer categoryId); en el repositorio.
     */
    @Transactional(readOnly = true)
    public List<WorkoutAccessoryResponse> getWorkoutAccessoriesByCategoryId(Integer categoryId) {
        return accessoryRepository.findAllByCategoryId(categoryId).stream()
            .map(WorkoutAccessoryMapper::toResponseDTO)
            .collect(Collectors.toList());
    }

    /**
     * Obtiene accesorios filtrados por la Categoría de Accesorio Específica (para el controlador de categorías).
     * Requiere: List<WorkoutAccessory> findAllByWorkoutAccessoryCategoryId(Integer categoryId); en el repositorio.
     */
    @Transactional(readOnly = true)
    public List<WorkoutAccessoryResponse> getWorkoutAccessoriesByWorkoutCategoryId(Integer categoryId) {
        return accessoryRepository.findAllByWorkoutAccessoryCategoryId(categoryId).stream()
            .map(WorkoutAccessoryMapper::toResponseDTO)
            .collect(Collectors.toList());
    }

    // -------------------- ESCRITURA (UPDATE, DELETE) --------------------
    
    /**
     * Actualiza un accesorio de entrenamiento existente.
     */
    public WorkoutAccessoryResponse updateWorkoutAccessory(Integer id, WorkoutAccessoryRequest dto) {
        WorkoutAccessory existing = findWorkoutAccessoryOrThrow(id);
        Category category = findCategoryIfPresentOrThrow(dto.getCategoryId());
        WorkoutAccessoryCategory accessoryCategory = findAccessoryCategoryIfPresentOrThrow(dto.getWorkoutAccessoryCategoryId());

        WorkoutAccessoryMapper.updateWorkoutAccessory(existing, dto, category, accessoryCategory);
        
        WorkoutAccessory updatedWorkoutAccessory = accessoryRepository.save(existing);
        return WorkoutAccessoryMapper.toResponseDTO(updatedWorkoutAccessory);
    }

    /**
     * Elimina un accesorio de entrenamiento.
     */
    public void deleteWorkoutAccessory(Integer id) {
        WorkoutAccessory accessory = findWorkoutAccessoryOrThrow(id);
        accessoryRepository.delete(accessory);
    }
    
    // -------------------- MÉTODOS AUXILIARES Y DE BÚSQUEDA (SIN CAMBIOS) --------------------

    /**
     * Busca un WorkoutAccessory por ID o lanza EntityNotFoundException.
     */
    public WorkoutAccessory findWorkoutAccessoryOrThrow(Integer accessoryId) {
        return accessoryRepository.findById(accessoryId)
                .orElseThrow(() -> new EntityNotFoundException("WorkoutAccessory not found with id: " + accessoryId));
    }
    
    /**
     * Busca una Categoría genérica por ID o lanza EntityNotFoundException (si el ID no es nulo).
     */
    private Category findCategoryIfPresentOrThrow(Integer categoryId) {
        if (categoryId == null) {
            return null;
        }
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new EntityNotFoundException("Category not found with id: " + categoryId));
    }
    
    /**
     * Busca una Categoría específica de Accesorio por ID o lanza EntityNotFoundException (si el ID no es nulo).
     */
    private WorkoutAccessoryCategory findAccessoryCategoryIfPresentOrThrow(Integer accessoryCategoryId) {
        if (accessoryCategoryId == null) {
            return null;
        }
        return accessoryCategoryRepository.findById(accessoryCategoryId)
                .orElseThrow(() -> new EntityNotFoundException("WorkoutAccessoryCategory not found with id: " + accessoryCategoryId));
    }
}