package org.springshop.api.controller.payment;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springshop.api.dto.payment.ShipmentRequestDto;
import org.springshop.api.dto.payment.ShipmentResponseDto;
import org.springshop.api.service.payment.ShipmentService;

@RestController
@RequestMapping("/api/shipments")
public class ShipmentController {
    private final static String BASE_URL = "/api/shipments";
    ShipmentService shipmentService;
    public ShipmentController(ShipmentService shipmentService) {
        this.shipmentService = shipmentService;
    }
    @PostMapping
    public ResponseEntity<ShipmentResponseDto> createShipment(@RequestBody ShipmentRequestDto requestDto) {
        ShipmentResponseDto responseDto = shipmentService.createShipment(requestDto);
        return ResponseEntity.created(URI.create(BASE_URL + responseDto.getId())).body(responseDto);
    }
    @GetMapping
    public ResponseEntity<List<ShipmentResponseDto>> getAllShipments() {
        return ResponseEntity.ok(shipmentService.getAllShipments());
    }
    @GetMapping("/{id}")
    public ResponseEntity<ShipmentResponseDto> getShipmentById(@PathVariable Integer id) {
        return ResponseEntity.ok(shipmentService.getShipmentById(id));
    }
    @PutMapping("/{id}")
    public ResponseEntity<ShipmentResponseDto> updateShipmentById(@PathVariable Integer id, @RequestBody ShipmentRequestDto requestDto) {
        return ResponseEntity.ok(shipmentService.updateShipmentById(id, requestDto));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteShipmentById(@PathVariable Integer id) {
        shipmentService.deleteShipmentById(id);
        return ResponseEntity.noContent().build();
    } 
}
