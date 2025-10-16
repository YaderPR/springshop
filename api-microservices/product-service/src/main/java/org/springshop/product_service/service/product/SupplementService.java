// Archivo: org.springshop.api.service.product.SupplementService.java (Refactorizado)

package org.springshop.product_service.service.product;

import jakarta.persistence.EntityNotFoundException;
import org.springshop.product_service.dto.product.supplement.SupplementRequest;
import org.springshop.product_service.dto.product.supplement.SupplementResponse;
// CORRECCIÓN: Renombramos el Mapper para mantener consistencia: SupplementMapper
import org.springshop.product_service.mapper.product.SupplementMapper; 
import org.springshop.product_service.model.product.Category;
import org.springshop.product_service.model.product.Supplement;
import org.springshop.product_service.repository.product.CategoryRepository;
import org.springshop.product_service.repository.product.SupplementRepository; 
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class SupplementService {

    private final SupplementRepository supplementRepository;
    private final CategoryRepository categoryRepository;

    public SupplementService(SupplementRepository supplementRepository, CategoryRepository categoryRepository) {
        this.supplementRepository = supplementRepository;
        this.categoryRepository = categoryRepository;
    }

    // -------------------- LECTURA (READ) Y FILTRADO --------------------
    
    /**
     * Obtiene todos los suplementos.
     */
    @Transactional(readOnly = true)
    public List<SupplementResponse> getAllSupplements() { // CONSISTENCIA: Renombramos
        return supplementRepository.findAll().stream()
                .map(SupplementMapper::toResponseDTO) // CONSISTENCIA: Renombramos el método del mapper
                .collect(Collectors.toList());
    }

    /**
     * Obtiene un suplemento por ID.
     */
    @Transactional(readOnly = true)
    public Optional<SupplementResponse> getSupplementById(Integer id) { // CONSISTENCIA: Renombramos
        return supplementRepository.findById(id)
                .map(SupplementMapper::toResponseDTO); // CONSISTENCIA: Renombramos el método del mapper
    }
    
    /**
     * Obtiene suplementos filtrados por la Categoría Genérica.
     * Requiere: List<Supplement> findAllByCategoryId(Integer categoryId); en el repositorio.
     */
    @Transactional(readOnly = true)
    public List<SupplementResponse> getSupplementsByCategoryId(Integer categoryId) {
        // ASUMIMOS que el repositorio implementará findAllByCategoryId
        return supplementRepository.findAllByCategoryId(categoryId).stream()
            .map(SupplementMapper::toResponseDTO)
            .collect(Collectors.toList());
    }

    // -------------------- ESCRITURA (CREATE, UPDATE, DELETE) --------------------

    /**
     * Crea un nuevo suplemento.
     */
    public SupplementResponse createSupplement(SupplementRequest dto) { // CONSISTENCIA: Renombramos
        // Búsqueda de la categoría centralizada
        Category category = findCategoryIfPresentOrThrow(dto.getCategoryId());

        Supplement supplement = SupplementMapper.toEntity(dto, category);
        Supplement saved = supplementRepository.save(supplement);
        
        return SupplementMapper.toResponseDTO(saved);
    }

    /**
     * Actualiza un suplemento existente.
     */
    public SupplementResponse updateSupplement(Integer id, SupplementRequest dto) { // CONSISTENCIA: Renombramos
        // 1. Centralizamos la búsqueda del recurso existente
        Supplement existing = findSupplementOrThrow(id);

        // 2. Centralizamos la búsqueda de las referencias
        Category category = findCategoryIfPresentOrThrow(dto.getCategoryId());

        // 3. Delegamos el mapeo al mapper (usando la convención updateSupplement)
        SupplementMapper.updateSupplement(existing, dto, category);
        
        Supplement updatedSupplement = supplementRepository.save(existing);
        return SupplementMapper.toResponseDTO(updatedSupplement);
    }

    /**
     * Elimina un suplemento.
     */
    public void deleteSupplement(Integer id) { // CONSISTENCIA: Renombramos
        // Optimizamos la eliminación: buscar y eliminar (más eficiente y limpio)
        Supplement supplement = findSupplementOrThrow(id);
        supplementRepository.delete(supplement);
    }
    
    // -------------------- MÉTODOS AUXILIARES Y DE BÚSQUEDA --------------------

    /**
     * Busca un Supplement por ID o lanza EntityNotFoundException.
     */
    public Supplement findSupplementOrThrow(Integer supplementId) {
        return supplementRepository.findById(supplementId)
                .orElseThrow(() -> new EntityNotFoundException("Supplement not found with id: " + supplementId));
    }
    
    /**
     * Busca una Categoría genérica por ID o lanza EntityNotFoundException (si el ID no es nulo).
     */
    private Category findCategoryIfPresentOrThrow(Integer categoryId) {
        if (categoryId == null) {
            return null;
        }
        // Centralizamos la búsqueda para que esté limpia en los métodos CRUD
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new EntityNotFoundException("Category not found with id: " + categoryId));
    }
}