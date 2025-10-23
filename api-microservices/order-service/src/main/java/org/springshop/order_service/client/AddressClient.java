package org.springshop.order_service.client;

import java.util.Optional;

import org.springframework.stereotype.Component;
import org.springshop.order_service.model.address.Address;

@Component
public abstract class AddressClient {
    public abstract Optional<Address> findById(Integer addressId);
}
