package org.springshop.api.repository.payment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springshop.api.model.payment.Address;

import java.util.List;

public interface AddressRepository extends JpaRepository<Address, Integer> {
    
    /**
     * Encuentra todas las direcciones asociadas a un ID de usuario específico.
     * Esto soporta el endpoint de relación GET /api/users/{userId}/addresses.
     */
    List<Address> findAllByUserId(Integer userId);
}