package org.springshop.shipment_service.dto.address;

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
    private Integer userId;
}