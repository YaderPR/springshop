package org.springshop.shipment_service.controller.address;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springshop.shipment_service.dto.address.AddressRequestDto;
import org.springshop.shipment_service.dto.address.AddressResponseDto;
import org.springshop.shipment_service.service.address.AddressService;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/addresses")
public class AddressController {

    private final AddressService addressService;
    private final static String BASE_URL = "/api/addresses";

    public AddressController(AddressService addressService) {
        this.addressService = addressService;
    }

    @PostMapping
    public ResponseEntity<AddressResponseDto> createAddress(@Valid @RequestBody AddressRequestDto requestDto) { // Añadimos
                                                                                                                // @Valid

        AddressResponseDto responseDto = addressService.createAddress(requestDto);
        URI location = URI.create(BASE_URL + "/" + responseDto.getId());

        return ResponseEntity.created(location).body(responseDto);
    }

    @GetMapping
    public ResponseEntity<List<AddressResponseDto>> getAllAddresses() {

        return ResponseEntity.ok(addressService.getAllAddresses());
    }

    @GetMapping("/{id:\\d+}")
    public ResponseEntity<AddressResponseDto> getAddressById(@PathVariable Integer id) {

        return wrapOrNotFound(addressService.getAddressById(id));
    }

    @PutMapping("/{id:\\d+}")
    public ResponseEntity<AddressResponseDto> updateAddress(
            @PathVariable Integer id,
            @Valid @RequestBody AddressRequestDto requestDto) {

        AddressResponseDto updatedDto = addressService.updateAddress(id, requestDto);

        return ResponseEntity.ok(updatedDto);
    }

    @DeleteMapping("/{id:\\d+}")
    public ResponseEntity<Void> deleteAddress(@PathVariable Integer id) {

        addressService.deleteAddress(id);

        return ResponseEntity.noContent().build();
    }

    private <T> ResponseEntity<T> wrapOrNotFound(Optional<T> maybeResponse) {

        return maybeResponse.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}