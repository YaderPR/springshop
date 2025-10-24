package org.springshop.shipment_service.mapper.address;

import org.springshop.shipment_service.dto.address.AddressRequestDto;
import org.springshop.shipment_service.dto.address.AddressResponseDto;
import org.springshop.shipment_service.model.address.Address;

public class AddressMapper {

    public static Address toEntity(AddressRequestDto dto, Integer userId) {
        if (dto == null)
            return null;

        Address address = new Address();
        address.setStreet(dto.getStreet());
        address.setCity(dto.getCity());
        address.setState(dto.getState());
        address.setCountry(dto.getCountry());
        address.setZipCode(dto.getZipCode());
        address.setPhoneNumber(dto.getPhoneNumber());
        address.setUserId(userId);
        return address;
    }

    public static AddressResponseDto toResponseDto(Address entity) {
        if (entity == null)
            return null;

        AddressResponseDto dto = new AddressResponseDto();
        dto.setId(entity.getId());
        dto.setStreet(entity.getStreet());
        dto.setCity(entity.getCity());
        dto.setState(entity.getState());
        dto.setCountry(entity.getCountry());
        dto.setZipCode(entity.getZipCode());
        dto.setPhoneNumber(entity.getPhoneNumber());

        if (entity.getUserId() != null) {

            dto.setUserId(entity.getUserId());
        }

        return dto;
    }

    public static void updateAddress(Address existing, AddressRequestDto requestDto, Integer userId) {
        if (existing == null || requestDto == null)
            return;

        existing.setCity(requestDto.getCity());
        existing.setCountry(requestDto.getCountry());
        existing.setState(requestDto.getState());
        existing.setStreet(requestDto.getStreet());
        existing.setZipCode(requestDto.getZipCode());
        existing.setPhoneNumber(requestDto.getPhoneNumber());
        existing.setUserId(userId);
    }
}