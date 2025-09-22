package org.springshop.api.mapper.payment;

import org.springshop.api.dto.payment.AddressRequestDto;
import org.springshop.api.dto.payment.AddressResponseDto;
import org.springshop.api.model.payment.Address;

public class AddressMapper {

    // Convierte de RequestDto -> Entidad
    public static Address toEntity(AddressRequestDto dto) {
        if (dto == null) return null;

        Address address = new Address();
        address.setStreet(dto.getStreet());
        address.setCity(dto.getCity());
        address.setState(dto.getState());
        address.setCountry(dto.getCountry());
        address.setZipCode(dto.getZipCode());
        address.setPhoneNumber(dto.getPhoneNumber());
        return address;
    }

    // Convierte de Entidad -> ResponseDto
    public static AddressResponseDto toDto(Address entity) {
        if (entity == null) return null;

        AddressResponseDto dto = new AddressResponseDto();
        dto.setId(entity.getId());
        dto.setStreet(entity.getStreet());
        dto.setCity(entity.getCity());
        dto.setState(entity.getState());
        dto.setCountry(entity.getCountry());
        dto.setZipCode(entity.getZipCode());
        dto.setPhoneNumber(entity.getPhoneNumber());

        if (entity.getUser() != null) {
            dto.setUsername(entity.getUser().getUsername());
        }

        return dto;
    }
}
