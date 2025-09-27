// Archivo: org.springshop.api.service.order.CartItemService.java

package org.springshop.api.service.order;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springshop.api.dto.order.CartItemCreateRequestDto;
import org.springshop.api.dto.order.CartItemResponseDto;
import org.springshop.api.dto.order.CartItemUpdateRequestDto;
import org.springshop.api.mapper.order.CartMapper;
import org.springshop.api.model.order.Cart;
import org.springshop.api.model.order.CartItem;
import org.springshop.api.model.product.Product;
import org.springshop.api.repository.order.CartItemRepository;
import org.springshop.api.repository.product.ProductRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
@Transactional
public class CartItemService {

    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final CartService cartService;
    
    // Asumimos que has agregado este método en CartItemRepository:
    // Optional<CartItem> findByCartIdAndProductId(Integer cartId, Integer productId);

    public CartItemService(CartItemRepository cartItemRepository, ProductRepository productRepository, CartService cartService) {
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
        this.cartService = cartService;
    }
    
    // -------------------- CREACIÓN/ACTUALIZACIÓN INTELIGENTE (UPSERT) --------------------
    
    /**
     * Agrega un nuevo ítem o actualiza la cantidad si el producto ya existe en el carrito.
     * @param cartId El ID del carrito.
     * @param itemDto DTO con la cantidad a agregar y el ID del producto.
     * @return El ítem del carrito (CartItem) actualizado/creado.
     */
    public CartItemResponseDto addItemOrUpdateQuantity(Integer cartId, CartItemCreateRequestDto itemDto) {
        
        // 1. Validar existencias
        Cart cart = cartService.findCartOrThrow(cartId); 
        Product product = findProductOrThrow(itemDto.getProductId());
        
        // 2. Lógica de UPSERT: Buscar ítem existente
        Optional<CartItem> existingItemOptional = cartItemRepository.findByCartIdAndProductId(
            cartId, 
            itemDto.getProductId()
        );
        
        CartItem itemToSave;
        
        if (existingItemOptional.isPresent()) {
            // Caso A: UPDATE (El ítem ya existe)
            itemToSave = existingItemOptional.get();
            // Sumar la cantidad solicitada a la cantidad existente
            int newQuantity = itemToSave.getQuantity() + itemDto.getQuantity();
            itemToSave.setQuantity(newQuantity); 
            
        } else {
            // Caso B: INSERT (Crear nuevo ítem)
            // Usa el Mapper modificado que toma el precio del objeto Product
            itemToSave = CartMapper.toEntity(itemDto, product, cart);
        }
        
        // 3. Guardar y mapear
        return CartMapper.toResponseDto(cartItemRepository.save(itemToSave));
    }

    // -------------------- OBTENCIÓN --------------------

    public List<CartItemResponseDto> getCartItemsByCartId(Integer cartId) {
        // Aseguramos que el carrito existe (doble check), luego consultamos los ítems.
        cartService.findCartOrThrow(cartId); 
        
        // Asume que tienes: List<CartItem> findAllByCartId(Integer cartId);
        List<CartItem> cartItems = cartItemRepository.findAllByCartId(cartId);
        
        return cartItems.stream().map(CartMapper::toResponseDto).collect(Collectors.toList());
    }

    // -------------------- ACTUALIZACIÓN (Reemplazo Total de Cantidad) --------------------

    /**
     * Actualiza un ítem existente en el carrito con una nueva cantidad.
     * @param cartId ID del carrito (para validación de pertenencia).
     * @param itemId ID del ítem a modificar.
     * @param itemDto DTO de actualización (contiene la nueva cantidad).
     * @return El CartItem actualizado.
     */
    public CartItemResponseDto updateCartItem(Integer cartId, Integer itemId, CartItemUpdateRequestDto itemDto) {
        CartItem item = findCartItemOrThrow(itemId);
        
        // 1. Validar Pertenencia (CRÍTICO)
        if (!item.getCart().getId().equals(cartId)) {
            throw new IllegalArgumentException("Item with id " + itemId + " does not belong to cart with id " + cartId);
        }
        
        // 2. Obtener el Producto (Se hace aunque no cambie, para mantener la firma del mapper)
        // NOTA: Si el DTO no incluye productId, este paso debería usar item.getProduct().getId()
        Product product = findProductOrThrow(itemDto.getProductId()); 

        // 3. Mapear y Guardar (Usando el nombre de método corregido: updateCartItem)
        CartMapper.updateCartItem(item, itemDto, product);
        
        CartItem updatedItem = cartItemRepository.save(item); 
        return CartMapper.toResponseDto(updatedItem);
    }
    
    // -------------------- ELIMINACIÓN --------------------

    public void deleteCartItem(Integer cartId, Integer itemId) {
        CartItem item = findCartItemOrThrow(itemId);

        // 1. Validar Pertenencia (CRÍTICO)
        if (!item.getCart().getId().equals(cartId)) {
            throw new IllegalArgumentException("Item with id " + itemId + " does not belong to cart with id " + cartId);
        }

        cartItemRepository.delete(item);
    }

    // -------------------- MÉTODOS AUXILIARES --------------------

    private Product findProductOrThrow(Integer productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + productId));
    }

    private CartItem findCartItemOrThrow(Integer itemId) {
        return cartItemRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("CartItem not found with id: " + itemId));
    }
}