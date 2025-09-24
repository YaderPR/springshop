package org.springshop.api.service.product;

import jakarta.persistence.EntityNotFoundException;

import org.springshop.api.dto.product.ProductRequestDTO;
import org.springshop.api.dto.product.ProductResponseDTO;
import org.springshop.api.dto.product.apparel.ApparelRequestDTO;
import org.springshop.api.dto.product.apparel.ApparelResponseDTO;
import org.springshop.api.dto.product.supplement.SupplementRequestDTO;
import org.springshop.api.dto.product.supplement.SupplementResponseDTO;
import org.springshop.api.dto.product.workoutaccessory.WorkoutAccessoryRequestDTO;
import org.springshop.api.dto.product.workoutaccessory.WorkoutAccessoryResponseDTO;
import org.springshop.api.mapper.product.ApparelMapper;
import org.springshop.api.mapper.product.ProductMapper;
import org.springshop.api.mapper.product.SuplementMapper;
import org.springshop.api.mapper.product.WorkoutAccessoryMapper;
import org.springshop.api.model.product.*;
import org.springshop.api.repository.product.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ApparelCategoryRepository apparelCategoryRepository;
    private final WorkoutAccessoryCategoryRepository workoutAccessoryCategoryRepository;

    public ProductService(ProductRepository productRepository,
                          CategoryRepository categoryRepository,
                          ApparelCategoryRepository apparelCategoryRepository,
                          WorkoutAccessoryCategoryRepository workoutAccessoryCategoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.apparelCategoryRepository = apparelCategoryRepository;
        this.workoutAccessoryCategoryRepository = workoutAccessoryCategoryRepository;
    }

    // -------------------- PRODUCTO GENÉRICO --------------------
    public ProductResponseDTO createProduct(ProductRequestDTO dto) {
        Category category = null;
        if (dto.getCategoryId() != null) {
            category = categoryRepository.findById(dto.getCategoryId())
                    .orElseThrow(() -> new EntityNotFoundException("Category not found with id " + dto.getCategoryId()));
        }

        Product product = ProductMapper.toEntity(dto, category);
        Product saved = productRepository.save(product);
        return ProductMapper.toDTO(saved);
    }

    public List<ProductResponseDTO> getAllProducts() {
        return productRepository.findAll()
                .stream()
                .map(ProductMapper::toDTO)
                .collect(Collectors.toList());
    }

    public Optional<ProductResponseDTO> getProductById(Integer id) {
        return productRepository.findById(id)
                .map(ProductMapper::toDTO);
    }

    public ProductResponseDTO updateProduct(Integer id, ProductRequestDTO dto) {
        Product existing = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found with id " + id));

        Category category = null;
        if (dto.getCategoryId() != null) {
            category = categoryRepository.findById(dto.getCategoryId())
                    .orElseThrow(() -> new EntityNotFoundException("Category not found with id " + dto.getCategoryId()));
        }
        ProductMapper.updateEntity(existing, dto, category);
        Product updatedProduct = productRepository.save(existing);
        return ProductMapper.toDTO(updatedProduct);
    }

    public void deleteProduct(Integer id) {
        if(!productRepository.existsById(id)) {
            throw new EntityNotFoundException("Product not found with id: " + id);
        }
        productRepository.deleteById(id);
    }

    // -------------------- APPAREL ESPECÍFICO --------------------
    public ApparelResponseDTO createApparel(ApparelRequestDTO dto) {
        Category category = null;
        if (dto.getCategoryId() != null) {
            category = categoryRepository.findById(dto.getCategoryId())
                    .orElseThrow(() -> new EntityNotFoundException("Category not found with id " + dto.getCategoryId()));
        }

        ApparelCategory apparelCategory = null;
        if (dto.getApparelCategoryId() != null) {
            apparelCategory = apparelCategoryRepository.findById(dto.getApparelCategoryId())
                    .orElseThrow(() -> new EntityNotFoundException("ApparelCategory not found with id " + dto.getApparelCategoryId()));
        }

        Apparel apparel = ApparelMapper.toEntity(dto, category, apparelCategory);
        Apparel saved = productRepository.save(apparel);
        return ApparelMapper.toDTO(saved);
    }

    public List<ApparelResponseDTO> getAllApparels() {
        return productRepository.findAll()
                .stream()
                .filter(p -> p instanceof Apparel)
                .map(p -> ApparelMapper.toDTO((Apparel) p))
                .collect(Collectors.toList());
    }

    public Optional<ApparelResponseDTO> getApparelById(Integer id) {
        return productRepository.findById(id)
                .filter(p -> p instanceof Apparel)
                .map(p -> ApparelMapper.toDTO((Apparel) p));
    }

    public ApparelResponseDTO updateApparel(Integer id, ApparelRequestDTO dto) {
        Product existing = productRepository.findById(id)
                .filter(p -> p instanceof Apparel)
                .orElseThrow(() -> new EntityNotFoundException("Apparel not found with id " + id));

        Category category = null;
        if (dto.getCategoryId() != null) {
            category = categoryRepository.findById(dto.getCategoryId())
                    .orElseThrow(() -> new EntityNotFoundException("Category not found with id " + dto.getCategoryId()));
        }

        ApparelCategory apparelCategory = null;
        if (dto.getApparelCategoryId() != null) {
            apparelCategory = apparelCategoryRepository.findById(dto.getApparelCategoryId())
                    .orElseThrow(() -> new EntityNotFoundException("ApparelCategory not found with id " + dto.getApparelCategoryId()));
        }
        ApparelMapper.updateEntity((Apparel) existing, dto, category, apparelCategory);
        Apparel updatedApparel = (Apparel) productRepository.save(existing);
        return ApparelMapper.toDTO(updatedApparel);
    }

    public void deleteApparel(Integer id) {
        if(!productRepository.existsById(id)) {
            throw new EntityNotFoundException("Apparel not found with id: " + id);
        }
        productRepository.deleteById(id);
    }

    // -------------------- WORKOUT ACCESSORY ESPECÍFICO --------------------
    public WorkoutAccessoryResponseDTO createWorkoutAccessory(WorkoutAccessoryRequestDTO dto) {
        Category category = null;
        if (dto.getCategoryId() != null) {
            category = categoryRepository.findById(dto.getCategoryId())
                    .orElseThrow(() -> new EntityNotFoundException("Category not found with id " + dto.getCategoryId()));
        }

        WorkoutAccessoryCategory accessoryCategory = null;
        if (dto.getWorkoutAccessoryCategoryId() != null) {
            accessoryCategory = workoutAccessoryCategoryRepository.findById(dto.getWorkoutAccessoryCategoryId())
                    .orElseThrow(() -> new EntityNotFoundException("WorkoutAccessoryCategory not found with id " + dto.getWorkoutAccessoryCategoryId()));
        }

        WorkoutAccessory accessory = WorkoutAccessoryMapper.toEntity(dto, category, accessoryCategory);
        WorkoutAccessory saved = productRepository.save(accessory);
        return WorkoutAccessoryMapper.toDTO(saved);
    }

    public List<WorkoutAccessoryResponseDTO> getAllWorkoutAccessories() {
        return productRepository.findAll()
                .stream()
                .filter(p -> p instanceof WorkoutAccessory)
                .map(p -> WorkoutAccessoryMapper.toDTO((WorkoutAccessory) p))
                .collect(Collectors.toList());
    }

    public Optional<WorkoutAccessoryResponseDTO> getWorkoutAccessoryById(Integer id) {
        return productRepository.findById(id)
                .filter(p -> p instanceof WorkoutAccessory)
                .map(p -> WorkoutAccessoryMapper.toDTO((WorkoutAccessory) p));
    }

    public WorkoutAccessoryResponseDTO updateWorkoutAccessory(Integer id, WorkoutAccessoryRequestDTO dto) {
        Product existing = productRepository.findById(id)
                .filter(p -> p instanceof WorkoutAccessory)
                .orElseThrow(() -> new EntityNotFoundException("WorkoutAccessory not found with id " + id));

        Category category = null;
        if (dto.getCategoryId() != null) {
            category = categoryRepository.findById(dto.getCategoryId())
                    .orElseThrow(() -> new EntityNotFoundException("Category not found with id " + dto.getCategoryId()));
        }

        WorkoutAccessoryCategory accessoryCategory = null;
        if (dto.getWorkoutAccessoryCategoryId() != null) {
            accessoryCategory = workoutAccessoryCategoryRepository.findById(dto.getWorkoutAccessoryCategoryId())
                    .orElseThrow(() -> new EntityNotFoundException("WorkoutAccessoryCategory not found with id " + dto.getWorkoutAccessoryCategoryId()));
        }

        WorkoutAccessoryMapper.updateEntity((WorkoutAccessory) existing, dto, category, accessoryCategory);
        WorkoutAccessory updatedWorkoutAccessory = (WorkoutAccessory) productRepository.save(existing); 
        return WorkoutAccessoryMapper.toDTO(updatedWorkoutAccessory);
    }

    public void deleteWorkoutAccessory(Integer id) {
        if(!productRepository.existsById(id)) {
            throw new EntityNotFoundException("WorkoutAccessory not found with id: " + id);
        }
        productRepository.deleteById(id);
    }

    // -------------------- SUPPLEMENT ESPECÍFICO --------------------
    public SupplementResponseDTO createSuplement(SupplementRequestDTO dto) {
        Category category = null;
        if (dto.getCategoryId() != null) {
            category = categoryRepository.findById(dto.getCategoryId())
                    .orElseThrow(() -> new EntityNotFoundException("Category not found with id " + dto.getCategoryId()));
        }

        Supplement suplement = SuplementMapper.toEntity(dto, category);
        Supplement saved = productRepository.save(suplement);
        return SuplementMapper.toDTO(saved);
    }

    public List<SupplementResponseDTO> getAllSuplements() {
        return productRepository.findAll()
                .stream()
                .filter(p -> p instanceof Supplement)
                .map(p -> SuplementMapper.toDTO((Supplement) p))
                .collect(Collectors.toList());
    }

    public Optional<SupplementResponseDTO> getSuplementById(Integer id) {
        return productRepository.findById(id)
                .filter(p -> p instanceof Supplement)
                .map(p -> SuplementMapper.toDTO((Supplement) p));
    }

    public SupplementResponseDTO updateSuplementById(Integer id, SupplementRequestDTO dto) {
        Product existing = productRepository.findById(id)
                .filter(p -> p instanceof Supplement)
                .orElseThrow(() -> new EntityNotFoundException("Suplement not found with id " + id));

        Category category = null;
        if (dto.getCategoryId() != null) {
            category = categoryRepository.findById(dto.getCategoryId())
                    .orElseThrow(() -> new EntityNotFoundException("Category not found with id " + dto.getCategoryId()));
        }

        SuplementMapper.updateEntity((Supplement) existing, dto, category);
        Supplement updatedSupplement = (Supplement) productRepository.save(existing);
        return SuplementMapper.toDTO(updatedSupplement);
    }

    public void deleteSuplementById(Integer id) {
        if(!productRepository.existsById(id)) {
            throw new EntityNotFoundException("Supplement not found with id: " + id);
        }
        productRepository.deleteById(id);
    }
}
