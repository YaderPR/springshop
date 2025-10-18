package org.springshop.cart_service.repository.cart;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springshop.cart_service.model.cart.CartItem;

public interface CartItemRepository extends JpaRepository<CartItem, Integer> {
    public List<CartItem> findAllByCartId(Integer id);
    Optional<CartItem> findByCartIdAndProductId(Integer cartId, Integer productId);
    @Modifying
    @Query("DELETE FROM CartItem ci WHERE ci.cart.id = :cartId")
    void deleteAllByCartId(Integer cartId);

}
