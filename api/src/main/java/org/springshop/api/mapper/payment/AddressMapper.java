package org.springshop.api.mapper.payment;

import org.springshop.api.dto.payment.AddressRequestDto;
import org.springshop.api.dto.payment.AddressResponseDto;
import org.springshop.api.model.payment.Address;
import org.springshop.api.model.user.User;

public class AddressMapper {

    // Convierte de RequestDto -> Entidad
    public static Address toEntity(AddressRequestDto dto, User user) {
        if (dto == null) return null;

        Address address = new Address();
        address.setStreet(dto.getStreet());
        address.setCity(dto.getCity());
        address.setState(dto.getState());
        address.setCountry(dto.getCountry());
        address.setZipCode(dto.getZipCode());
        address.setPhoneNumber(dto.getPhoneNumber());
        address.setUser(user);
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
    public static void updateEntity(Address existing, AddressRequestDto requestDto, User user) {
        existing.setCity(requestDto.getCity());
        existing.setCountry(requestDto.getCountry());
        existing.setState(requestDto.getState());
        existing.setStreet(requestDto.getStreet());
        existing.setZipCode(requestDto.getZipCode());
        existing.setPhoneNumber(requestDto.getPhoneNumber());
        existing.setCountry(requestDto.getCountry());
        existing.setUser(user);
    }
}
