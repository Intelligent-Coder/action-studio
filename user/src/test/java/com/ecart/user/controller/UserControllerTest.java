package com.ecart.user.controller;

import com.ecart.user.dto.AddressDto;
import com.ecart.user.dto.CreateUserDto;
import com.ecart.user.dto.UserDto;
import com.ecart.user.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private ObjectMapper objectMapper;
    private UserDto userDto;
    private CreateUserDto createUserDto;
    private AddressDto addressDto;

    @BeforeEach
    void setUp() {
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        mockMvc = MockMvcBuilders.standaloneSetup(userController)
                .setValidator(validator)
                .build();
        objectMapper = new ObjectMapper();

        addressDto = new AddressDto("123 Main St", "New York", "NY", "10001");

        createUserDto = new CreateUserDto(
                "john.doe@example.com",
                "John",
                "Doe",
                "+1234567890",
                addressDto
        );

        userDto = new UserDto(
                1L,
                "john.doe@example.com",
                "John",
                "Doe",
                "+1234567890",
                addressDto,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }

    @Test
    void createUser_ValidInput_ReturnsCreatedUser() throws Exception {
        when(userService.createUser(any(CreateUserDto.class))).thenReturn(userDto);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createUserDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(userDto.getId().intValue())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())))
                .andExpect(jsonPath("$.firstName", is(userDto.getFirstName())))
                .andExpect(jsonPath("$.lastName", is(userDto.getLastName())))
                .andExpect(jsonPath("$.phoneNumber", is(userDto.getPhoneNumber())))
                .andExpect(jsonPath("$.address.street", is(addressDto.getStreet())));
    }

    @Test
    void createUser_InvalidInput_ReturnsBadRequest() throws Exception {
        CreateUserDto invalidDto = new CreateUserDto("", "", "", "", null);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(userService);
    }

    @Test
    void getUserById_ExistingId_ReturnsUser() throws Exception {
        when(userService.getUserById(1L)).thenReturn(userDto);

        mockMvc.perform(get("/api/users/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto.getId().intValue())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())));
    }

    @Test
    void getUserByEmail_ExistingEmail_ReturnsUser() throws Exception {
        when(userService.getUserByEmail("john.doe@example.com")).thenReturn(userDto);

        mockMvc.perform(get("/api/users/email/{email}", "john.doe@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto.getId().intValue())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())));
    }

    @Test
    void getAllUsers_ReturnsUserList() throws Exception {
        UserDto userDto2 = new UserDto(
                2L,
                "jane.doe@example.com",
                "Jane",
                "Doe",
                "+0987654321",
                addressDto,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        List<UserDto> userList = Arrays.asList(userDto, userDto2);
        when(userService.getAllUsers()).thenReturn(userList);

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(userDto.getId().intValue())))
                .andExpect(jsonPath("$[0].email", is(userDto.getEmail())))
                .andExpect(jsonPath("$[1].id", is(userDto2.getId().intValue())))
                .andExpect(jsonPath("$[1].email", is(userDto2.getEmail())));
    }

    @Test
    void updateUser_ExistingId_ReturnsUpdatedUser() throws Exception {
        UserDto updatedUserDto = new UserDto(
                1L,
                "john.updated@example.com",
                "John Updated",
                "Doe",
                "+1234567890",
                addressDto,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        CreateUserDto updateDto = new CreateUserDto(
                "john.updated@example.com",
                "John Updated",
                "Doe",
                "+1234567890",
                addressDto
        );

        when(userService.updateUser(eq(1L), any(CreateUserDto.class))).thenReturn(updatedUserDto);

        mockMvc.perform(put("/api/users/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(updatedUserDto.getId().intValue())))
                .andExpect(jsonPath("$.email", is(updatedUserDto.getEmail())))
                .andExpect(jsonPath("$.firstName", is(updatedUserDto.getFirstName())));
    }

    @Test
    void deleteUser_ExistingId_ReturnsNoContent() throws Exception {
        doNothing().when(userService).deleteUser(1L);

        mockMvc.perform(delete("/api/users/{id}", 1L))
                .andExpect(status().isNoContent());
    }

    @Test
    void updateUser_InvalidInput_ReturnsBadRequest() throws Exception {
        CreateUserDto invalidDto = new CreateUserDto("", "", "", "", null);

        mockMvc.perform(put("/api/users/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(userService);
    }
}