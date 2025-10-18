package org.springshop.cart_service.service.cart;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springshop.cart_service.controller.exception.StockException;
import org.springshop.cart_service.dto.cart.CartItemCreateRequestDto;
import org.springshop.cart_service.dto.cart.CartItemResponseDto;
import org.springshop.cart_service.dto.cart.CartItemUpdateRequestDto;

import org.springshop.cart_service.mapper.cart.CartMapper;
import org.springshop.cart_service.model.cart.Cart;
import org.springshop.cart_service.model.cart.CartItem;
import org.springshop.cart_service.model.product.Product;
import org.springshop.cart_service.repository.cart.CartItemRepository;
import org.springshop.cart_service.client.ProductClient;

import jakarta.persistence.EntityNotFoundException;

@Service
@Transactional
public class CartItemService {

    private final CartItemRepository cartItemRepository;
    private final ProductClient productClient;
    private final CartService cartService;

    
    public CartItemService(CartItemRepository cartItemRepository, ProductClient productClient, 
                           CartService cartService) {
        this.cartItemRepository = cartItemRepository;
        this.productClient = productClient;
        this.cartService = cartService;
    }
    
    public CartItemResponseDto addItemOrUpdateQuantity(Integer cartId, CartItemCreateRequestDto itemDto) {
        
        Cart cart = cartService.findCartOrThrow(cartId); 
        Product product = findProductOrThrow(itemDto.getProductId());
        final int quantityToAdd = itemDto.getQuantity();
        
        // 1. Lógica de UPSERT: Buscar ítem existente
        Optional<CartItem> existingItemOptional = cartItemRepository.findByCartIdAndProductId(
            cartId, 
            itemDto.getProductId()
        );
        
        CartItem itemToSave;
        
        if (existingItemOptional.isPresent()) {
            // Caso A: UPDATE (El ítem ya existe)
            itemToSave = existingItemOptional.get();
            int newQuantity = itemToSave.getQuantity() + quantityToAdd;
            
            // VALIDACIÓN 1: Stock máximo (si el carrito supera el stock actual)
            if (newQuantity > product.getStock()) {
                 throw new StockException(
                    String.format("La cantidad solicitada (%d) supera el stock disponible (%d) para el producto %s.", 
                    newQuantity, product.getStock(), product.getName())
                );
            }
            itemToSave.setQuantity(newQuantity); 
            
        } else {
            // Caso B: INSERT (Crear nuevo ítem)
            // VALIDACIÓN 2: Stock máximo para la cantidad inicial
            if (quantityToAdd > product.getStock()) {
                throw new StockException(
                    String.format("La cantidad solicitada (%d) supera el stock disponible (%d) para el producto %s.", 
                    quantityToAdd, product.getStock(), product.getName())
                );
            }
            itemToSave = CartMapper.toEntity(itemDto, product.getId(), product.getPrice(), cart);
        }
        
        // 2. Guardar y Recalcular
        itemToSave = cartItemRepository.save(itemToSave);
        RecalculateCartTotal(cart); 
        
        return CartMapper.toResponseDto(itemToSave);
    }

    // -------------------- OBTENCIÓN --------------------

    public List<CartItemResponseDto> getCartItemsByCartId(Integer cartId) {
        cartService.findCartOrThrow(cartId); 
        List<CartItem> cartItems = cartItemRepository.findAllByCartId(cartId);
        return cartItems.stream().map(CartMapper::toResponseDto).collect(Collectors.toList());
    }

    // -------------------- ACTUALIZACIÓN (Reemplazo Total de Cantidad) --------------------

    public CartItemResponseDto updateCartItem(Integer cartId, Integer itemId, CartItemUpdateRequestDto itemDto) {
        CartItem item = findCartItemOrThrow(itemId);
        Cart cart = cartService.findCartOrThrow(cartId);
        
        if (!item.getCart().getId().equals(cartId)) {
            throw new IllegalArgumentException("Item with id " + itemId + " does not belong to cart with id " + cartId);
        }
        
        Product product = findProductOrThrow(itemDto.getProductId()); 
        
        // 1. VALIDACIÓN DE STOCK para la nueva cantidad total
        if (itemDto.getQuantity() > product.getStock()) {
             throw new StockException(
                String.format("La cantidad solicitada (%d) supera el stock disponible (%d) para el producto %s.", 
                itemDto.getQuantity(), product.getStock(), product.getName())
            );
        }
        
        // 2. Mapear y Guardar
        CartMapper.updateCartItem(item, itemDto, product.getId());
        
        CartItem updatedItem = cartItemRepository.save(item);
        
        // 3. Recalcular Total
        RecalculateCartTotal(cart);
        
        return CartMapper.toResponseDto(updatedItem);
    }
    
    // -------------------- ELIMINACIÓN --------------------

    public void deleteCartItem(Integer cartId, Integer itemId) {
        CartItem item = findCartItemOrThrow(itemId);
        Cart cart = cartService.findCartOrThrow(cartId);

        if (!item.getCart().getId().equals(cartId)) {
            throw new IllegalArgumentException("Item with id " + itemId + " does not belong to cart with id " + cartId);
        }

        cartItemRepository.delete(item);
        
        // Recalcular Total después de eliminar
        RecalculateCartTotal(cart);
    }

    // -------------------- MÉTODOS AUXILIARES --------------------

    private Product findProductOrThrow(Integer productId) {
        return productClient.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + productId));
    }

    private CartItem findCartItemOrThrow(Integer itemId) {
        return cartItemRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("CartItem not found with id: " + itemId));
    }
    
    /**
     * Calcula el total del carrito y actualiza el campo totalAmount de la entidad Cart.
     */
    private void RecalculateCartTotal(Cart cart) {
        // Asumimos que tienes un método para calcular el total en CartService
        double newTotal = cartService.calculateCartTotals(cart.getId());
        cart.setTotalAmount(newTotal);
        // El @Transactional asegura que se persista.
    }
}