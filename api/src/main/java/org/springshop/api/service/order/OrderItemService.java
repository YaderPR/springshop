package org.springshop.api.service.order;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springshop.api.controller.exception.StockException;
import org.springshop.api.dto.order.OrderItemRequestDto;
import org.springshop.api.dto.order.OrderItemResponseDto;
import org.springshop.api.mapper.order.OrderMapper;
import org.springshop.api.model.order.Order;
import org.springshop.api.model.order.OrderItem;
import org.springshop.api.model.product.Product;
import org.springshop.api.repository.order.OrderItemRepository;
import org.springshop.api.repository.product.ProductRepository;
import org.springshop.api.service.product.ProductService;

import jakarta.persistence.EntityNotFoundException;

@Service
@Transactional
public class OrderItemService {
    
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;
    private final ProductService productService; 
    // private final OrderService orderService; ⬅️ ¡ELIMINADA PARA ROMPER EL CICLO!

    public OrderItemService(OrderItemRepository orderItemRepository, ProductRepository productRepository, 
                            ProductService productService) {
        this.orderItemRepository = orderItemRepository;
        this.productRepository = productRepository;
        this.productService = productService;
    }

    // -------------------- OBTENCIÓN --------------------

    // NOTA: La validación de que la orden existe ahora se hace en el OrderService llamador.
    public List<OrderItemResponseDto> getOrderItemsByOrderId(Integer orderId) {
        return orderItemRepository.findAllByOrderId(orderId).stream()
            .map(OrderMapper::toResponseDto)
            .collect(Collectors.toList());
    }

    public OrderItemResponseDto getOrderItemById(Integer id) {
        return orderItemRepository.findById(id).map(OrderMapper::toResponseDto)
                .orElseThrow(() -> new EntityNotFoundException("OrderItem not found with id: " + id));
    }
    
    // -------------------- CREACIÓN (Requiere la entidad Order) --------------------
    
    /**
     * Crea un nuevo OrderItem y reduce el stock del producto de forma atómica.
     * @param order La entidad Order padre (ya buscada y persistida).
     */
    public OrderItemResponseDto createOrderItem(Order order, OrderItemRequestDto requestDto) {
        // La entidad Order ya está validada y buscada por el OrderService.
        Product product = findProductOrThrow(requestDto.getProductId());
        
        // 1. VALIDACIÓN y REDUCCIÓN DE STOCK (CRÍTICO)
        try {
            // Se reduce el stock. Si falla, la transacción es revertida.
            productService.updateStock(product.getId(), -requestDto.getQuantity()); 
        } catch (StockException e) {
            throw new StockException(e.getMessage()); 
        }
        
        // 2. CREACIÓN DEL ITEM DE ORDEN
        OrderItem orderItem = OrderMapper.toEntity(requestDto, product, order); 
        orderItem = orderItemRepository.save(orderItem);

        // 3. RECALCULAR TOTALES: Movido al OrderService que es el padre de la transacción. 

        return OrderMapper.toResponseDto(orderItem);
    }
    
    // -------------------- ACTUALIZACIÓN --------------------
    
    /**
     * Actualiza un OrderItem, ajustando el stock según el cambio de cantidad.
     */
    public OrderItemResponseDto updateOrderItem(Integer orderId, Integer itemId, OrderItemRequestDto requestDto) {
        OrderItem orderItem = findOrderItemOrThrow(itemId);
        
        // 1. Validar Pertenencia (CRÍTICO)
        if (!orderItem.getOrder().getId().equals(orderId)) {
            throw new IllegalArgumentException("Item with id " + itemId + " does not belong to order with id " + orderId);
        }
        
        int oldQuantity = orderItem.getQuantity();
        int newQuantity = requestDto.getQuantity();
        int delta = newQuantity - oldQuantity; 

        // 2. AJUSTE DE STOCK
        if (delta != 0) {
             try {
                 productService.updateStock(orderItem.getProduct().getId(), -delta);
             } catch (StockException e) {
                throw new StockException(e.getMessage());
             }
        }
        
        // 3. Mapeo y Guardado
        Product product = findProductOrThrow(requestDto.getProductId()); 
        OrderMapper.updateOrderItem(orderItem, requestDto, product, orderItem.getOrder());
        
        OrderItem updatedOrderItem = orderItemRepository.save(orderItem);
        
        // 4. Recálculo del Total de la Orden: Movido al OrderService
        
        return OrderMapper.toResponseDto(updatedOrderItem);
    }
    
    // -------------------- ELIMINACIÓN --------------------
    
    /**
     * Elimina un OrderItem y repone el stock del producto.
     */
    public void deleteOrderItem(Integer orderId, Integer itemId) {
        OrderItem item = findOrderItemOrThrow(itemId);
        
        // 1. Validar Pertenencia
        if (!item.getOrder().getId().equals(orderId)) {
            throw new IllegalArgumentException("Item with id " + itemId + " does not belong to order with id " + orderId);
        }
        
        // 2. REPOSICIÓN DE STOCK (CRÍTICO)
        productService.updateStock(item.getProduct().getId(), item.getQuantity()); 
        
        orderItemRepository.delete(item);
        
        // 3. Recálculo del Total de la Orden: Movido al OrderService
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
}