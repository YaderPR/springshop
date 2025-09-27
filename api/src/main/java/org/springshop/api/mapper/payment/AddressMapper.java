package org.springshop.api.mapper.payment;

import org.springshop.api.dto.payment.AddressRequestDto;
import org.springshop.api.dto.payment.AddressResponseDto;
import org.springshop.api.model.payment.Address;
import org.springshop.api.model.user.User;

public class AddressMapper {

    /**
     * Convierte de RequestDto -> Entidad Address.
     */
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

    /**
     * Convierte de Entidad -> ResponseDto.
     */
    // CONVENCIÓN: Renombramos toDto a toResponseDto
    public static AddressResponseDto toResponseDto(Address entity) {
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
            // AÑADIDO: Incluimos el ID del usuario, esencial para la consistencia y navegación
            dto.setUserId(entity.getUser().getId()); 
            dto.setUsername(entity.getUser().getUsername());
        }

        return dto;
    }
    
    /**
     * Actualiza una entidad Address existente con los datos de un RequestDto.
     */
    // CONVENCIÓN: Renombramos updateEntity a updateAddress
    public static void updateAddress(Address existing, AddressRequestDto requestDto, User user) {
        if (existing == null || requestDto == null) return;
        
        existing.setCity(requestDto.getCity());
        existing.setCountry(requestDto.getCountry());
        existing.setState(requestDto.getState());
        existing.setStreet(requestDto.getStreet());
        existing.setZipCode(requestDto.getZipCode());
        existing.setPhoneNumber(requestDto.getPhoneNumber());
        // CORRECCIÓN: Se repite `setCountry` en el código original, lo eliminamos
        // existing.setCountry(requestDto.getCountry()); 
        
        // Es clave permitir actualizar el usuario si se mueve la dirección,
        // aunque en muchos casos de negocio real esto podría estar prohibido.
        existing.setUser(user); 
    }
}