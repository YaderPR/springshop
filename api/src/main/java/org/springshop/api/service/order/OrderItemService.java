// Archivo: org.springshop.api.service.order.OrderItemService.java
package org.springshop.api.service.order;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springshop.api.dto.order.OrderItemRequestDto;
import org.springshop.api.dto.order.OrderItemResponseDto;
import org.springshop.api.mapper.order.OrderMapper;
import org.springshop.api.model.order.Order;
import org.springshop.api.model.order.OrderItem;
import org.springshop.api.model.product.Product;
import org.springshop.api.repository.order.OrderItemRepository;
import org.springshop.api.repository.product.ProductRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
@Transactional
public class OrderItemService {
    
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;
    private final OrderService orderService; // Inyectamos el servicio padre

    public OrderItemService(OrderItemRepository orderItemRepository, ProductRepository productRepository, OrderService orderService) {
        this.orderItemRepository = orderItemRepository;
        this.productRepository = productRepository;
        this.orderService = orderService;
    }

    // -------------------- OBTENCIÓN --------------------

    public List<OrderItemResponseDto> getOrderItemsByOrderId(Integer orderId) {
        // Aseguramos que la orden padre existe (uso del método auxiliar del OrderService)
        orderService.findOrderOrThrow(orderId); 
        
        return orderItemRepository.findAllByOrderId(orderId).stream()
            .map(OrderMapper::toResponseDto)
            .collect(Collectors.toList());
    }

    public OrderItemResponseDto getOrderItemById(Integer id) {
        // Devolvemos el Optional mapeado, para que el Controller decida cómo manejar el Not Found.
        return orderItemRepository.findById(id).map(OrderMapper::toResponseDto)
                .orElseThrow(() -> new EntityNotFoundException("OrderItem not found with id: " + id));
    }
    
    // -------------------- CREACIÓN --------------------
    
    // NOTA: La creación de ítems de orden post-creación de la orden es una operación inusual.
    // Generalmente, OrderItems se crean SOLO cuando se crea la Orden, copiándolos del Cart.
    // Mantenemos el método CRUD si el dominio lo requiere, pero con validaciones.

    public OrderItemResponseDto createOrderItem(Integer orderId, OrderItemRequestDto requestDto) {
        Order order = orderService.findOrderOrThrow(orderId);
        Product product = findProductOrThrow(requestDto.getProductId());
        
        // Mapeamos a la entidad con la orden y el producto
        OrderItem orderItem = OrderMapper.toEntity(requestDto, product, order); 
        orderItem = orderItemRepository.save(orderItem);

        // Opcional: Recalcular el total de la orden después de añadir el ítem
        // RecalculateOrderTotal(order); 

        return OrderMapper.toResponseDto(orderItem);
    }
    
    // -------------------- ACTUALIZACIÓN --------------------
    
    public OrderItemResponseDto updateOrderItem(Integer orderId, Integer itemId, OrderItemRequestDto requestDto) {
        OrderItem orderItem = findOrderItemOrThrow(itemId);
        Order order = orderService.findOrderOrThrow(orderId);
        
        // 1. Validar Pertenencia (CRÍTICO)
        if (!orderItem.getOrder().getId().equals(orderId)) {
            throw new IllegalArgumentException("Item with id " + itemId + " does not belong to order with id " + orderId);
        }
        
        // El producto puede cambiar (si el DTO lo permite, aunque inusual para una orden)
        Product product = findProductOrThrow(requestDto.getProductId()); 
        
        // Usamos el nombre de mapper corregido (updateOrderItem)
        OrderMapper.updateOrderItem(orderItem, requestDto, product, order);
        
        OrderItem updatedOrderItem = orderItemRepository.save(orderItem);
        
        // Opcional: Recalcular el total de la orden después de modificar el ítem
        // RecalculateOrderTotal(order); 
        
        return OrderMapper.toResponseDto(updatedOrderItem);
    }
    
    // -------------------- ELIMINACIÓN --------------------
    
    public void deleteOrderItem(Integer orderId, Integer itemId) {
        OrderItem item = findOrderItemOrThrow(itemId);
        
        // 1. Validar Pertenencia
        if (!item.getOrder().getId().equals(orderId)) {
            throw new IllegalArgumentException("Item with id " + itemId + " does not belong to order with id " + orderId);
        }
        
        orderItemRepository.delete(item);
        
        // Opcional: Recalcular el total de la orden después de eliminar el ítem
        // RecalculateOrderTotal(item.getOrder()); 
    }
    
    // -------------------- MÉTODOS AUXILIARES --------------------
    
    private Product findProductOrThrow(Integer productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + productId));
    }
    
    private OrderItem findOrderItemOrThrow(Integer itemId) {
        return orderItemRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("OrderItem not found with id: " + itemId));
    }
    
    // Opcional: Método para centralizar el recálculo y guardado del total
    @SuppressWarnings("unused")
    private void RecalculateOrderTotal(Order order) {
        double newTotal = orderService.calculateOrderTotals(order.getId());
        order.setTotalAmount(newTotal);
        // Dado que estamos en @Transactional, y la orden es una entidad gestionada, 
        // simplemente cambiar el valor es suficiente, pero se podría llamar a orderRepository.save(order) 
        // para asegurar la persistencia inmediata.
    }
}
