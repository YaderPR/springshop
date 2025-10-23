package org.springshop.order_service.client;

import java.util.Optional;

import org.springframework.stereotype.Component;
import org.springshop.order_service.model.cart.Cart;

@Component
public abstract class CartClient {
    public abstract Optional<Cart> findById(Integer cartId);
    public abstract Void clearCart(Integer cartId);
}
