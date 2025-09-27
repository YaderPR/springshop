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

    // -------------------- CRUD BÁSICO --------------------
    
    /**
     * Crea un nuevo envío.
     */
    @PostMapping
    public ResponseEntity<ShipmentResponseDto> createShipment(@Valid @RequestBody ShipmentRequestDto requestDto) { // Añadimos @Valid
        ShipmentResponseDto responseDto = shipmentService.createShipment(requestDto);
        // CORRECCIÓN: La URI debe incluir una barra de separación antes del ID
        URI location = URI.create(BASE_URL + "/" + responseDto.getId()); 
        return ResponseEntity.created(location).body(responseDto);
    }
    
    /**
     * Obtiene todos los envíos.
     */
    @GetMapping
    public ResponseEntity<List<ShipmentResponseDto>> getAllShipments() {
        return ResponseEntity.ok(shipmentService.getAllShipments());
    }

    /**
     * Obtiene un envío por ID.
     */
    @GetMapping("/{id:\\d+}") // Añadimos validación de ruta numérica
    public ResponseEntity<ShipmentResponseDto> getShipmentById(@PathVariable Integer id) {
        // Usamos wrapOrNotFound para manejar el 404 de forma limpia (el servicio devuelve Optional)
        return wrapOrNotFound(shipmentService.getShipmentById(id));
    }
    
    /**
     * Actualiza un envío por ID.
     */
    @PutMapping("/{id:\\d+}") // Añadimos validación de ruta numérica
    public ResponseEntity<ShipmentResponseDto> updateShipment( // CONSISTENCIA: Renombramos
            @PathVariable Integer id, 
            @Valid @RequestBody ShipmentRequestDto requestDto) { // Añadimos @Valid
        
        // Usamos el nombre de método corregido en el servicio
        ShipmentResponseDto updatedDto = shipmentService.updateShipment(id, requestDto); 
        return ResponseEntity.ok(updatedDto);
    }
    
    /**
     * Elimina un envío por ID.
     */
    @DeleteMapping("/{id:\\d+}") // Añadimos validación de ruta numérica
    public ResponseEntity<Void> deleteShipment(@PathVariable Integer id) { // CONSISTENCIA: Renombramos
        // Usamos el nombre de método corregido en el servicio
        shipmentService.deleteShipment(id); 
        return ResponseEntity.noContent().build();
    }
    
    // -------------------- HELPER --------------------
    
    /**
     * Convierte un Optional<T> en ResponseEntity<T> (200 OK) o 404 Not Found.
     */
    private <T> ResponseEntity<T> wrapOrNotFound(Optional<T> maybeResponse) {
        return maybeResponse.map(ResponseEntity::ok)
                            .orElse(ResponseEntity.notFound().build());
    }
}