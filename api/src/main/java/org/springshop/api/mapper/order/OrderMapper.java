package org.springshop.api.mapper.order;

import java.util.stream.Collectors;

import org.springshop.api.dto.order.OrderRequestDto;
import org.springshop.api.dto.order.OrderResponseDto;
import org.springshop.api.dto.order.OrderItemRequestDto;
import org.springshop.api.dto.order.OrderItemResponseDto;
import org.springshop.api.model.order.Order;
import org.springshop.api.model.order.OrderItem;
import org.springshop.api.model.payment.Address;
import org.springshop.api.model.product.Product;
import org.springshop.api.model.user.User;

public class OrderMapper {

    // -------------------- ORDER ENTITY/DTO --------------------

    public static Order toEntity(OrderRequestDto dto, Address address, User user) {
        if (dto == null) return null;

        Order order = new Order();
        order.setUser(user);
        order.setStatus(dto.getStatus());
        
        // CRÍTICO: Eliminar la asignación de totalAmount desde el DTO. 
        // El total debe ser calculado en el Servicio.
        // order.setTotalAmount(dto.getTotalAmount()); 

        order.setShippingAddress(address);
        return order;
    }

    public static OrderResponseDto toResponseDto(Order entity) {
        if (entity == null) return null;

        OrderResponseDto dto = new OrderResponseDto();
        dto.setId(entity.getId());
        dto.setStatus(entity.getStatus());
        
        // OK: El totalAmount es un campo de la entidad y debe ser devuelto.
        dto.setTotalAmount(entity.getTotalAmount()); 
        
        dto.setCreateAt(entity.getCreateAt());
        dto.setUpdateAt(entity.getUpdateAt());

        if (entity.getItems() != null) {
            dto.setItems(
                entity.getItems().stream()
                    .map(OrderMapper::toResponseDto)
                    .collect(Collectors.toList()));
        }
        
        // RECOMENDACIÓN: Considerar devolver el ID de la dirección de envío y el ID del usuario.

        return dto;
    }

    public static void updateOrder(Order existing, OrderRequestDto dto, Address address, User user) {
        if (existing == null || dto == null) return;

        existing.setStatus(dto.getStatus());
        
        // CRÍTICO: Eliminar la actualización de totalAmount desde el DTO. 
        // Si el total cambia, es porque la lógica de negocio lo recalculó.
        // existing.setTotalAmount(dto.getTotalAmount()); 

        existing.setShippingAddress(address);
        existing.setUser(user);
    }

    // -------------------- ORDER ITEM ENTITY/DTO --------------------
    
    /**
     * Crea un nuevo OrderItem y fija el precio del producto en la orden.
     * Este método solo debería ser llamado desde el Servicio durante la creación de la Orden.
     * @param dto DTO de creación (contiene solo la cantidad y el ID del producto).
     * @param product Entidad Producto (para obtener el precio actual).
     * @param order Entidad Order (para la referencia).
     * @return Nuevo OrderItem.
     */
    public static OrderItem toEntity(OrderItemRequestDto dto, Product product, Order order) {
        if (dto == null) return null;

        OrderItem item = new OrderItem();
        item.setProduct(product);
        item.setQuantity(dto.getQuantity());
        
        // CRÍTICO: FIJAR EL PRECIO AL CREAR EL ITEM (precio de la entidad Product)
        item.setPrice(product.getPrice()); 
        
        item.setOrder(order);
        return item;
    }
    
    // El método toEntity(OrderItemRequestDto, Product) que creaba un ítem huérfano ha sido eliminado.

    public static OrderItemResponseDto toResponseDto(OrderItem entity) {
        if (entity == null) return null;

        OrderItemResponseDto dto = new OrderItemResponseDto();
        dto.setId(entity.getId());
        dto.setQuantity(entity.getQuantity());
        
        // OK: Devolver el precio fijado de la Orden.
        dto.setPrice(entity.getPrice()); 
        
        dto.setProductId(entity.getProduct() != null ? entity.getProduct().getId() : null);
        
        // RECOMENDACIÓN: Considerar devolver el ID de la orden (entity.getOrder().getId()).
        
        return dto;
    }
    
    public static void updateOrderItem(OrderItem existing, OrderItemRequestDto dto, Product product, Order order) {
        if(existing == null || dto == null) return;
        
        // La actualización de ítems de una orden ya existente es inusual. 
        // Asumiendo que solo se actualiza la cantidad o la referencia de la orden/producto:
        existing.setQuantity(dto.getQuantity());
        existing.setOrder(order);
        existing.setProduct(product);

        // CRÍTICO: El precio de un OrderItem NO debe cambiar después de la creación 
        // (ya que es el registro histórico del precio de venta).
        // existing.setPrice(dto.getPrice()); // ELIMINADO
    }
}