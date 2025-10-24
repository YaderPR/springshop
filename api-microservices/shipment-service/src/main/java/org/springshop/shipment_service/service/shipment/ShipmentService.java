package org.springshop.shipment_service.service.shipment;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springshop.shipment_service.dto.shipment.ShipmentRequestDto;
import org.springshop.shipment_service.dto.shipment.ShipmentResponseDto;
import org.springshop.shipment_service.mapper.shipment.ShipmentMapper;
import org.springshop.shipment_service.model.order.Order;
import org.springshop.shipment_service.model.shipment.Shipment;
import org.springshop.shipment_service.client.OrderClient;
import org.springshop.shipment_service.repository.shipment.ShipmentRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
@Transactional
public class ShipmentService {
    
    private final ShipmentRepository shipmentRepository;
    private final OrderClient orderClient;
    
    public ShipmentService(ShipmentRepository shipmentRepository, OrderClient orderClient) {
        this.shipmentRepository = shipmentRepository;
        this.orderClient = orderClient;
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
        Shipment newShipment = ShipmentMapper.toEntity(requestDto, order.getId());
        Shipment savedShipment = shipmentRepository.save(newShipment);
        
        return ShipmentMapper.toResponseDto(savedShipment);
    }


    public ShipmentResponseDto updateShipment(Integer id, ShipmentRequestDto requestDto) { 

        Shipment existingShipment = findShipmentOrThrow(id);
        Order order = findOrderOrThrow(requestDto.getOrderId());
        ShipmentMapper.updateShipment(existingShipment, requestDto, order.getId());
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
        return orderClient.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found with id: " + orderId));
    }
}