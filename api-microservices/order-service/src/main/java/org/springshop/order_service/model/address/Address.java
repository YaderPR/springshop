package org.springshop.order_service.model.address;

import lombok.Data;

@Data
public class Address {

    private Integer id;

    private String street;
    private String city;
    private String state;
    private String country;
    private String zipCode;
    private String phoneNumber;
    private Integer userId;
    private String username;
}
