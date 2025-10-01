package org.springshop.api.service.order;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springshop.api.dto.order.CartRequestDto;
import org.springshop.api.dto.order.CartResponseDto;
import org.springshop.api.dto.order.CartItemResponseDto;
import org.springshop.api.mapper.order.CartMapper;
import org.springshop.api.model.order.Cart;
import org.springshop.api.model.user.User;
import org.springshop.api.repository.order.CartItemRepository;
import org.springshop.api.repository.order.CartRepository;
import org.springshop.api.repository.user.UserRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
@Transactional
public class CartService {

        private final CartRepository cartRepository;
        private final CartItemRepository cartItemRepository;
        private final UserRepository userRepository;

        public CartService(CartRepository cartRepository, UserRepository userRepository, CartItemRepository cartItemRepository) {
                this.cartRepository = cartRepository;
                this.userRepository = userRepository;
                this.cartItemRepository = cartItemRepository;
        }

        public CartResponseDto createCart(CartRequestDto dto) {
                User user = findUserOrThrow(dto.getUserId());

                Cart cart = CartMapper.toEntity(dto, user, 0.0);
                cart = cartRepository.save(cart);

                return CartMapper.toResponseDto(cart);
        }

        public List<CartResponseDto> getAllCarts() {
                return cartRepository.findAll().stream()
                                .map(CartMapper::toResponseDto)
                                .collect(Collectors.toList());
        }

        public Optional<CartResponseDto> getCartById(Integer id) {
                return cartRepository.findById(id)
                                .map(CartMapper::toResponseDto);
        }

        public CartResponseDto updateCart(Integer id, CartRequestDto dto) {
                Cart cart = findCartOrThrow(id);

                User user = findUserOrThrow(dto.getUserId());
                CartMapper.updateCart(cart, user);
                cart = cartRepository.save(cart);
                return CartMapper.toResponseDto(cart);
        }

        public void deleteCart(Integer id) {
                Cart cart = findCartOrThrow(id);
                cartRepository.delete(cart);
        }
    public void clearCart(Integer cartId) {
        Cart cart = findCartOrThrow(cartId);
        cartItemRepository.deleteAllByCartId(cartId);
        cart.getItems().clear();
        cart.setTotalAmount(0.0);

        cartRepository.save(cart); 
    }

        public List<CartItemResponseDto> getItemsInCart(Integer cartId) {

                Cart cart = findCartOrThrow(cartId); 

                return cart.getItems().stream()
                                .map(CartMapper::toResponseDto)
                                .collect(Collectors.toList());
        }

        @Transactional(readOnly = true)
        public double calculateCartTotals(Integer cartId) {
                Cart cart = findCartOrThrow(cartId);
                return cart.getItems().stream()
                        
                                .filter(item -> item.getProduct() != null)
                                .mapToDouble(item -> {
                                        double price = item.getProduct().getPrice();
                                        int quantity = item.getQuantity();
                                        return price * quantity;
                                })
                                .sum();
        }

        private User findUserOrThrow(Integer userId) {
                return userRepository.findById(userId)
                                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));
        }

        public Cart findCartOrThrow(Integer cartId) {
                return cartRepository.findById(cartId)
                                .orElseThrow(() -> new EntityNotFoundException("Cart not found with id: " + cartId));
        }
}