package org.springshop.api.service.product;

import org.springshop.api.dto.product.ProductRequestDTO;
import org.springshop.api.dto.product.ProductResponseDTO;
import org.springshop.api.dto.product.apparel.ApparelRequestDTO;
import org.springshop.api.dto.product.apparel.ApparelResponseDTO;
import org.springshop.api.dto.product.suplement.SuplementRequestDTO;
import org.springshop.api.dto.product.suplement.SuplementResponseDTO;
import org.springshop.api.dto.product.workoutaccessory.WorkoutAccessoryRequestDTO;
import org.springshop.api.dto.product.workoutaccessory.WorkoutAccessoryResponseDTO;
import org.springshop.api.mapper.ApparelMapper;
import org.springshop.api.mapper.ProductMapper;
import org.springshop.api.mapper.SuplementMapper;
import org.springshop.api.mapper.WorkoutAccessoryMapper;
import org.springshop.api.model.product.Apparel;
import org.springshop.api.model.product.ApparelCategory;
import org.springshop.api.model.product.Category;
import org.springshop.api.model.product.Product;
import org.springshop.api.model.product.Suplement;
import org.springshop.api.model.product.WorkoutAccessory;
import org.springshop.api.model.product.WorkoutAccessoryCategory;
import org.springshop.api.repository.product.ApparelCategoryRepository;
import org.springshop.api.repository.product.CategoryRepository;
import org.springshop.api.repository.product.ProductRepository;
import org.springshop.api.repository.product.WorkoutAccessoryCategoryRepository;
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
        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));

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
                .orElseThrow(() -> new RuntimeException("Product not found"));

        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        ProductMapper.updateEntity(existing, dto, category);
        return ProductMapper.toDTO(existing);
    }

    public void deleteProduct(Integer id) {
        productRepository.deleteById(id);
    }

    // -------------------- APPAREL ESPECÍFICO --------------------
    public ApparelResponseDTO createApparel(ApparelRequestDTO dto) {
        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        ApparelCategory apparelCategory = null;
        if (dto.getApparelCategoryId() != null) {
            apparelCategory = apparelCategoryRepository.findById(dto.getApparelCategoryId())
                    .orElseThrow(() -> new RuntimeException("ApparelCategory not found"));
        }

        Apparel apparel = ApparelMapper.toEntity(dto, category, apparelCategory);
        Apparel saved = productRepository.save(apparel); // Hibernate maneja TPT
        return ApparelMapper.toDTO(saved);
    }

    public List<ApparelResponseDTO> getAllApparels() {
        return productRepository.findAll()
                .stream()
                .filter(p -> p instanceof Apparel)
                .map(p -> ApparelMapper.toDTO((Apparel)p))
                .collect(Collectors.toList());
    }

    public Optional<ApparelResponseDTO> getApparelById(Integer id) {
        return productRepository.findById(id)
                .filter(p -> p instanceof Apparel)
                .map(p -> ApparelMapper.toDTO((Apparel)p));
    }

    public ApparelResponseDTO updateApparel(Integer id, ApparelRequestDTO dto) {
        Product existing = productRepository.findById(id)
                .filter(p -> p instanceof Apparel)
                .orElseThrow(() -> new RuntimeException("Apparel not found"));

        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        ApparelCategory apparelCategory = null;
        if (dto.getApparelCategoryId() != null) {
            apparelCategory = apparelCategoryRepository.findById(dto.getApparelCategoryId())
                    .orElseThrow(() -> new RuntimeException("ApparelCategory not found"));
        }

        ApparelMapper.updateEntity((Apparel) existing, dto, category, apparelCategory);
        return ApparelMapper.toDTO((Apparel) existing);
    }

    public void deleteApparel(Integer id) {
        productRepository.deleteById(id);
    }

    // -------------------- WORKOUT ACCESSORY ESPECÍFICO --------------------
    public WorkoutAccessoryResponseDTO createWorkoutAccessory(WorkoutAccessoryRequestDTO dto) {
        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        WorkoutAccessoryCategory accessoryCategory = null;
        if (dto.getWorkoutAccessoryCategoryId() != null) {
            accessoryCategory = workoutAccessoryCategoryRepository.findById(dto.getWorkoutAccessoryCategoryId())
                    .orElseThrow(() -> new RuntimeException("WorkoutAccessoryCategory not found"));
        }

        WorkoutAccessory accessory = WorkoutAccessoryMapper.toEntity(dto, category, accessoryCategory);
        WorkoutAccessory saved = productRepository.save(accessory);
        return WorkoutAccessoryMapper.toDTO(saved);
    }

    public List<WorkoutAccessoryResponseDTO> getAllWorkoutAccessories() {
        return productRepository.findAll()
                .stream()
                .filter(p -> p instanceof WorkoutAccessory)
                .map(p -> WorkoutAccessoryMapper.toDTO((WorkoutAccessory)p))
                .collect(Collectors.toList());
    }

    public Optional<WorkoutAccessoryResponseDTO> getWorkoutAccessoryById(Integer id) {
        return productRepository.findById(id)
                .filter(p -> p instanceof WorkoutAccessory)
                .map(p -> WorkoutAccessoryMapper.toDTO((WorkoutAccessory)p));
    }

    public WorkoutAccessoryResponseDTO updateWorkoutAccessory(Integer id, WorkoutAccessoryRequestDTO dto) {
        Product existing = productRepository.findById(id)
                .filter(p -> p instanceof WorkoutAccessory)
                .orElseThrow(() -> new RuntimeException("WorkoutAccessory not found"));

        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        WorkoutAccessoryCategory accessoryCategory = null;
        if (dto.getWorkoutAccessoryCategoryId() != null) {
            accessoryCategory = workoutAccessoryCategoryRepository.findById(dto.getWorkoutAccessoryCategoryId())
                    .orElseThrow(() -> new RuntimeException("WorkoutAccessoryCategory not found"));
        }

        WorkoutAccessoryMapper.updateEntity((WorkoutAccessory) existing, dto, category, accessoryCategory);
        return WorkoutAccessoryMapper.toDTO((WorkoutAccessory) existing);
    }

    public void deleteWorkoutAccessory(Integer id) {
        productRepository.deleteById(id);
    }

    // -------------------- SUPPLEMENT ESPECÍFICO --------------------
    public SuplementResponseDTO createSuplement(SuplementRequestDTO dto) {
        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        Suplement suplement = SuplementMapper.toEntity(dto, category);
        Suplement saved = productRepository.save(suplement);
        return SuplementMapper.toDTO(saved);
    }

    public List<SuplementResponseDTO> getAllSuplements() {
        return productRepository.findAll()
                .stream()
                .filter(p -> p instanceof Suplement)
                .map(p -> SuplementMapper.toDTO((Suplement)p))
                .collect(Collectors.toList());
    }

    public Optional<SuplementResponseDTO> getSuplementById(Integer id) {
        return productRepository.findById(id)
                .filter(p -> p instanceof Suplement)
                .map(p -> SuplementMapper.toDTO((Suplement)p));
    }

    public SuplementResponseDTO updateSuplement(Integer id, SuplementRequestDTO dto) {
        Product existing = productRepository.findById(id)
                .filter(p -> p instanceof Suplement)
                .orElseThrow(() -> new RuntimeException("Suplement not found"));

        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        SuplementMapper.updateEntity((Suplement) existing, dto, category);
        return SuplementMapper.toDTO((Suplement) existing);
    }

    public void deleteSuplement(Integer id) {
        productRepository.deleteById(id);
    }
}

