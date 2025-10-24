package org.springshop.order_service.service.order;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springshop.order_service.dto.order.OrderRequestDto;
import org.springshop.order_service.dto.order.OrderResponseDto;
import org.springshop.order_service.dto.order.OrderUpdateStatus;
import org.springshop.order_service.client.AddressClient;
import org.springshop.order_service.client.CartClient;
import org.springshop.order_service.client.ProductClient;
import org.springshop.order_service.client.ShipmentClient;
import org.springshop.order_service.client.UserClient;
import org.springshop.order_service.controller.exception.StockException;
import org.springshop.order_service.dto.order.OrderItemRequestDto;
import org.springshop.order_service.dto.order.OrderItemResponseDto;
import org.springshop.order_service.mapper.order.OrderMapper;
import org.springshop.order_service.model.cart.Cart;
import org.springshop.order_service.model.cart.CartItem;
import org.springshop.order_service.model.order.Order;
import org.springshop.order_service.model.order.OrderItem;
import org.springshop.order_service.model.order.OrderStatus;
import org.springshop.order_service.model.address.Address;
import org.springshop.order_service.model.product.Product;
import org.springshop.order_service.model.shipment.Shipment;
import org.springshop.order_service.model.user.User;
import org.springshop.order_service.repository.order.OrderRepository;

import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.RollbackException;

@Service
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartClient cartClient;
    private final OrderItemService orderItemService;
    private final UserClient userClient;
    private final AddressClient addressClient;
    private final ProductClient productClient;
    private final ShipmentClient shipmentClient;

    public OrderService(OrderRepository orderRepository, CartClient cartClient,
            OrderItemService orderItemService, UserClient userClient,
            AddressClient addressClient, ProductClient productClient, ShipmentClient shipmentClient) {
        this.orderRepository = orderRepository;
        this.cartClient = cartClient;
        this.orderItemService = orderItemService;
        this.userClient = userClient;
        this.addressClient = addressClient;
        this.productClient = productClient;
        this.shipmentClient = shipmentClient;
    }

    public Order createOrderFromCart(Integer cartId, Integer userId, Integer addressId) {

        // 1. OBTENER ENTIDADES NECESARIAS
        Cart cart = findCartOrThrow(cartId);
        User user = findUserOrThrow(userId);
        if (cart.getItems().isEmpty()) {
            throw new IllegalArgumentException("Cannot create an order from an empty cart.");
        }

        // 2. CREAR Y PERSISTIR LA ENTIDAD ORDEN INICIAL
        Order order = new Order();
        order.setUserId(user.getId());
        order.setAddressId(addressId);
        order.setStatus(OrderStatus.PENDING);
        order.setTotalAmount(0.0);

        Order savedOrder = orderRepository.save(order);

        double calculatedTotal = 0.0;

        // 3. TRANSFERENCIA DE ITEMS Y RESERVA DE STOCK
        for (CartItem cartItem : cart.getItems()) {
            Integer productId = cartItem.getProductId();
            Product product = findProductOrThrow(productId);
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

        return savedOrder;
    }

    @Transactional
    public void rollbackFailedOrder(Integer orderId) {

        // 1. OBTENER Y VALIDAR LA ORDEN PROVISIONAL
        Order order = findOrderOrThrow(orderId);

        if (order.getStatus() != OrderStatus.PENDING && order.getStatus() != OrderStatus.FAILED) {
            throw new RollbackException(
                    "No se puede revertir la orden " + orderId + ". Estado actual: " + order.getStatus());
        }

        // 2. REVERTIR EL STOCK RESERVADO (La acción crítica)
        List<OrderItemResponseDto> orderItems = orderItemService.getOrderItemsByOrderId(orderId);

        for (OrderItemResponseDto item : orderItems) {
            try {
                // Revertir el stock añadiendo la cantidad (PATCH con cantidad positiva)
                productClient.updateStock(item.getProductId(), item.getQuantity());

            } catch (Exception e) {
                // Manejo de CRÍTICO: Fallo en la reversión de stock.
                System.err.println("CRÍTICO: Fallo al revertir stock para producto " + item.getProductId() + ".");
                // Loguear el error y continuar.
            }
        }

        // 3. ELIMINAR ITEMS DE ORDEN ASOCIADOS
        orderItemService.deleteItemsByOrderId(orderId);

        // 4. ELIMINAR EL REGISTRO DE LA ORDEN
        orderRepository.delete(order);

        // *ELIMINADO*: Ya no se necesita restaurar el carrito, porque nunca se limpió.

        System.out.println("Rollback completado para la orden: " + orderId);
    }

    public OrderResponseDto createOrder(OrderRequestDto requestDto) {
        User user = findUserOrThrow(requestDto.getUserId());
        Address address = findAddressOrThrow(requestDto.getAddressId());

        Order order = OrderMapper.toEntity(requestDto, address.getId(), user.getId());
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
        OrderMapper.updateOrder(order, requestDto, address.getId(), user.getId());
        return OrderMapper.toResponseDto(orderRepository.save(order));
    }

    @Transactional
    public OrderResponseDto updateOrderStatus(Integer id, OrderUpdateStatus updatedStatus) {
        Order order = findOrderOrThrow(id);
        order.setStatus(updatedStatus.getStatus());
        return OrderMapper.toResponseDto(order);
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
        return userClient.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));
    }

    private Address findAddressOrThrow(Integer addressId) {
        return addressClient.findById(addressId)
                .orElseThrow(() -> new EntityNotFoundException("address not found with id: " + addressId));
    }

    public Order findOrderOrThrow(Integer orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found with id: " + orderId));
    }

    public Cart findCartOrThrow(Integer cartId) {
        return cartClient.findById(cartId)
                .orElseThrow(() -> new EntityNotFoundException("Cart not found with id: " + cartId));
    }

    public Product findProductOrThrow(Integer productId) {
        return productClient.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + productId));
    }

    public Shipment findShipmentOrThrow(Integer shipmentId) {
        return shipmentClient.findById(shipmentId)
                .orElseThrow(() -> new EntityNotFoundException("Shipment not found with id: " + shipmentId));
    }
}