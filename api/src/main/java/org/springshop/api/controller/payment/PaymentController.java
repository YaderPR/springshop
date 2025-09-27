package org.springshop.api.controller.payment;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springshop.api.dto.payment.PaymentRequestDto;
import org.springshop.api.dto.payment.PaymentResponseDto;
import org.springshop.api.service.payment.PaymentService;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import jakarta.validation.Valid; // Importante para la validación
import org.springframework.web.bind.annotation.RequestBody; // CORRECCIÓN: Usar la importación de Spring

@RestController
@RequestMapping("/api/payments")
public class PaymentController {
    
    private final static String BASE_URL = "/api/payments";
    private final PaymentService paymentService; // Eliminamos la inicialización duplicada
    
    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    // -------------------- CRUD BÁSICO --------------------
    
    /**
     * Crea un nuevo pago.
     */
    @PostMapping
    public ResponseEntity<PaymentResponseDto> createPayment(@Valid @RequestBody PaymentRequestDto requestDto) { // Añadimos @Valid
        PaymentResponseDto responseDto = paymentService.createPayment(requestDto);
        // CORRECCIÓN: La URI debe incluir una barra de separación antes del ID
        URI location = URI.create(BASE_URL + "/" + responseDto.getId()); 
        return ResponseEntity.created(location).body(responseDto);
    }
    
    /**
     * Obtiene todos los pagos.
     * (NOTA: Aquí se podría añadir filtrado por status, fecha, etc.)
     */
    @GetMapping
    public ResponseEntity<List<PaymentResponseDto>> getAllPayments() {
        return ResponseEntity.ok(paymentService.getAllPayments());
    }

    /**
     * Obtiene un pago por ID.
     */
    @GetMapping("/{id:\\d+}") // Añadimos validación de ruta numérica
    public ResponseEntity<PaymentResponseDto> getPaymentById(@PathVariable Integer id) {
        // CORRECCIÓN CRÍTICA: Usamos wrapOrNotFound para manejar el 404
        return wrapOrNotFound(paymentService.getPaymentById(id));
    }
    
    /**
     * Actualiza un pago por ID.
     */
    @PutMapping("/{id:\\d+}") // Añadimos validación de ruta numérica
    public ResponseEntity<PaymentResponseDto> updatePayment( // CONSISTENCIA: Renombramos
            @PathVariable Integer id, 
            @Valid @RequestBody PaymentRequestDto requestDto) { // Añadimos @Valid
        
        // CORRECCIÓN: Usamos el nombre de método corregido en el servicio y confiamos en el 404 del servicio
        PaymentResponseDto updatedDto = paymentService.updatePayment(id, requestDto); 
        return ResponseEntity.ok(updatedDto);
    }
    
    /**
     * Elimina un pago por ID.
     */
    @DeleteMapping("/{id:\\d+}") // Añadimos validación de ruta numérica
    public ResponseEntity<Void> deletePayment(@PathVariable Integer id) { // CONSISTENCIA: Renombramos
        // CORRECCIÓN: Usamos el nombre de método corregido en el servicio
        paymentService.deletePayment(id); 
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