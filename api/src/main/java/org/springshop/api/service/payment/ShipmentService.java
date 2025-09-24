package org.springshop.api.service.payment;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springshop.api.dto.payment.ShipmentRequestDto;
import org.springshop.api.dto.payment.ShipmentResponseDto;
import org.springshop.api.mapper.payment.ShipmentMapper;
import org.springshop.api.model.order.Order;
import org.springshop.api.model.payment.Shipment;
import org.springshop.api.repository.order.OrderRepository;
import org.springshop.api.repository.payment.ShipmentRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class ShipmentService {
    ShipmentRepository shipmentRepository;
    OrderRepository orderRepository;
    public ShipmentService(ShipmentRepository shipmentRepository, OrderRepository orderRepository) {
        this.shipmentRepository = shipmentRepository;
        this.orderRepository = orderRepository;
    }
    public ShipmentResponseDto createShipment(ShipmentRequestDto requestDto) {
        Order order = orderRepository.findById(requestDto.getOrderId()).orElseThrow(() -> new EntityNotFoundException("Order not found with id: " + requestDto.getOrderId()));
        Shipment savedShipment = shipmentRepository.save(ShipmentMapper.toEntity(requestDto, order));
        return ShipmentMapper.toDto(savedShipment);
    }
    public List<ShipmentResponseDto> getAllShipments() {
        return shipmentRepository.findAll().stream().map(ShipmentMapper::toDto).collect(Collectors.toList());
    }
    public ShipmentResponseDto getShipmentById(Integer id) {
        Shipment shipment = shipmentRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Shipment not found with id: " + id));
        return ShipmentMapper.toDto(shipment);
    }
    public ShipmentResponseDto updateShipmentById(Integer id, ShipmentRequestDto requestDto) {
        Order order = orderRepository.findById(requestDto.getOrderId()).orElseThrow(() -> new EntityNotFoundException("Order not found with id: " + requestDto.getOrderId()));
        Shipment shipment = shipmentRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Shipment not found with id: " + id));
        ShipmentMapper.updateEntity(shipment, requestDto, order);
        return ShipmentMapper.toDto(shipment);
    }
    public void deleteShipmentById(Integer id) {
        if(!shipmentRepository.existsById(id)) {
            throw new EntityNotFoundException("Shipment not found with id: " + id);
        }
        shipmentRepository.deleteById(id);
    }
}
