package com.ecart.user.service;

import com.ecart.user.dto.AddressDto;
import com.ecart.user.dto.CreateUserDto;
import com.ecart.user.dto.UserDto;
import com.ecart.user.entity.Address;
import com.ecart.user.entity.User;
import com.ecart.user.exception.DuplicateResourceException;
import com.ecart.user.exception.ResourceNotFoundException;
import com.ecart.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public UserDto createUser(CreateUserDto createUserDto) {
        if (userRepository.findByEmail(createUserDto.getEmail()).isPresent()) {
            throw new DuplicateResourceException("User with email already exists");
        }
        if (userRepository.findByPhoneNumber(createUserDto.getPhoneNumber()).isPresent()) {
            throw new DuplicateResourceException("User with phone number already exists");
        }

        User user = new User();
        user.setEmail(createUserDto.getEmail());
        user.setFirstName(createUserDto.getFirstName());
        user.setLastName(createUserDto.getLastName());
        user.setPhoneNumber(createUserDto.getPhoneNumber());
        user.setAddress(convertToAddressEntity(createUserDto.getAddress()));

        User savedUser = userRepository.save(user);
        return convertToDto(savedUser);
    }

    @Override
    public UserDto getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        return convertToDto(user);
    }

    @Override
    public UserDto getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
        return convertToDto(user);
    }

    @Override
    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto updateUser(Long id, CreateUserDto updateUserDto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        user.setEmail(updateUserDto.getEmail());
        user.setFirstName(updateUserDto.getFirstName());
        user.setLastName(updateUserDto.getLastName());
        user.setPhoneNumber(updateUserDto.getPhoneNumber());
        user.setAddress(convertToAddressEntity(updateUserDto.getAddress()));

        User updatedUser = userRepository.save(user);
        return convertToDto(updatedUser);
    }

    @Override
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        userRepository.delete(user);
    }

    private UserDto convertToDto(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setPhoneNumber(user.getPhoneNumber());
        dto.setAddress(convertToAddressDto(user.getAddress()));
        dto.setCreatedAt(user.getCreatedAt());
        dto.setUpdatedAt(user.getUpdatedAt());
        return dto;
    }

    private AddressDto convertToAddressDto(Address address) {
        if (address == null) {
            return null;
        }
        return new AddressDto(address.getStreet(), address.getCity(), address.getState(), address.getZipCode());
    }

    private Address convertToAddressEntity(AddressDto addressDto) {
        if (addressDto == null) {
            return null;
        }
        return new Address(addressDto.getStreet(), addressDto.getCity(), addressDto.getState(), addressDto.getZipCode());
    }
}