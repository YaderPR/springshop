// Archivo: org.springshop.api.service.product.ProductService.java (Refactorizado)

package org.springshop.api.service.product;

import jakarta.persistence.EntityNotFoundException;

import org.springshop.api.controller.exception.StockException;
import org.springshop.api.dto.product.ProductRequestDTO;
import org.springshop.api.dto.product.ProductResponseDTO;
import org.springshop.api.mapper.product.ProductMapper;
import org.springshop.api.model.product.Category;
import org.springshop.api.model.product.Product;
import org.springshop.api.repository.product.CategoryRepository;
import org.springshop.api.repository.product.ProductRepository;
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

    public ProductService(ProductRepository productRepository, CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    // -------------------- CRUD DE PRODUCTO GENÉRICO --------------------
    
    /**
     * Crea un nuevo producto genérico.
     */
    public ProductResponseDTO createProduct(ProductRequestDTO dto) {
        // Centraliza la búsqueda de la categoría
        Category category = findCategoryIfPresentOrThrow(dto.getCategoryId());
        
        Product product = ProductMapper.toEntity(dto, category);
        Product saved = productRepository.save(product);
        
        // Uso de la nomenclatura estándar 'toResponseDto' si el mapper fue ajustado, 
        // pero mantenemos 'toDTO' si es la convención en este dominio.
        return ProductMapper.toResponseDto(saved); 
    }

    /**
     * Obtiene todos los productos.
     */
    @Transactional(readOnly = true)
    public List<ProductResponseDTO> getAllProducts() {
        return productRepository.findAll().stream()
                .map(ProductMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene un producto por su ID (devuelve Optional para manejo RESTful).
     */
    @Transactional(readOnly = true)
    public Optional<ProductResponseDTO> getProductById(Integer id) {
        return productRepository.findById(id)
                .map(ProductMapper::toResponseDto);
    }

    /**
     * Actualiza un producto existente.
     */
    public ProductResponseDTO updateProduct(Integer id, ProductRequestDTO dto) {
        // 1. Centralizamos la búsqueda del producto existente
        Product existing = findProductOrThrow(id);

        // 2. Centralizamos la búsqueda de la categoría
        Category category = findCategoryIfPresentOrThrow(dto.getCategoryId());

        ProductMapper.updateProduct(existing, dto, category);
        
        // Con @Transactional, basta con que el objeto 'existing' sea modificado, 
        // pero llamar a save() lo hace explícito.
        Product updatedProduct = productRepository.save(existing); 
        return ProductMapper.toResponseDto(updatedProduct);
    }

    /**
     * Elimina un producto.
     */
    public void deleteProduct(Integer id) {
        // Optimizamos la eliminación: buscar y eliminar es más limpio y
        // previene una doble consulta (existsById + deleteById).
        Product product = findProductOrThrow(id);
        productRepository.delete(product);
    }
    
    // -------------------- MÉTODOS AUXILIARES Y DE BÚSQUEDA --------------------

    /**
     * Busca un Producto por ID o lanza EntityNotFoundException.
     */
    public Product findProductOrThrow(Integer productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + productId));
    }
    
    /**
     * Busca una Categoría por ID (si está presente) o lanza EntityNotFoundException.
     * Retorna null si el categoryId es null.
     */
    private Category findCategoryIfPresentOrThrow(Integer categoryId) {
        if (categoryId == null) {
            return null;
        }
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new EntityNotFoundException("Category not found with id: " + categoryId));
    }
    public ProductResponseDTO updateStock(Integer productId, Integer quantityChange) {
        if (quantityChange == 0) {
            return getProductById(productId)
                    .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + productId));
        }
        
        // 1. Obtener y bloquear la entidad (gracias a @Transactional)
        Product product = findProductOrThrow(productId);
        
        int currentStock = product.getStock();
        int newStock = currentStock + quantityChange;

        // 2. VALIDACIÓN DEL RANGO (Stock no puede ser negativo)
        if (newStock < 0) {
            throw new StockException(
                String.format("No se puede reducir el stock de %s. Stock actual: %d, Reducción solicitada: %d.",
                    product.getName(), currentStock, Math.abs(quantityChange))
            );
        }
        
        // 3. Aplicar el cambio
        product.setStock(newStock);
        
        // No es estrictamente necesario llamar a save() dentro de @Transactional si se usa findProductOrThrow,
        // pero lo hacemos para ser explícitos.
        Product updated = productRepository.save(product);
        
        return ProductMapper.toResponseDto(updated);
    }
}