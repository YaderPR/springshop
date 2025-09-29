package org.springshop.api.controller.payment;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springshop.api.dto.payment.ShipmentRequestDto;
import org.springshop.api.dto.payment.ShipmentResponseDto;
import org.springshop.api.service.payment.ShipmentService;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import jakarta.validation.Valid; // Importante para la validación

@RestController
@RequestMapping("/api/shipments")
public class ShipmentController {

    private final static String BASE_URL = "/api/shipments";
    private final ShipmentService shipmentService;

    public ShipmentController(ShipmentService shipmentService) {
        this.shipmentService = shipmentService;
    }

    @PostMapping
    public ResponseEntity<ShipmentResponseDto> createShipment(@Valid @RequestBody ShipmentRequestDto requestDto) { 

        ShipmentResponseDto responseDto = shipmentService.createShipment(requestDto);
        URI location = URI.create(BASE_URL + "/" + responseDto.getId());

        return ResponseEntity.created(location).body(responseDto);
    }

    @GetMapping
    public ResponseEntity<List<ShipmentResponseDto>> getAllShipments() {

        return ResponseEntity.ok(shipmentService.getAllShipments());
    }

    @GetMapping("/{id:\\d+}")
    public ResponseEntity<ShipmentResponseDto> getShipmentById(@PathVariable Integer id) {

        return wrapOrNotFound(shipmentService.getShipmentById(id));
    }

    @PutMapping("/{id:\\d+}")
    public ResponseEntity<ShipmentResponseDto> updateShipment(
            @PathVariable Integer id,
            @Valid @RequestBody ShipmentRequestDto requestDto) {

        ShipmentResponseDto updatedDto = shipmentService.updateShipment(id, requestDto);

        return ResponseEntity.ok(updatedDto);
    }

    @DeleteMapping("/{id:\\d+}")
    public ResponseEntity<Void> deleteShipment(@PathVariable Integer id) {

        shipmentService.deleteShipment(id);

        return ResponseEntity.noContent().build();
    }

    private <T> ResponseEntity<T> wrapOrNotFound(Optional<T> maybeResponse) {

        return maybeResponse.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}