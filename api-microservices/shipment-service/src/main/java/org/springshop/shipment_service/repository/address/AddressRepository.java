package org.springshop.shipment_service.repository.address;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springshop.shipment_service.model.address.Address;

import java.util.List;

public interface AddressRepository extends JpaRepository<Address, Integer> {
    List<Address> findAllByUserId(Integer userId);
}