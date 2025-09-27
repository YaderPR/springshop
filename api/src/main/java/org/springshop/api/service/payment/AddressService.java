package org.springshop.api.service.payment;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Importante
import org.springshop.api.dto.payment.AddressRequestDto;
import org.springshop.api.dto.payment.AddressResponseDto;
import org.springshop.api.mapper.payment.AddressMapper;
import org.springshop.api.model.payment.Address;
import org.springshop.api.model.user.User;
import org.springshop.api.repository.payment.AddressRepository;
import org.springshop.api.repository.user.UserRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
@Transactional // Aplicamos transaccionalidad a nivel de clase
public class AddressService {
    
    private final AddressRepository addressRepository;
    private final UserRepository userRepository;
    
    // Inyección por constructor (ya es buena práctica)
    public AddressService(AddressRepository addressRepository, UserRepository userRepository) {
        this.addressRepository = addressRepository;
        this.userRepository = userRepository;
    }

    // -------------------- LECTURA (READ) Y BÚSQUEDA --------------------

    /**
     * Obtiene todas las direcciones.
     */
    @Transactional(readOnly = true) // Solo lectura
    public List<AddressResponseDto> getAllAddresses() {
        // CONSISTENCIA: Usamos toResponseDTO si el mapper tiene la convención
        return addressRepository.findAll().stream()
            .map(AddressMapper::toResponseDto) 
            .collect(Collectors.toList());
    }

    /**
     * Obtiene una dirección por ID.
     */
    @Transactional(readOnly = true)
    public Optional<AddressResponseDto> getAddressById(Integer id) {
        // Mantenemos el Optional para delegar la decisión de 404 al controlador
        return addressRepository.findById(id)
            .map(AddressMapper::toResponseDto); 
    }
    
    /**
     * Obtiene todas las direcciones asociadas a un usuario específico.
     * (Asumimos que el repositorio lo implementará: List<Address> findAllByUserId(Integer userId))
     */
    @Transactional(readOnly = true)
    public List<AddressResponseDto> getAddressesByUserId(Integer userId) {
        // Verifica que el usuario exista (o lanza 404 si es necesario validar la existencia)
        findUserOrThrow(userId); 
        
        return addressRepository.findAllByUserId(userId).stream()
            .map(AddressMapper::toResponseDto)
            .collect(Collectors.toList());
    }

    // -------------------- ESCRITURA (CREATE, UPDATE, DELETE) --------------------

    /**
     * Crea una nueva dirección asociada a un usuario.
     */
    public AddressResponseDto createAddress(AddressRequestDto requestDto) {
        // Centralizamos la búsqueda del usuario
        User user = findUserOrThrow(requestDto.getUserId());
        
        Address newAddress = AddressMapper.toEntity(requestDto, user);
        Address savedAddress = addressRepository.save(newAddress);
        
        return AddressMapper.toResponseDto(savedAddress);
    }

    /**
     * Actualiza una dirección existente.
     */
    public AddressResponseDto updateAddress(Integer id, AddressRequestDto requestDto) { // CONSISTENCIA: Renombramos
        // 1. Centralizamos la búsqueda del recurso existente
        Address existingAddress = findAddressOrThrow(id);
        
        // 2. Centralizamos la búsqueda del usuario (asegurando que exista)
        User user = findUserOrThrow(requestDto.getUserId());
        
        // 3. Delegamos el mapeo al mapper (usando la convención updateAddress)
        AddressMapper.updateAddress(existingAddress, requestDto, user);
        
        // Guardamos y devolvemos el DTO actualizado
        addressRepository.save(existingAddress); // El mapper actualiza la referencia
        return AddressMapper.toResponseDto(existingAddress);
    }

    /**
     * Elimina una dirección por ID.
     */
    public void deleteAddress(Integer id) { // CONSISTENCIA: Renombramos
        // Optimizamos la eliminación: buscar y eliminar
        Address address = findAddressOrThrow(id);
        addressRepository.delete(address);
    }

    // -------------------- MÉTODOS AUXILIARES DE BÚSQUEDA --------------------

    /**
     * Busca una Address por ID o lanza EntityNotFoundException.
     */
    public Address findAddressOrThrow(Integer addressId) {
        return addressRepository.findById(addressId)
                .orElseThrow(() -> new EntityNotFoundException("Address not found with id: " + addressId));
    }
    
    /**
     * Busca un User por ID o lanza EntityNotFoundException.
     */
    private User findUserOrThrow(Integer userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));
    }
}