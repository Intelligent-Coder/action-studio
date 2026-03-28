package com.ecart.user.service;

import com.ecart.user.dto.CreateUserDto;
import com.ecart.user.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto createUser(CreateUserDto createUserDto);

    UserDto getUserById(Long id);

    UserDto getUserByEmail(String email);

    List<UserDto> getAllUsers();

    UserDto updateUser(Long id, CreateUserDto updateUserDto);

    void deleteUser(Long id);
}
