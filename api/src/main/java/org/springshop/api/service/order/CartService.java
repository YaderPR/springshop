// Archivo: org.springshop.api.service.order.CartService.java

package org.springshop.api.service.order;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springshop.api.dto.order.CartRequestDto;
import org.springshop.api.dto.order.CartResponseDto;
import org.springshop.api.dto.order.CartItemResponseDto; // Necesario para getItemsInCart
import org.springshop.api.mapper.order.CartMapper;
import org.springshop.api.model.order.Cart;
import org.springshop.api.model.user.User;
import org.springshop.api.repository.order.CartRepository;
import org.springshop.api.repository.user.UserRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
@Transactional // Aplicamos @Transactional a nivel de clase para consistencia
public class CartService {

        private final CartRepository cartRepository;
        private final UserRepository userRepository;

        // Eliminamos CartItemRepository y ProductRepository de aquí.

        public CartService(CartRepository cartRepository, UserRepository userRepository) {
                this.cartRepository = cartRepository;
                this.userRepository = userRepository;
        }

        // -------------------- CRUD DE CART --------------------

        public CartResponseDto createCart(CartRequestDto dto) {
                // Mejoramos la legibilidad usando un método auxiliar para buscar al usuario
                User user = findUserOrThrow(dto.getUserId());

                Cart cart = CartMapper.toEntity(dto, user);
                cart = cartRepository.save(cart);

                return CartMapper.toResponseDto(cart);
        }

        public List<CartResponseDto> getAllCarts() {
                return cartRepository.findAll().stream()
                                .map(CartMapper::toResponseDto)
                                .collect(Collectors.toList());
        }

        public Optional<CartResponseDto> getCartById(Integer id) {
                // Devolvemos el Optional directamente, dejando el mapeo al consumidor o al
                // controlador
                return cartRepository.findById(id)
                                .map(CartMapper::toResponseDto);
        }

        public CartResponseDto updateCart(Integer id, CartRequestDto dto) {
                Cart cart = findCartOrThrow(id);

                // Se mantiene la validación de la existencia del usuario
                User user = findUserOrThrow(dto.getUserId());

                // Asumiendo que CartMapper.updateCart actualiza la entidad existente
                CartMapper.updateCart(cart, user);

                // No es necesario llamar a save() si @Transactional está presente
                // y el mapper solo actualiza la entidad.
                // Pero lo mantenemos si el mapper realiza lógica que requiera un save explícito
                cart = cartRepository.save(cart);
                return CartMapper.toResponseDto(cart);
        }

        public void deleteCart(Integer id) {
                // Optimización: findById y deleteById son a menudo más limpios que existsById +
                // deleteById
                Cart cart = findCartOrThrow(id);
                cartRepository.delete(cart);
        }

        // -------------------- OPERACIONES CON LA COLECCIÓN DE ÍTEMS
        // --------------------

        public List<CartItemResponseDto> getItemsInCart(Integer cartId) {
                // En lugar de obtener el carrito y luego su Set<Items>,
                // preferimos usar un Query Method en CartItemRepository para mayor eficiencia
                // y para evitar problemas con la carga perezosa (Lazy Loading) si no está
                // configurada.
                // Dejamos la implementación de esta función en el CartItemService (ver más
                // abajo).

                // Opcional: Si mantienes la lógica aquí, al menos valida la existencia del
                // carrito.
                // Pero moverla al servicio especializado es mejor.

                Cart cart = findCartOrThrow(cartId); // Para asegurar que el carrito existe

                // Si la relación es LAZY, se disparará una consulta N+1.
                // Si es EAGER, puede ser ineficiente.
                // La mejor práctica es usar un JOIN FETCH en un Query Method especializado.
                // Asumiendo que la relación es EAGER O que el repositorio de Ítems maneja la
                // consulta:
                return cart.getItems().stream()
                                .map(CartMapper::toResponseDto)
                                .collect(Collectors.toList());
        }

        @Transactional(readOnly = true) // Es solo una operación de lectura
        public double calculateCartTotals(Integer cartId) {
                Cart cart = findCartOrThrow(cartId);

                // Usar la colección de ítems cargada por Hibernate
                return cart.getItems().stream()
                                // Aseguramos que los ítems y productos están presentes y no son nulos
                                .filter(item -> item.getProduct() != null)
                                .mapToDouble(item -> {
                                        double price = item.getProduct().getPrice();
                                        int quantity = item.getQuantity();
                                        return price * quantity;
                                })
                                .sum();
        }

        // -------------------- MÉTODOS AUXILIARES --------------------

        private User findUserOrThrow(Integer userId) {
                return userRepository.findById(userId)
                                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));
        }

        // Método auxiliar para no duplicar la lógica de búsqueda de Carrito
        public Cart findCartOrThrow(Integer cartId) {
                return cartRepository.findById(cartId)
                                .orElseThrow(() -> new EntityNotFoundException("Cart not found with id: " + cartId));
        }
}