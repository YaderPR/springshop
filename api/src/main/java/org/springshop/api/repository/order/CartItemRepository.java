package org.springshop.api.repository.order;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springshop.api.model.order.CartItem;

public interface CartItemRepository extends JpaRepository<CartItem, Integer> {
    public List<CartItem> findAllByCartId(Integer id);
    Optional<CartItem> findByCartIdAndProductId(Integer cartId, Integer productId);

}
