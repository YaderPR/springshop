package org.springshop.api.service.payment;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Importante
import org.springshop.api.dto.payment.ShipmentRequestDto;
import org.springshop.api.dto.payment.ShipmentResponseDto;
import org.springshop.api.mapper.payment.ShipmentMapper;
import org.springshop.api.model.order.Order;
import org.springshop.api.model.payment.Shipment;
import org.springshop.api.repository.order.OrderRepository;
import org.springshop.api.repository.payment.ShipmentRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
@Transactional // Aplicamos transaccionalidad a nivel de clase
public class ShipmentService {
    
    private final ShipmentRepository shipmentRepository;
    private final OrderRepository orderRepository;
    
    public ShipmentService(ShipmentRepository shipmentRepository, OrderRepository orderRepository) {
        this.shipmentRepository = shipmentRepository;
        this.orderRepository = orderRepository;
    }

// -------------------- LECTURA (READ) Y BÚSQUEDA --------------------

    /**
     * Obtiene todos los envíos.
     */
    @Transactional(readOnly = true)
    public List<ShipmentResponseDto> getAllShipments() {
        return shipmentRepository.findAll().stream()
            .map(ShipmentMapper::toResponseDto) // CONSISTENCIA: Usamos toResponseDto
            .collect(Collectors.toList());
    }

    /**
     * Obtiene un envío por ID.
     */
    @Transactional(readOnly = true)
    public Optional<ShipmentResponseDto> getShipmentById(Integer id) {
        // CORRECCIÓN: Devolvemos Optional para manejo consistente en el controlador
        return shipmentRepository.findById(id)
            .map(ShipmentMapper::toResponseDto); 
    }
    
    /**
     * Obtiene todos los envíos asociados a una orden específica.
     * (Requiere: List<Shipment> findAllByOrderId(Integer orderId) en el repositorio)
     */
    @Transactional(readOnly = true)
    public List<ShipmentResponseDto> getShipmentsByOrderId(Integer orderId) {
        return shipmentRepository.findAllByOrderId(orderId).stream()
            .map(ShipmentMapper::toResponseDto)
            .collect(Collectors.toList());
    }

// -------------------- ESCRITURA (CREATE, UPDATE, DELETE) --------------------

    /**
     * Crea un nuevo envío, enlazándolo a una orden existente.
     */
    public ShipmentResponseDto createShipment(ShipmentRequestDto requestDto) {
        // Centralizamos la búsqueda del Order
        Order order = findOrderOrThrow(requestDto.getOrderId());
        
        Shipment newShipment = ShipmentMapper.toEntity(requestDto, order);
        Shipment savedShipment = shipmentRepository.save(newShipment);
        
        return ShipmentMapper.toResponseDto(savedShipment);
    }

    /**
     * Actualiza un envío existente.
     */
    public ShipmentResponseDto updateShipment(Integer id, ShipmentRequestDto requestDto) { // CONSISTENCIA: Renombramos
        // 1. Centralizamos la búsqueda del recurso existente
        Shipment existingShipment = findShipmentOrThrow(id);
        
        // 2. Buscamos la nueva OrderId del DTO (clave foránea)
        Order order = findOrderOrThrow(requestDto.getOrderId());
        
        // 3. Delegamos el mapeo al mapper (usando la convención updateShipment)
        ShipmentMapper.updateShipment(existingShipment, requestDto, order); // CONSISTENCIA: Renombramos
        
        // Guardamos y devolvemos el DTO actualizado
        Shipment savedShipment = shipmentRepository.save(existingShipment);
        return ShipmentMapper.toResponseDto(savedShipment);
    }

    /**
     * Elimina un envío por ID.
     */
    public void deleteShipment(Integer id) { // CONSISTENCIA: Renombramos
        // Optimizamos la eliminación: buscar y eliminar
        Shipment shipment = findShipmentOrThrow(id);
        shipmentRepository.delete(shipment);
    }

// -------------------- MÉTODOS AUXILIARES DE BÚSQUEDA --------------------

    /**
     * Busca un Shipment por ID o lanza EntityNotFoundException.
     */
    public Shipment findShipmentOrThrow(Integer shipmentId) {
        return shipmentRepository.findById(shipmentId)
                .orElseThrow(() -> new EntityNotFoundException("Shipment not found with id: " + shipmentId));
    }
    
    /**
     * Busca un Order por ID o lanza EntityNotFoundException.
     */
    private Order findOrderOrThrow(Integer orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found with id: " + orderId));
    }
}