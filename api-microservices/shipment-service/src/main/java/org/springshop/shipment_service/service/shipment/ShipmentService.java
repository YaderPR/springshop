package org.springshop.shipment_service.service.shipment;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springshop.shipment_service.dto.shipment.ShipmentRequestDto;
import org.springshop.shipment_service.dto.shipment.ShipmentResponseDto;
import org.springshop.shipment_service.mapper.shipment.ShipmentMapper;
import org.springshop.shipment_service.model.order.Order;
import org.springshop.shipment_service.model.shipment.Shipment;
import org.springshop.shipment_service.model.shipment.ShipmentStatus;
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
    public Optional<ShipmentResponseDto> findLatestShipmentByOrder(Integer orderId) {
        return shipmentRepository.findTopByOrderIdOrderByShippedAtDesc(orderId)
                .map(ShipmentMapper::toResponseDto);
    }
    @Async
    @Transactional 
    public void simulateShipment(Integer shipmentId) {
        System.out.println("Hilo: " + Thread.currentThread().getName() + " - Iniciando envío para Shipment ID: " + shipmentId);

        try {
            // SIMULACIÓN DE PROCESO DE LARGA DURACIÓN (60 segundos)
            Thread.sleep(60000); 

            // 1. Obtener la entidad Shipment
            Shipment shipment = findShipmentOrThrow(shipmentId);

            // 2. Aplicar el cambio de estado interno
            shipment.setStatus(ShipmentStatus.SHIPPED);
            shipment.setTrackingNumber(generateRandomTrackingId()); // Simular asignación de tracking
            shipment.setDeliveredAt(LocalDateTime.now());
            shipment.setCarrier("UPS");
            // 3. Persistir el cambio de estado (guardado por el Dirty Checking de @Transactional)
            shipmentRepository.save(shipment);
            
            System.out.println("✅ Hilo: " + Thread.currentThread().getName() + " - Shipment ID: " + shipmentId + " CAMBIADO a SHIPPED.");

        } catch (InterruptedException e) {
            // Manejo de interrupciones del hilo (puede ser un rollback a FAILED)
            Thread.currentThread().interrupt();
            System.err.println("❌ Proceso de envío interrumpido para Shipment ID: " + shipmentId);
            updateShipmentStatus(shipmentId, ShipmentStatus.RETURNED);
        } catch (Exception e) {
            System.err.println("❌ Error en el proceso asíncrono para Shipment ID: " + shipmentId + ". Error: " + e.getMessage());
            updateShipmentStatus(shipmentId, ShipmentStatus.RETURNED);
        }
    }
    
    // Método auxiliar para actualizar estado a FAILED
    private void updateShipmentStatus(Integer shipmentId, ShipmentStatus status) {
        shipmentRepository.findById(shipmentId).ifPresent(shipment -> {
            shipment.setStatus(status);
            shipmentRepository.save(shipment);
        });
    }
    
    private String generateRandomTrackingId() {
        return "TRK-" + (int) (Math.random() * 1000000);
    }
    //Helpers
    public Shipment findShipmentOrThrow(Integer shipmentId) {
        return shipmentRepository.findById(shipmentId)
                .orElseThrow(() -> new EntityNotFoundException("Shipment not found with id: " + shipmentId));
    }
    
    private Order findOrderOrThrow(Integer orderId) {
        return orderClient.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found with id: " + orderId));
    }
}