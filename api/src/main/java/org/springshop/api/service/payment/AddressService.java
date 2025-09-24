package org.springshop.api.service.payment;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springshop.api.dto.payment.AddressRequestDto;
import org.springshop.api.dto.payment.AddressResponseDto;
import org.springshop.api.mapper.payment.AddressMapper;
import org.springshop.api.model.payment.Address;
import org.springshop.api.model.user.User;
import org.springshop.api.repository.payment.AddressRepository;
import org.springshop.api.repository.user.UserRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class AddressService {
    AddressRepository addressRepository;
    UserRepository userRepository;
    public AddressService(AddressRepository addressRepository, UserRepository userRepository) {
        this.addressRepository = addressRepository;
        this.userRepository = userRepository;
    }
    public AddressResponseDto createAddress(AddressRequestDto requestDto) {
        User user = userRepository.findById(requestDto.getUserId()).orElseThrow(() -> new EntityNotFoundException("User not found with id: " + requestDto.getUserId()));
        Address savedAddress = addressRepository.save(AddressMapper.toEntity(requestDto, user));
        return AddressMapper.toDto(savedAddress);
    }
    public List<AddressResponseDto> getAllAddresses() {
        return addressRepository.findAll().stream().map(AddressMapper::toDto).collect(Collectors.toList());
    }
    public AddressResponseDto getAddressById(Integer id) {
        Address address = addressRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Address not found with id: " + id));
        return AddressMapper.toDto(address);
    }
    public AddressResponseDto updateAddressById(Integer id, AddressRequestDto requestDto) {
        User user = userRepository.findById(requestDto.getUserId()).orElseThrow(() -> new EntityNotFoundException("User not found with id: " + requestDto.getUserId()));
        Address address = addressRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Address not found with id: " + id));
        AddressMapper.updateEntity(address, requestDto, user);
        return AddressMapper.toDto(address);
    }
    public void deleteAddressById(Integer id) {
        if(!addressRepository.existsById(id)) {
            throw new EntityNotFoundException("Address not found with id: " + id);
        }
        addressRepository.deleteById(id);
    }
}
