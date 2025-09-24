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
import org.springshop.api.dto.payment.AddressRequestDto;
import org.springshop.api.dto.payment.AddressResponseDto;
import org.springshop.api.service.payment.AddressService;


@RestController
@RequestMapping("/api/addresses")
public class AddressController {
    private final AddressService addressService;
    private final static String BASE_URL = "/api/addresses";
    public AddressController(AddressService addressService) {
        this.addressService = addressService;
    }
    @PostMapping
    public ResponseEntity<AddressResponseDto> createAddress(@RequestBody AddressRequestDto requestDto) {
        AddressResponseDto responseDto = addressService.createAddress(requestDto);
        return ResponseEntity.created(URI.create(BASE_URL + responseDto.getId())).body(responseDto);
    }
    @GetMapping
    public List<AddressResponseDto> getAllAddresses() {
        return addressService.getAllAddresses();
    }
    @GetMapping("{id}")
    public AddressResponseDto getAddressById(@PathVariable Integer id) {
        return addressService.getAddressById(id);
    }
    @PutMapping("/{id}")
    public AddressResponseDto updateAddressById(@PathVariable Integer id, AddressRequestDto requestDto) {
        return addressService.updateAddressById(id, requestDto);
    }
    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteAddressById(@PathVariable Integer id) {
        addressService.deleteAddressById(id);
        return ResponseEntity.noContent().build(); 
    }
}
