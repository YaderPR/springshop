package org.springshop.api.service.order;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springshop.api.dto.order.CartItemRequestDto;
import org.springshop.api.dto.order.CartItemResponseDto;
import org.springshop.api.dto.order.CartRequestDto;
import org.springshop.api.dto.order.CartResponseDto;
import org.springshop.api.mapper.order.CartMapper;
import org.springshop.api.model.order.Cart;
import org.springshop.api.model.order.CartItem;
import org.springshop.api.model.product.Product;
import org.springshop.api.model.user.User;
import org.springshop.api.repository.order.CartItemRepository;
import org.springshop.api.repository.order.CartRepository;
import org.springshop.api.repository.product.ProductRepository;
import org.springshop.api.repository.user.UserRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class CartService {
    CartRepository cartRepository;
    CartItemRepository cartItemRepository;
    UserRepository userRepository;
    ProductRepository productRepository;

    public CartService(CartRepository cartRepository, CartItemRepository cartItemRepository,
            UserRepository userRepository, ProductRepository productRepository) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.userRepository = userRepository;
    }

    public CartResponseDto createCart(CartRequestDto dto) {
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + dto.getUserId()));

        Set<CartItem> items = dto.getItems() != null
                ? dto.getItems().stream()
                        .map(itemDto -> {
                            Product product = productRepository.findById(itemDto.getProductId())
                                    .orElseThrow(() -> new EntityNotFoundException(
                                            "Product not found with id: " + itemDto.getProductId()));
                            return CartMapper.toEntity(itemDto, product);
                        })
                        .collect(Collectors.toSet())
                : null;

        Cart cart = CartMapper.toEntity(dto, user, items);
        cart = cartRepository.save(cart);

        return CartMapper.toResponseDto(cart);
    }
    public List<CartResponseDto> getAllCarts() {
        List<Cart> carts = cartRepository.findAll();
        return carts.stream()
                .map(CartMapper::toResponseDto)
                .collect(Collectors.toList());
    }
    public CartResponseDto getCartById(Integer id) {
        Cart cart = cartRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cart not found with id: " + id));
        return CartMapper.toResponseDto(cart);
    }
    public void deleteCart(Integer id) {
        if (!cartRepository.existsById(id)) {
            throw new EntityNotFoundException("Cart not found with id: " + id);
        }
        cartRepository.deleteById(id);
    }
    public CartResponseDto updateCart(Integer id, CartRequestDto dto) {
        Cart cart = cartRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cart not found with id: " + id));

        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + dto.getUserId()));  

        Set<CartItem> items = dto.getItems() != null
                ? dto.getItems().stream()
                        .map(itemDto -> {
                            Product product = productRepository.findById(itemDto.getProductId())
                                    .orElseThrow(() -> new EntityNotFoundException(
                                            "Product not found with id: " + itemDto.getProductId()));
                            return CartMapper.toEntity(itemDto, product);
                        })
                        .collect(Collectors.toSet())
                : null;

        cart.setUser(user);
        if (items != null) {
            cart.setItems(items);
        }

        cart = cartRepository.save(cart);
        return CartMapper.toResponseDto(cart);
    }
    public CartItemResponseDto addItemToCart(Integer cartId, CartItemRequestDto itemDto) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new EntityNotFoundException("Cart not found with id: " + cartId));

        Product product = productRepository.findById(itemDto.getProductId())
                .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + itemDto.getProductId()));
        CartItem item = CartMapper.toEntity(itemDto, product);
        item.setCart(cart);
        item = cartItemRepository.save(item);
        return CartMapper.toResponseDto(item);
    }
    public void removeItemFromCart(Integer cartId, Integer itemId) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new EntityNotFoundException("Cart not found with id: " + cartId));

        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("CartItem not found with id: " + itemId));

        if (!item.getCart().getId().equals(cart.getId())) {
            throw new IllegalArgumentException("Item does not belong to the specified cart");
        }

        cartItemRepository.deleteById(itemId);
    }
    public List<CartItemResponseDto> getItemsInCart(Integer cartId) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new EntityNotFoundException("Cart not found with id: " + cartId));

        Set<CartItem> items = cart.getItems();
        return items.stream()
                .map(CartMapper::toResponseDto)
                .collect(Collectors.toList());
    }
    public CartItemResponseDto updateItemFromCart(Integer cartId, Integer itemId, CartItemRequestDto itemDto) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new EntityNotFoundException("Cart not found with id: " + cartId));

        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("CartItem not found with id: " + itemId));

        if (!item.getCart().getId().equals(cart.getId())) {
            throw new IllegalArgumentException("Item does not belong to the specified cart");
        }

        Product product = productRepository.findById(itemDto.getProductId())
                .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + itemDto.getProductId()));
        item.setProduct(product);
        item.setQuantity(itemDto.getQuantity());
        item.setPrice(itemDto.getPrice());
        item = cartItemRepository.save(item);
        return CartMapper.toResponseDto(item);
    }
    public void deleteCartItem(Integer cartId, Integer itemId) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new EntityNotFoundException("Cart not found with id: " + cartId));

        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("CartItem not found with id: " + itemId));

        if (!item.getCart().getId().equals(cart.getId())) {
            throw new IllegalArgumentException("Item does not belong to the specified cart");
        }

        cartItemRepository.deleteById(itemId);
    }
    public CartResponseDto updateCartItem(Integer cartId, Integer itemId, CartItemRequestDto itemDto) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new EntityNotFoundException("Cart not found with id: " + cartId));

        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("CartItem not found with id: " + itemId));

        if (!item.getCart().getId().equals(cart.getId())) {
            throw new IllegalArgumentException("Item does not belong to the specified cart");
        }

        Product product = productRepository.findById(itemDto.getProductId())
                .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + itemDto.getProductId()));
        item.setProduct(product);
        item.setQuantity(itemDto.getQuantity());
        item.setPrice(itemDto.getPrice());
        item = cartItemRepository.save(item);
        return CartMapper.toResponseDto(cart);
    }
}
