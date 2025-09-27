package org.springshop.api.service.product;

import java.util.List;
import java.util.Optional; // Importación necesaria
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Usamos el de Spring
import org.springshop.api.dto.product.apparel.ApparelCategoryRequestDTO;
import org.springshop.api.dto.product.apparel.ApparelCategoryResponseDTO;
import org.springshop.api.mapper.product.ApparelCategoryMapper;
import org.springshop.api.model.product.ApparelCategory;
import org.springshop.api.repository.product.ApparelCategoryRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
@Transactional
public class ApparelCategoryService {
    
    private final ApparelCategoryRepository apparelCategoryRepository;
    
    public ApparelCategoryService(ApparelCategoryRepository apparelCategoryRepository) {
        this.apparelCategoryRepository = apparelCategoryRepository;
    }

    // -------------------- CRUD DE CATEGORÍAS DE INDUMENTARIA --------------------

    /**
     * Lista todas las categorías de indumentaria.
     */
    @Transactional(readOnly = true)
    public List<ApparelCategoryResponseDTO> getAllApparelCategories() {
        return apparelCategoryRepository.findAll().stream()
                .map(ApparelCategoryMapper::toResponseDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Obtiene una categoría por ID.
     */
    @Transactional(readOnly = true)
    public Optional<ApparelCategoryResponseDTO> getApparelCategoryById(Integer id) {
        // CORRECCIÓN: Devolvemos Optional. La conversión .orElseGet(null) se maneja en el Controller (wrapOrNotFound).
        return apparelCategoryRepository.findById(id)
                .map(ApparelCategoryMapper::toResponseDTO);
    }
    
    /**
     * Crea una nueva categoría.
     */
    public ApparelCategoryResponseDTO createApparelCategory(ApparelCategoryRequestDTO requestDTO) {
        ApparelCategory apparelCategory = ApparelCategoryMapper.toEntity(requestDTO);
        ApparelCategory saved = apparelCategoryRepository.save(apparelCategory);
        return ApparelCategoryMapper.toResponseDTO(saved);
    }
    
    /**
     * Actualiza una categoría existente.
     */
    public ApparelCategoryResponseDTO updateApparelCategory(Integer id, ApparelCategoryRequestDTO requestDto) {
        // 1. Centralizamos la búsqueda
        ApparelCategory apparelCategory = findApparelCategoryOrThrow(id);
        
        // 2. Delegamos la actualización al mapper y usamos la convención updateApparelCategory
        ApparelCategoryMapper.updateApparelCategory(apparelCategory, requestDto); 
        
        // El save() es opcional aquí, pero lo mantenemos por claridad de la persistencia
        ApparelCategory updatedApparelCategory = apparelCategoryRepository.save(apparelCategory);
        return ApparelCategoryMapper.toResponseDTO(updatedApparelCategory);
    }
    
    /**
     * Elimina una categoría.
     */
    public void deleteApparelCategory(Integer id) {
        // Optimizamos la eliminación: buscar la entidad y eliminar (una consulta)
        ApparelCategory category = findApparelCategoryOrThrow(id);
        apparelCategoryRepository.delete(category);
    }

    // -------------------- MÉTODOS AUXILIARES Y DE BÚSQUEDA --------------------

    /**
     * Busca una ApparelCategory por ID o lanza EntityNotFoundException.
     * Es público para que otros servicios (como ApparelService) puedan usarlo.
     */
    public ApparelCategory findApparelCategoryOrThrow(Integer categoryId) {
        return apparelCategoryRepository.findById(categoryId)
                .orElseThrow(() -> new EntityNotFoundException("Apparel Category not found with id: " + categoryId));
    }
}