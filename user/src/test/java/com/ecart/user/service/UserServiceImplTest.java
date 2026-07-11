package com.ecart.user.service;

import com.ecart.user.dto.AddressDto;
import com.ecart.user.dto.CreateUserDto;
import com.ecart.user.dto.UserDto;
import com.ecart.user.entity.Address;
import com.ecart.user.entity.User;
import com.ecart.user.exception.DuplicateResourceException;
import com.ecart.user.exception.ResourceNotFoundException;
import com.ecart.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private User testUser;
    private CreateUserDto createUserDto;
    private AddressDto addressDto;

    @BeforeEach
    void setup() {
        addressDto = new AddressDto("123 Main St", "New York", "NY", "10001");
        createUserDto = new CreateUserDto("john.doe@example.com", "John", "Doe", "+1234567890", addressDto);

        Address address = new Address("123 Main St", "New York", "NY", "10001");

        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("john.doe@example.com");
        testUser.setFirstName("John");
        testUser.setLastName("Doe");
        testUser.setPhoneNumber("+1234567890");
        testUser.setAddress(address);
        testUser.setCreatedAt(LocalDateTime.now());
        testUser.setUpdatedAt(LocalDateTime.now());

    }

    @Test
    void createUser_ValidInput_ReturnsUserDto() {
        when(userRepository.findByEmail(createUserDto.getEmail())).thenReturn(Optional.empty());
        when(userRepository.findByPhoneNumber(createUserDto.getPhoneNumber())).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        UserDto result = userService.createUser(createUserDto);
        assertNotNull(result);
        assertEquals(testUser.getId(), result.getId());
        assertEquals(testUser.getEmail(), result.getEmail());
        assertEquals(testUser.getFirstName(), result.getFirstName());
        assertEquals(testUser.getLastName(), result.getLastName());

        verify(userRepository).findByEmail(createUserDto.getEmail());
        verify(userRepository).findByPhoneNumber(createUserDto.getPhoneNumber());
        verify(userRepository).save(any(User.class));

    }

    @Test
    void createUser_DuplicateEmail_ThrowsDuplicateResourceException() {
        when(userRepository.findByEmail(createUserDto.getEmail())).thenReturn(Optional.of(testUser));

        DuplicateResourceException exception = assertThrows(
                DuplicateResourceException.class,
                () -> userService.createUser(createUserDto));

        assertEquals("User with email already exists", exception.getMessage());

        verify(userRepository).findByEmail(createUserDto.getEmail());
        verify(userRepository, never()).findByPhoneNumber(createUserDto.getPhoneNumber());
        verify(userRepository, never()).save(any(User.class));

    }

    @Test
    void createUser_DuplicatePhoneNumber_ThrowsDuplicateResourceException() {
        when(userRepository.findByEmail(createUserDto.getEmail())).thenReturn(Optional.empty());
        when(userRepository.findByPhoneNumber(createUserDto.getPhoneNumber())).thenReturn(Optional.of(testUser));

        DuplicateResourceException exception = assertThrows(
                DuplicateResourceException.class,
                () -> userService.createUser(createUserDto));

        assertEquals("User with phone number already exists", exception.getMessage());

        verify(userRepository).findByEmail(createUserDto.getEmail());
        verify(userRepository).findByPhoneNumber(createUserDto.getPhoneNumber());
        verify(userRepository, never()).save(any(User.class));

    }

    @Test
    void createUser_NullAddress_SuccessfullyCreatesUser() {
        CreateUserDto dtoWithoutAddress = new CreateUserDto(
                "john.doe@example.com",
                "John",
                "Doe",
                "+1234567890",
                null
        );

        User userWithoutAddress = new User();
        userWithoutAddress.setId(1L);
        userWithoutAddress.setEmail("john.doe@example.com");
        userWithoutAddress.setFirstName("John");
        userWithoutAddress.setLastName("Doe");
        userWithoutAddress.setPhoneNumber("+1234567890");
        userWithoutAddress.setAddress(null);

        when(userRepository.findByEmail(dtoWithoutAddress.getEmail())).thenReturn(Optional.empty());
        when(userRepository.findByPhoneNumber(dtoWithoutAddress.getPhoneNumber())).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(userWithoutAddress);

        UserDto result = userService.createUser(dtoWithoutAddress);

        assertNotNull(result);
        assertNull(result.getAddress());
        verify(userRepository).save(any(User.class));


    }

    @Test
    void getUserById_ExistingId_ReturnsUserDto() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        UserDto result = userService.getUserById(1L);
        assertNotNull(result);
        assertEquals(testUser.getId(), result.getId());
        assertEquals(testUser.getEmail(), result.getEmail());

        verify(userRepository).findById(1L);
    }

    @Test
    void getUserById_NonExistingId_ThrowsResourceNotFoundException() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> userService.getUserById(999L)
        );
        assertEquals("User not found with id: 999", exception.getMessage());
        verify(userRepository).findById(999L);

    }

    @Test
    void getUserByEmail_ExistingEmail_ReturnsUserDto() {
        when(userRepository.findByEmail("john.doe@example.com")).thenReturn(Optional.of(testUser));


        UserDto result = userService.getUserByEmail("john.doe@example.com");

        assertNotNull(result);
        assertEquals(testUser.getEmail(), result.getEmail());
        verify(userRepository).findByEmail("john.doe@example.com");
    }


    @Test
    void getUserByEmail_NonExistingEmail_ThrowsResourceNotFoundException() {

        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> userService.getUserByEmail("nonexistent@example.com")
        );

        assertEquals("User not found with email: nonexistent@example.com", exception.getMessage());
        verify(userRepository).findByEmail("nonexistent@example.com");
    }

    @Test
    void getAllUsers_ReturnsUserList() {
        User user2 = new User();
        user2.setId(2L);
        user2.setEmail("jane.doe@example.com");
        user2.setFirstName("Jane");
        user2.setLastName("Doe");

        List<User> userList = Arrays.asList(testUser, user2);
        when(userRepository.findAll()).thenReturn(userList);

        List<UserDto> result = userService.getAllUsers();

        assertEquals(2, result.size());
        assertEquals(testUser.getId(), result.getFirst().getId());
        assertEquals(user2.getId(), result.get(1).getId());

        verify(userRepository).findAll();

    }

    @Test
    void updateUser_ExistingId_ReturnsUpdatedUserDto() {
        CreateUserDto updateDto = new CreateUserDto(
                "john.updated@example.com",
                "John Updated",
                "Doe",
                "+1234567899",
                addressDto
        );

        User updatedUser = new User();
        updatedUser.setId(1L);
        updatedUser.setEmail("john.updated@example.com");
        updatedUser.setFirstName("John Updated");
        updatedUser.setLastName("Doe");
        updatedUser.setPhoneNumber("+1234567899");
        updatedUser.setAddress(new Address("123 Main St", "Los Angeles", "CA", "10001"));

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.findByEmail(updateDto.getEmail())).thenReturn(Optional.empty());
        when(userRepository.findByPhoneNumber(updateDto.getPhoneNumber())).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);

        UserDto result = userService.updateUser(1L, updateDto);

        assertNotNull(result);
        assertEquals(updatedUser.getEmail(), result.getEmail());
        assertEquals(updatedUser.getFirstName(), result.getFirstName());
        assertEquals(updatedUser.getAddress().getCity(), result.getAddress().getCity());
        assertEquals(updatedUser.getAddress().getState(), result.getAddress().getState());
        verify(userRepository).findById(1L);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void updateUser_DuplicateEmail_ThrowsDuplicateResourceException() {
        CreateUserDto updateDto = new CreateUserDto(
                "taken@example.com",
                "John",
                "Doe",
                "+1234567890",
                addressDto
        );
        User anotherUser = new User();
        anotherUser.setId(2L);
        anotherUser.setEmail("taken@example.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.findByEmail(updateDto.getEmail())).thenReturn(Optional.of(anotherUser));

        DuplicateResourceException exception = assertThrows(
                DuplicateResourceException.class,
                () -> userService.updateUser(1L, updateDto)
        );

        assertEquals("User with email already exists", exception.getMessage());
        verify(userRepository).findById(1L);
        verify(userRepository).findByEmail(updateDto.getEmail());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void updateUser_DuplicatePhoneNumber_ThrowsDuplicateResourceException() {
        CreateUserDto updateDto = new CreateUserDto(
                "john.doe@example.com",
                "John",
                "Doe",
                "+1987654321",
                addressDto
        );
        User anotherUser = new User();
        anotherUser.setId(2L);
        anotherUser.setPhoneNumber("+1987654321");

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.findByEmail(updateDto.getEmail())).thenReturn(Optional.of(testUser));
        when(userRepository.findByPhoneNumber(updateDto.getPhoneNumber())).thenReturn(Optional.of(anotherUser));

        DuplicateResourceException exception = assertThrows(
                DuplicateResourceException.class,
                () -> userService.updateUser(1L, updateDto)
        );

        assertEquals("User with phone number already exists", exception.getMessage());
        verify(userRepository).findById(1L);
        verify(userRepository).findByPhoneNumber(updateDto.getPhoneNumber());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void updateUser_NonExistingId_ThrowsResourceNotFoundException() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> userService.updateUser(999L, createUserDto)
        );

        assertEquals("User not found with id: 999", exception.getMessage());
        verify(userRepository).findById(999L);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void updateUser_NullAddress_SuccessfullyUpdates() {
        CreateUserDto updateDto = new CreateUserDto(
                "john.doe@example.com",
                "John",
                "Doe",
                "+1234567890",
                null
        );

        testUser.setAddress(null);
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.findByEmail(updateDto.getEmail())).thenReturn(Optional.of(testUser));
        when(userRepository.findByPhoneNumber(updateDto.getPhoneNumber())).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        UserDto result = userService.updateUser(1L, updateDto);

        assertNotNull(result);
        assertNull(result.getAddress());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void deleteUser_ExistingId_SuccessfullyDeletes() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        doNothing().when(userRepository).delete(testUser);

        userService.deleteUser(1L);

        verify(userRepository).findById(1L);
        verify(userRepository).delete(testUser);
    }


    @Test
    void deleteUser_NonExistingId_ThrowsResourceNotFoundException() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> userService.deleteUser(999L)
        );

        assertEquals("User not found with id: 999", exception.getMessage());
        verify(userRepository).findById(999L);
        verify(userRepository, never()).delete(any(User.class));
    }







}
