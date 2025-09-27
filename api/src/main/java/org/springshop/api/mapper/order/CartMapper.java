package org.springshop.api.mapper.order;

import org.springshop.api.dto.order.CartItemCreateRequestDto;
import org.springshop.api.dto.order.CartItemResponseDto;
import org.springshop.api.dto.order.CartItemUpdateRequestDto;
import org.springshop.api.dto.order.CartRequestDto;
import org.springshop.api.dto.order.CartResponseDto;
import org.springshop.api.model.order.Cart;
import org.springshop.api.model.order.CartItem;
import org.springshop.api.model.product.Product;
import org.springshop.api.model.user.User;

import java.util.stream.Collectors;

public class CartMapper {

    // -------------------- CART ENTITY/DTO --------------------

    public static Cart toEntity(CartRequestDto dto, User user) {
        if (dto == null) return null;

        Cart cart = new Cart();
        cart.setUser(user);
        // NOTA: Se asume que la fecha de creación se maneja por JPA (@CreationTimestamp)
        return cart;
    }

    public static CartResponseDto toResponseDto(Cart cart) {
        if (cart == null) return null;
        
        CartResponseDto dto = new CartResponseDto();
        dto.setId(cart.getId());
        dto.setUserId(cart.getUser().getId());
        
        // Mapea la colección de ítems
        dto.setItems(cart.getItems() != null
                ? cart.getItems().stream()
                        .map(CartMapper::toResponseDto)
                        .collect(Collectors.toSet())
                : null);
        
        // El TOTAL se calculará en el servicio, no aquí.
        return dto;
    }

    public static void updateCart(Cart existing, User user) {
        if(existing == null) return;
        
        // Se asume que solo se puede cambiar el User (dueño) del carrito
        existing.setUser(user);
        
        // RECOMENDACIÓN: Mover la lógica de fecha a un listener JPA o @UpdateTimestamp
        // existing.setUpdateAt(LocalDateTime.now());
    }

    // -------------------- CART ITEM ENTITY/DTO --------------------

    /**
     * Crea un nuevo CartItem. El precio del producto debe provenir de la entidad Product (Service Layer).
     * @param dto DTO de creación (contiene solo la cantidad y el ID del producto).
     * @param product Entidad Producto (para obtener el precio actual y la referencia).
     * @param cart Entidad Cart (para la referencia).
     * @return Nuevo CartItem.
     */
    public static CartItem toEntity(CartItemCreateRequestDto dto, Product product, Cart cart) {
        if (dto == null) return null;

        CartItem item = new CartItem();
        item.setProduct(product);
        item.setQuantity(dto.getQuantity());
        
        // CRÍTICO: FIJAR EL PRECIO AL CREAR EL ITEM (precio de la entidad Product)
        item.setPrice(product.getPrice()); 
        
        item.setCart(cart);
        return item;
    }
    
    // Eliminado el toEntity(CartItemUpdateRequestDto, Product) que era redundante.

    public static CartItemResponseDto toResponseDto(CartItem item) {
        if (item == null) return null;
        
        CartItemResponseDto dto = new CartItemResponseDto();
        dto.setId(item.getId());
        dto.setProductId(item.getProduct().getId());
        dto.setQuantity(item.getQuantity());
        
        // CRÍTICO: El precio del CartItem es el precio fijado al momento de la adición.
        dto.setPrice(item.getPrice()); 
        
        // Se asume que el DTO de respuesta debe incluir el ID del carrito padre
        dto.setCartId(item.getCart().getId()); 

        return dto;
    }

    /**
     * Actualiza un CartItem existente. Solo se actualiza la cantidad. 
     * El precio NO debería actualizarse a menos que se reajuste intencionalmente.
     * @param existing Ítem del carrito existente.
     * @param dto DTO de actualización (contiene la nueva cantidad).
     * @param product Entidad Producto (necesaria si la referencia de producto cambia, lo cual es inusual).
     */
    public static void updateCartItem(CartItem existing, CartItemUpdateRequestDto dto, Product product) {
        if(existing == null || dto == null) return;
        
        // RECOMENDACIÓN: Solo actualizar la cantidad y potencialmente el producto
        existing.setQuantity(dto.getQuantity());
        existing.setProduct(product); 
        
        // Se elimina la actualización del precio, ya que debe ser fijo
        // Si quieres reajustar el precio, esa lógica DEBE estar en el Servicio.
        
        // Si el DTO no trae el ProductId y el Service no lo busca, el producto
        // no debería ser actualizado.
    }
}