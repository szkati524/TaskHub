package com.taskhub.TaskHub.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.taskhub.TaskHub.dto.UserRequestDto;
import com.taskhub.TaskHub.dto.UserResponseDto;
import com.taskhub.TaskHub.enums.Role;
import com.taskhub.TaskHub.security.JwtService;
import com.taskhub.TaskHub.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserDetailsService userDetailsService;

    private UserRequestDto sampleRequestDto;
    private UserResponseDto sampleResponseDto;

    @BeforeEach
    void setUp() {
        sampleRequestDto = new UserRequestDto(
                "jan_kowalski",
                "jan@taskhub.com",
                "haslo123",
                Role.ROLE_EMPLOYEE
        );
        sampleResponseDto = new UserResponseDto(
                1L,
                "jan_kowalski",
                "jan@taskhub.com",
                Role.ROLE_EMPLOYEE
        );
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldCreateUser_AndReturnCreatedStatus() throws Exception {
        when(userService.createUser(any(UserRequestDto.class))).thenReturn(sampleResponseDto);

        mockMvc.perform(post("/api/users")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleRequestDto)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.username").value("jan_kowalski"))
                .andExpect(jsonPath("$.email").value("jan@taskhub.com"))
                .andExpect(jsonPath("$.role").value("ROLE_EMPLOYEE"));

        verify(userService, times(1)).createUser(any(UserRequestDto.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldReturnBadRequest_WhenCreatingUserWithInvalidData() throws Exception {
        UserRequestDto invalidDto = new UserRequestDto(
                "ab",
                "niepoprawny-email",
                "haslo123",
                Role.ROLE_EMPLOYEE
        );

        mockMvc.perform(post("/api/users")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());

        verify(userService, never()).createUser(any());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldUpdateUser_AndReturnOkStatus() throws Exception {
        when(userService.updateUser(eq(1L), any(UserRequestDto.class))).thenReturn(sampleResponseDto);

        mockMvc.perform(put("/api/users/{id}", 1L)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleRequestDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.username").value("jan_kowalski"))
                .andExpect(jsonPath("$.email").value("jan@taskhub.com"))
                .andExpect(jsonPath("$.role").value("ROLE_EMPLOYEE"));

        verify(userService, times(1)).updateUser(eq(1L), any(UserRequestDto.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldGetUsersByRole_AndReturnOkStatus() throws Exception {
        when(userService.findUsersByRole(Role.ROLE_EMPLOYEE)).thenReturn(List.of(sampleResponseDto));

        mockMvc.perform(get("/api/users/role/{role}", Role.ROLE_EMPLOYEE.name()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].username").value("jan_kowalski"))
                .andExpect(jsonPath("$[0].role").value("ROLE_EMPLOYEE"));

        verify(userService, times(1)).findUsersByRole(Role.ROLE_EMPLOYEE);
    }

    @Test
    @WithMockUser
    void shouldGetUserById_AndReturnOkStatus() throws Exception {
        when(userService.findById(1L)).thenReturn(sampleResponseDto);

        mockMvc.perform(get("/api/users/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.username").value("jan_kowalski"))
                .andExpect(jsonPath("$.email").value("jan@taskhub.com"));

        verify(userService, times(1)).findById(1L);
    }

    @Test
    @WithMockUser
    void shouldGetAllUsers_AndReturnOkStatus() throws Exception {
        when(userService.findAllUsers()).thenReturn(List.of(sampleResponseDto));

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].username").value("jan_kowalski"));

        verify(userService, times(1)).findAllUsers();
    }
}
