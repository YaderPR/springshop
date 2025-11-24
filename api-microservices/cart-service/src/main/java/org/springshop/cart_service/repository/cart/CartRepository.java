package org.springshop.cart_service.repository.cart;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springshop.cart_service.model.cart.Cart;


import java.util.Optional;
import java.util.List;


public interface CartRepository extends JpaRepository<Cart, Integer> {
    Optional<Cart> findTopByUserIdOrderByIdDesc(Integer userId);
    List<Cart> findAllByOrderByIdDesc();
}
