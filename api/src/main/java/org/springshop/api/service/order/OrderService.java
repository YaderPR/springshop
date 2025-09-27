// Archivo: org.springshop.api.service.order.OrderService.java
package org.springshop.api.service.order;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springshop.api.dto.order.OrderRequestDto;
import org.springshop.api.dto.order.OrderResponseDto;
import org.springshop.api.mapper.order.OrderMapper;
import org.springshop.api.model.order.Order;
import org.springshop.api.model.payment.Address;
import org.springshop.api.model.user.User;
import org.springshop.api.repository.order.OrderRepository;
import org.springshop.api.repository.payment.AddressRepository;
import org.springshop.api.repository.user.UserRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
@Transactional
public class OrderService {
    
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final AddressRepository addressRepository;

    // Eliminamos OrderItemRepository y ProductRepository de aquí (SRP).
    public OrderService(OrderRepository orderRepository, UserRepository userRepository, AddressRepository addressRepository) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.addressRepository = addressRepository;
    }

    // -------------------- CRUD DE ORDEN --------------------

    public List<OrderResponseDto> getAllOrders() {
        return orderRepository.findAll().stream()
            .map(OrderMapper::toResponseDto)
            .collect(Collectors.toList());
    }

    /**
     * Usa findOrderOrThrow para centralizar la lógica de Not Found.
     * @param id ID de la orden.
     * @return DTO de la orden.
     */
    public Optional<OrderResponseDto> getOrderById(Integer id) {
        // En lugar de findById().orElseGet(null), devolvemos el Optional mapeado
        return orderRepository.findById(id).map(OrderMapper::toResponseDto);
    }

    /**
     * Crea una orden. El total debe ser calculado DESPUÉS de agregar los ítems, 
     * pero para el CRUD inicial, lo dejamos simple.
     * @param requestDto DTO de la orden.
     * @return DTO de respuesta.
     */
    public OrderResponseDto createOrder(OrderRequestDto requestDto) {
        User user = findUserOrThrow(requestDto.getUserId());
        Address address = findAddressOrThrow(requestDto.getAddressId());
        
        Order order = OrderMapper.toEntity(requestDto, address, user);
        
        // El totalAmount se establece a 0 aquí, y se recalculará al añadir ítems.
        order.setTotalAmount(0.0); 
        
        return OrderMapper.toResponseDto(orderRepository.save(order));
    }

    public OrderResponseDto updateOrder(Integer id, OrderRequestDto requestDto) {
        Order order = findOrderOrThrow(id); // Optimización: Usamos método auxiliar
        
        // Validación de referencias (asumiendo que User y Address pueden cambiar)
        User user = findUserOrThrow(requestDto.getUserId());
        Address address = findAddressOrThrow(requestDto.getAddressId());
        
        // Usamos el nombre de mapper corregido (updateOrder)
        OrderMapper.updateOrder(order, requestDto, address, user);
        
        // No es necesario llamar a save() si el totalAmount no se recalcula aquí,
        // pero lo mantenemos si el mapper hace más cambios.
        return OrderMapper.toResponseDto(orderRepository.save(order));
    } 

    public void deleteOrder(Integer id) {
        // Optimización: findById y delete(entity) es más limpio y eficiente que existsById + deleteById
        Order order = findOrderOrThrow(id);
        orderRepository.delete(order);
    }

    // -------------------- CÁLCULO DE TOTALES (Como en CartService) --------------------

    /**
     * Calcula el monto total de la orden basado en los ítems existentes.
     * Se usa para actualizar la Order.totalAmount después de modificar ítems.
     * @param orderId ID de la orden.
     * @return El monto total calculado.
     */
    @Transactional(readOnly = true)
    public double calculateOrderTotals(Integer orderId) {
        Order order = findOrderOrThrow(orderId);

        return order.getItems().stream()
                .filter(item -> item.getProduct() != null)
                .mapToDouble(item -> item.getPrice() * item.getQuantity())
                .sum();
    }
    
    // -------------------- MÉTODOS AUXILIARES --------------------
    
    // Método auxiliar para buscar Orden
    public Order findOrderOrThrow(Integer orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found with id: " + orderId));
    }
    
    private User findUserOrThrow(Integer userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));
    }

    private Address findAddressOrThrow(Integer addressId) {
        return addressRepository.findById(addressId)
                .orElseThrow(() -> new EntityNotFoundException("Address not found with id: " + addressId));
    }
}