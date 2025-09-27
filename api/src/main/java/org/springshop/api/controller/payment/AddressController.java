package org.springshop.api.controller.payment;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springshop.api.dto.payment.AddressRequestDto;
import org.springshop.api.dto.payment.AddressResponseDto;
import org.springshop.api.service.payment.AddressService;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import jakarta.validation.Valid; // Importante para la validación

@RestController
@RequestMapping("/api/addresses")
public class AddressController {
    
    private final AddressService addressService;
    private final static String BASE_URL = "/api/addresses";
    
    public AddressController(AddressService addressService) {
        this.addressService = addressService;
    }

    // -------------------- CRUD BÁSICO --------------------
    
    /**
     * Crea una nueva dirección.
     */
    @PostMapping
    public ResponseEntity<AddressResponseDto> createAddress(@Valid @RequestBody AddressRequestDto requestDto) { // Añadimos @Valid
        AddressResponseDto responseDto = addressService.createAddress(requestDto);
        // NOTA: La URI debe incluir una barra de separación antes del ID
        URI location = URI.create(BASE_URL + "/" + responseDto.getId()); 
        return ResponseEntity.created(location).body(responseDto);
    }
    
    /**
     * Obtiene todas las direcciones (generalmente solo para admins).
     * NOTA: Este endpoint no soporta filtrado por userId, ese se manejará en el UserController.
     */
    @GetMapping
    public ResponseEntity<List<AddressResponseDto>> getAllAddresses() {
        // CORRECCIÓN: Devolvemos ResponseEntity.ok
        return ResponseEntity.ok(addressService.getAllAddresses()); 
    }

    /**
     * Obtiene una dirección por ID.
     */
    @GetMapping("/{id:\\d+}") // Añadimos validación de ruta numérica
    public ResponseEntity<AddressResponseDto> getAddressById(@PathVariable Integer id) {
        // CORRECCIÓN: Usamos el método de servicio que devuelve Optional
        return wrapOrNotFound(addressService.getAddressById(id)); 
    }
    
    /**
     * Actualiza una dirección por ID.
     */
    @PutMapping("/{id:\\d+}") // Añadimos validación de ruta numérica
    public ResponseEntity<AddressResponseDto> updateAddress(
            @PathVariable Integer id, 
            @Valid @RequestBody AddressRequestDto requestDto) { // Añadimos @Valid y @RequestBody
        
        // CORRECCIÓN: Confiamos en que el servicio lance 404
        AddressResponseDto updatedDto = addressService.updateAddress(id, requestDto); 
        return ResponseEntity.ok(updatedDto);
    }
    
    /**
     * Elimina una dirección por ID.
     */
    @DeleteMapping("/{id:\\d+}") // Añadimos validación de ruta numérica
    public ResponseEntity<Void> deleteAddress(@PathVariable Integer id) { // CONSISTENCIA: Renombramos
        // CORRECCIÓN: Usamos el nombre de método corregido en el servicio
        addressService.deleteAddress(id); 
        return ResponseEntity.noContent().build(); 
    }
    
    // -------------------- HELPER --------------------
    
    /**
     * Convierte un Optional<T> en ResponseEntity<T> o 404 Not Found.
     */
    private <T> ResponseEntity<T> wrapOrNotFound(Optional<T> maybeResponse) {
        return maybeResponse.map(ResponseEntity::ok)
                            .orElse(ResponseEntity.notFound().build());
    }
}