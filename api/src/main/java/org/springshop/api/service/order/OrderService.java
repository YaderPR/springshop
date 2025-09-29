package org.springshop.api.service.order;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springshop.api.dto.order.OrderRequestDto;
import org.springshop.api.dto.order.OrderResponseDto;
import org.springshop.api.controller.exception.StockException;
import org.springshop.api.dto.order.OrderItemRequestDto;
import org.springshop.api.mapper.order.OrderMapper;
import org.springshop.api.model.order.Cart;
import org.springshop.api.model.order.CartItem;
import org.springshop.api.model.order.Order;
import org.springshop.api.model.order.OrderStatus;
import org.springshop.api.model.payment.Address;
import org.springshop.api.model.product.Product;
import org.springshop.api.model.user.User;
import org.springshop.api.repository.order.OrderRepository;
import org.springshop.api.repository.user.UserRepository;
import org.springshop.api.service.payment.AddressService;

import jakarta.persistence.EntityNotFoundException;

@Service
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartService cartService;
    private final OrderItemService orderItemService;
    private final UserRepository userRepository;
    private final AddressService addressService;

    public OrderService(OrderRepository orderRepository, CartService cartService,
            OrderItemService orderItemService, UserRepository userRepository,
            AddressService addressService) {
        this.orderRepository = orderRepository;
        this.cartService = cartService;
        this.orderItemService = orderItemService;
        this.userRepository = userRepository;
        this.addressService = addressService;
    }

    public Order createOrderFromCart(Integer cartId, Integer userId, Integer shippingAddressId) {

        // 1. OBTENER ENTIDADES NECESARIAS
        Cart cart = cartService.findCartOrThrow(cartId);
        User user = findUserOrThrow(userId);
        Address shippingAddress = findAddressOrThrow(shippingAddressId);

        if (cart.getItems().isEmpty()) {
            throw new IllegalArgumentException("Cannot create an order from an empty cart.");
        }

        // 2. CREAR Y PERSISTIR LA ENTIDAD ORDEN INICIAL
        Order order = new Order();
        order.setUser(user);
        order.setShippingAddress(shippingAddress);
        order.setStatus(OrderStatus.PENDING);
        order.setTotalAmount(0.0);

        Order savedOrder = orderRepository.save(order);

        double calculatedTotal = 0.0;

        // 3. TRANSFERENCIA DE ITEMS Y RESERVA DE STOCK
        for (CartItem cartItem : cart.getItems()) {
            Product product = cartItem.getProduct();

            // 3A. VALIDACIÓN FINAL DE STOCK
            if (product.getStock() < cartItem.getQuantity()) {
                throw new StockException(
                        "Stock insuficiente para el producto: " + product.getName() +
                                ". Stock disponible: " + product.getStock());
            }

            // 3B. CREACIÓN DEL ORDER ITEM y REDUCCIÓN DE STOCK (Llamada al ItemService)
            OrderItemRequestDto itemDto = new OrderItemRequestDto(
                    product.getId(),
                    cartItem.getQuantity());

            orderItemService.createOrderItem(savedOrder, itemDto);

            calculatedTotal += product.getPrice() * cartItem.getQuantity();
        }

        savedOrder.setTotalAmount(calculatedTotal);
        cartService.clearCart(cartId);

        return savedOrder;
    }
    public OrderResponseDto createOrder(OrderRequestDto requestDto) {
        User user = findUserOrThrow(requestDto.getUserId());
        Address address = findAddressOrThrow(requestDto.getAddressId());

        Order order = OrderMapper.toEntity(requestDto, address, user);
        order.setTotalAmount(0.0);

        return OrderMapper.toResponseDto(orderRepository.save(order));
    }

    public List<OrderResponseDto> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(OrderMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    public Optional<OrderResponseDto> getOrderById(Integer id) {
        return orderRepository.findById(id)
                .map(OrderMapper::toResponseDto);
    }

    public OrderResponseDto updateOrder(Integer id, OrderRequestDto requestDto) {
        Order order = findOrderOrThrow(id);
        User user = findUserOrThrow(requestDto.getUserId());
        Address address = findAddressOrThrow(requestDto.getAddressId());
        OrderMapper.updateOrder(order, requestDto, address, user);
        return OrderMapper.toResponseDto(orderRepository.save(order));
    }
    public void deleteOrder(Integer id) {
        Order order = findOrderOrThrow(id);
        if (order.getStatus() != OrderStatus.PENDING) {
            // throw new IllegalStateException("Cannot delete an order that is not
            // pending.");
            // Si se permite la eliminación, aquí va a ir la lógica de reposición de stock.
        }

        orderRepository.delete(order);
    }
    public double calculateOrderTotals(Integer orderId) {
        Order order = findOrderOrThrow(orderId);
        return order.getItems().stream()
                .mapToDouble(item -> item.getPrice() * item.getQuantity())
                .sum();
    }

    @Transactional
    public void updateOrderTotal(Integer orderId) {
        Order order = findOrderOrThrow(orderId);
        double newTotal = calculateOrderTotals(orderId);

        if (order.getTotalAmount() != newTotal) {
            order.setTotalAmount(newTotal);
            orderRepository.save(order);
        }
    }
    private User findUserOrThrow(Integer userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));
    }

    private Address findAddressOrThrow(Integer addressId) {
        return addressService.findAddressOrThrow(addressId);
    }

    public Order findOrderOrThrow(Integer orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found with id: " + orderId));
    }
}