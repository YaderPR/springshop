package org.springshop.cart_service.service.cart;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springshop.cart_service.dto.cart.CartRequestDto;
import org.springshop.cart_service.dto.cart.CartResponseDto;
import org.springshop.cart_service.dto.cart.CartItemResponseDto;
import org.springshop.cart_service.mapper.cart.CartMapper;
import org.springshop.cart_service.model.cart.Cart;
import org.springshop.cart_service.model.user.User;
import org.springshop.cart_service.repository.cart.CartItemRepository;
import org.springshop.cart_service.repository.cart.CartRepository;
import org.springshop.cart_service.client.UserClient;

import jakarta.persistence.EntityNotFoundException;

@Service
@Transactional
public class CartService {

        private final CartRepository cartRepository;
        private final CartItemRepository cartItemRepository;
        private final UserClient userClient;

        public CartService(CartRepository cartRepository, UserClient userClient,
                        CartItemRepository cartItemRepository) {
                this.cartRepository = cartRepository;
                this.userClient = userClient;
                this.cartItemRepository = cartItemRepository;
        }

        public CartResponseDto createCart(CartRequestDto dto) {
                User user = findUserOrThrow(dto.getUserId());

                Cart cart = CartMapper.toEntity(dto, user.getId(), 0.0);
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
                CartMapper.updateCart(cart, user.getId());
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

                                .filter(item -> item.getProductId() != null)
                                .mapToDouble(item -> {
                                        double price = item.getPrice();
                                        int quantity = item.getQuantity();
                                        return price * quantity;
                                })
                                .sum();
        }

        private User findUserOrThrow(Integer userId) {
                return userClient.findById(userId)
                                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));
        }

        public Cart findCartOrThrow(Integer cartId) {
                return cartRepository.findById(cartId)
                                .orElseThrow(() -> new EntityNotFoundException("Cart not found with id: " + cartId));
        }
}