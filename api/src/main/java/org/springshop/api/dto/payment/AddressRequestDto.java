// AddressRequestDto.java (CORRECTO)
package org.springshop.api.dto.payment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddressRequestDto {

    @NotBlank
    private String street;

    @NotBlank
    private String city;

    @NotBlank
    private String state;

    @NotBlank
    private String country;

    @NotBlank
    private String zipCode;

    private String phoneNumber; // opcional (si es requerido, debería ser @NotBlank)

    @NotNull // Es correcto que el ID del usuario sea obligatorio
    private Integer userId; 
}