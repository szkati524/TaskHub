package com.taskhub.TaskHub.service;

import com.taskhub.TaskHub.dto.AuthResponseDto;
import com.taskhub.TaskHub.dto.LoginRequestDto;
import com.taskhub.TaskHub.dto.UserRequestDto;
import com.taskhub.TaskHub.entity.User;
import com.taskhub.TaskHub.enums.Role;
import com.taskhub.TaskHub.exception.ResourceNotFoundException;
import com.taskhub.TaskHub.repository.UserRepository;
import com.taskhub.TaskHub.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {
    @Mock
    private UserRepository userRepository;


    @Mock
    private PasswordEncoder passwordEncoder;


    @Mock
    private JwtService jwtService;


    @Mock
    private AuthenticationManager authenticationManager;


    @InjectMocks
    private AuthService authService;

    private User sampleUser;

    @BeforeEach
    void setUp() {

        sampleUser = User.builder()
                .id(1L)
                .username("jan_kowalski")
                .email("jan@taskhub.com")
                .password("encoded_password")
                .role(Role.ROLE_EMPLOYEE)
                .build();
    }



    @Test
    void shouldRegisterUser_Successfully() {

        UserRequestDto requestDto = new UserRequestDto(
                "jan_kowalski", "jan@taskhub.com", "tajneHaslo", Role.ROLE_EMPLOYEE
        );


        when(userRepository.findByUsername(requestDto.username())).thenReturn(Optional.empty());


        when(passwordEncoder.encode(requestDto.password())).thenReturn("encoded_password");


        when(jwtService.generateToken(any())).thenReturn("mock-jwt-token");


        AuthResponseDto result = authService.register(requestDto);


        assertNotNull(result);
        assertEquals("mock-jwt-token", result.token());
        assertEquals("jan_kowalski", result.username());
        assertEquals(Role.ROLE_EMPLOYEE, result.role());


        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void shouldThrowException_WhenRegisteringUserWithExistingUsername() {

        UserRequestDto requestDto = new UserRequestDto(
                "jan_kowalski", "inny@taskhub.com", "haslo", Role.ROLE_EMPLOYEE
        );


        when(userRepository.findByUsername(requestDto.username())).thenReturn(Optional.of(sampleUser));


        assertThrows(ResourceNotFoundException.class, () -> {
            authService.register(requestDto);
        });


        verify(userRepository, never()).save(any(User.class));
    }



    @Test
    void shouldLoginUser_Successfully() {

        LoginRequestDto loginDto = new LoginRequestDto("jan_kowalski", "tajneHaslo");


        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(null);


        when(userRepository.findByUsername("jan_kowalski")).thenReturn(Optional.of(sampleUser));


        when(jwtService.generateToken(any())).thenReturn("jwt-login-token");


        AuthResponseDto result = authService.login(loginDto);


        assertNotNull(result);
        assertEquals("jwt-login-token", result.token());
        assertEquals("jan_kowalski", result.username());


        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userRepository, times(1)).findByUsername("jan_kowalski");
    }

    @Test
    void shouldThrowException_WhenUserNotFoundDuringLogin() {

        LoginRequestDto loginDto = new LoginRequestDto("nieznany_user", "haslo");


        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(null);


        when(userRepository.findByUsername("nieznany_user")).thenReturn(Optional.empty());


        assertThrows(UsernameNotFoundException.class, () -> {
            authService.login(loginDto);
        });
    }
}
