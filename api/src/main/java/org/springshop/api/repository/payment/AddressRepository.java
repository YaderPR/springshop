package org.springshop.api.repository.payment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springshop.api.model.payment.Address;

import java.util.List;

public interface AddressRepository extends JpaRepository<Address, Integer> {
    List<Address> findAllByUserId(Integer userId);
}