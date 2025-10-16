// Archivo: org.springshop.api.service.product.ApparelService.java (Refactorizado)

package org.springshop.product_service.service.product;

import jakarta.persistence.EntityNotFoundException;
import org.springshop.product_service.dto.product.apparel.ApparelRequest;
import org.springshop.product_service.dto.product.apparel.ApparelResponse;
import org.springshop.product_service.mapper.product.ApparelMapper;
import org.springshop.product_service.model.product.Apparel;
import org.springshop.product_service.model.product.ApparelCategory;
import org.springshop.product_service.model.product.Category;
import org.springshop.product_service.repository.product.ApparelCategoryRepository;
import org.springshop.product_service.repository.product.ApparelRepository;
import org.springshop.product_service.repository.product.CategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class ApparelService {

    private final ApparelRepository apparelRepository;
    private final CategoryRepository categoryRepository;
    private final ApparelCategoryRepository apparelCategoryRepository;
    
    // Asumimos que ProductService tiene findCategoryOrThrow si quisiéramos inyectarlo, 
    // pero mantenemos los repositorios aquí por la naturaleza especializada del servicio.

    public ApparelService(ApparelRepository apparelRepository,
                          CategoryRepository categoryRepository,
                          ApparelCategoryRepository apparelCategoryRepository) {
        this.apparelRepository = apparelRepository;
        this.categoryRepository = categoryRepository;
        this.apparelCategoryRepository = apparelCategoryRepository;
    }

    // -------------------- APPAREL ESPECÍFICO --------------------
    
    /**
     * Crea un nuevo artículo de indumentaria (Apparel).
     */
    public ApparelResponse createApparel(ApparelRequest dto) {
        // 1. Centralizamos la búsqueda de las dos categorías
        Category category = findCategoryIfPresentOrThrow(dto.getCategoryId());
        ApparelCategory apparelCategory = findApparelCategoryIfPresentOrThrow(dto.getApparelCategoryId());

        Apparel apparel = ApparelMapper.toEntity(dto, category, apparelCategory);
        Apparel saved = apparelRepository.save(apparel);
        
        // Usamos la convención de nomenclatura del mapper
        return ApparelMapper.toResponseDTO(saved); 
    }

    /**
     * Obtiene todos los artículos de indumentaria.
     */
    @Transactional(readOnly = true)
    public List<ApparelResponse> getAllApparels() {
        return apparelRepository.findAll().stream()
                .map(ApparelMapper::toResponseDTO) // Usamos la convención
                .collect(Collectors.toList());
    }

    /**
     * Obtiene artículos de indumentaria por la Categoría genérica.
     * @param categoryId ID de la Categoría.
     */
    @Transactional(readOnly = true)
    public List<ApparelResponse> getApparelsByCategoryId(Integer categoryId) {
        // Asumiendo que findAllByCategoryId existe en ApparelRepository
        return apparelRepository.findAllByCategoryId(categoryId).stream()
                .map(ApparelMapper::toResponseDTO) // Usamos la convención
                .collect(Collectors.toList());
    }

    /**
     * Obtiene un artículo de indumentaria por ID.
     */
    @Transactional(readOnly = true)
    public Optional<ApparelResponse> getApparelById(Integer id) {
        return apparelRepository.findById(id)
                .map(ApparelMapper::toResponseDTO); // Usamos la convención
    }

    /**
     * Actualiza un artículo de indumentaria existente.
     */
    public ApparelResponse updateApparel(Integer id, ApparelRequest dto) {
        // 1. Centralizamos la búsqueda del recurso existente
        Apparel existing = findApparelOrThrow(id);

        // 2. Centralizamos la búsqueda de las referencias
        Category category = findCategoryIfPresentOrThrow(dto.getCategoryId());
        ApparelCategory apparelCategory = findApparelCategoryIfPresentOrThrow(dto.getApparelCategoryId());
        
        // 3. Delegamos el mapeo y usamos la convención updateApparel
        ApparelMapper.updateApparel(existing, dto, category, apparelCategory);
        
        Apparel updatedApparel = apparelRepository.save(existing);
        return ApparelMapper.toResponseDTO(updatedApparel);
    }

    /**
     * Elimina un artículo de indumentaria.
     */
    public void deleteApparel(Integer id) {
        // Optimizamos la eliminación: buscar y eliminar (más eficiente y limpio)
        Apparel apparel = findApparelOrThrow(id);
        apparelRepository.delete(apparel);
    }
    
    // -------------------- MÉTODOS AUXILIARES Y DE BÚSQUEDA --------------------

    /**
     * Busca un Apparel por ID o lanza EntityNotFoundException.
     */
    public Apparel findApparelOrThrow(Integer apparelId) {
        return apparelRepository.findById(apparelId)
                .orElseThrow(() -> new EntityNotFoundException("Apparel not found with id: " + apparelId));
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
     * Busca una Categoría específica de Indumentaria por ID o lanza EntityNotFoundException (si el ID no es nulo).
     */
    private ApparelCategory findApparelCategoryIfPresentOrThrow(Integer apparelCategoryId) {
        if (apparelCategoryId == null) {
            return null;
        }
        return apparelCategoryRepository.findById(apparelCategoryId)
                .orElseThrow(() -> new EntityNotFoundException("ApparelCategory not found with id: " + apparelCategoryId));
    }
}