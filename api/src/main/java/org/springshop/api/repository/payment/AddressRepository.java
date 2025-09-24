package org.springshop.api.repository.payment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springshop.api.model.payment.Address;

@Repository
public interface AddressRepository extends JpaRepository<Address, Integer> {
}
