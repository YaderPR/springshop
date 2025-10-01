package org.springshop.api.service.payment;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springshop.api.dto.payment.ShipmentRequestDto;
import org.springshop.api.dto.payment.ShipmentResponseDto;
import org.springshop.api.mapper.payment.ShipmentMapper;
import org.springshop.api.model.order.Order;
import org.springshop.api.model.payment.Shipment;
import org.springshop.api.repository.order.OrderRepository;
import org.springshop.api.repository.payment.ShipmentRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
@Transactional
public class ShipmentService {
    
    private final ShipmentRepository shipmentRepository;
    private final OrderRepository orderRepository;
    
    public ShipmentService(ShipmentRepository shipmentRepository, OrderRepository orderRepository) {
        this.shipmentRepository = shipmentRepository;
        this.orderRepository = orderRepository;
    }

    @Transactional(readOnly = true)
    public List<ShipmentResponseDto> getAllShipments() {
        return shipmentRepository.findAll().stream()
            .map(ShipmentMapper::toResponseDto)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<ShipmentResponseDto> getShipmentById(Integer id) {

        return shipmentRepository.findById(id)
            .map(ShipmentMapper::toResponseDto); 
    }
    
    @Transactional(readOnly = true)
    public List<ShipmentResponseDto> getShipmentsByOrderId(Integer orderId) {

        return shipmentRepository.findAllByOrderId(orderId).stream()
            .map(ShipmentMapper::toResponseDto)
            .collect(Collectors.toList());
    }

    public ShipmentResponseDto createShipment(ShipmentRequestDto requestDto) {

        Order order = findOrderOrThrow(requestDto.getOrderId());
        Shipment newShipment = ShipmentMapper.toEntity(requestDto, order);
        Shipment savedShipment = shipmentRepository.save(newShipment);
        
        return ShipmentMapper.toResponseDto(savedShipment);
    }


    public ShipmentResponseDto updateShipment(Integer id, ShipmentRequestDto requestDto) { 

        Shipment existingShipment = findShipmentOrThrow(id);
        Order order = findOrderOrThrow(requestDto.getOrderId());
        ShipmentMapper.updateShipment(existingShipment, requestDto, order);
        Shipment savedShipment = shipmentRepository.save(existingShipment);

        return ShipmentMapper.toResponseDto(savedShipment);
    }

    public void deleteShipment(Integer id) { 

        Shipment shipment = findShipmentOrThrow(id);
        shipmentRepository.delete(shipment);
    }

    public Shipment findShipmentOrThrow(Integer shipmentId) {
        return shipmentRepository.findById(shipmentId)
                .orElseThrow(() -> new EntityNotFoundException("Shipment not found with id: " + shipmentId));
    }
    
    private Order findOrderOrThrow(Integer orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found with id: " + orderId));
    }
}