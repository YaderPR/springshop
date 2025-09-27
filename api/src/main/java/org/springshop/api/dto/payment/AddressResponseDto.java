// AddressResponseDto.java (AJUSTADO)
package org.springshop.api.dto.payment;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddressResponseDto {

    private Integer id;

    private String street;
    private String city;
    private String state;
    private String country;
    private String zipCode;
    private String phoneNumber;

    // AÑADIDO: ID del usuario para integridad de la API
    private Integer userId;

    // Información mínima del usuario dueño de la dirección
    private String username;
}