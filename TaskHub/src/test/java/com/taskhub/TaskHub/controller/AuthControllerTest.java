package com.taskhub.TaskHub.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.taskhub.TaskHub.dto.AuthResponseDto;
import com.taskhub.TaskHub.dto.LoginRequestDto;
import com.taskhub.TaskHub.dto.UserRequestDto;
import com.taskhub.TaskHub.enums.Role;
import com.taskhub.TaskHub.security.JwtService;
import com.taskhub.TaskHub.service.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;


    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @Test
    @WithMockUser
    void shouldRegisterUser_AndReturnCreatedStatus()throws Exception {
        UserRequestDto requestDto = new UserRequestDto(
                "jan_kowalski",
                "jan@taskhub.com",
                "tajneHaslo123",
                Role.ROLE_EMPLOYEE
        );
        AuthResponseDto responseDto = new AuthResponseDto(
                "sample-jwt-token",
                "jan_kowalski",
                Role.ROLE_EMPLOYEE
        );
        when(authService.register(any(UserRequestDto.class))).thenReturn(responseDto);
        mockMvc.perform(post("/api/auth/register")

                        .with(csrf())

                        .contentType(MediaType.APPLICATION_JSON)

                        .content(objectMapper.writeValueAsString(requestDto)))

                .andExpect(status().isCreated())

                .andExpect(content().contentType(MediaType.APPLICATION_JSON))

                .andExpect(jsonPath("$.token").value("sample-jwt-token"))
                .andExpect(jsonPath("$.username").value("jan_kowalski"))
                .andExpect(jsonPath("$.role").value("ROLE_EMPLOYEE"));


        verify(authService, times(1)).register(any(UserRequestDto.class));
    }
@Test
    @WithMockUser
    void shouldReturnBadRequest_WhenRegisterRequestIsInvalid() throws Exception{
        UserRequestDto invalidDto = new UserRequestDto(
                "",
                "niepoprawny-email",
                "",
                null
        );
        mockMvc.perform(post("/api/auth/register")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());
        verify(authService,never()).register(any());
}
@Test
    @WithMockUser
    void shouldLoginUser_AndReturnOkStatus() throws Exception{
    LoginRequestDto loginDto = new LoginRequestDto("jan_kowalski","tajneHaslo123");
    AuthResponseDto responseDto = new AuthResponseDto(
            "login-jwt-token",
            "jan_kowalski",
            Role.ROLE_EMPLOYEE
    );
    when(authService.login(any(LoginRequestDto.class))).thenReturn(responseDto);
    mockMvc.perform(post("/api/auth/login")
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(loginDto)))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.token").value("login-jwt-token"))
            .andExpect(jsonPath("$.username").value("jan_kowalski"))
            .andExpect(jsonPath("$.role").value("ROLE_EMPLOYEE"));
    verify(authService,times(1)).login(any(LoginRequestDto.class));

}
    }

