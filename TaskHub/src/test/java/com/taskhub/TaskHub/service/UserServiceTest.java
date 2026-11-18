package com.taskhub.TaskHub.service;

import com.taskhub.TaskHub.dto.UserRequestDto;
import com.taskhub.TaskHub.dto.UserResponseDto;
import com.taskhub.TaskHub.entity.User;
import com.taskhub.TaskHub.enums.Role;
import com.taskhub.TaskHub.exception.ResourceNotFoundException;
import com.taskhub.TaskHub.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

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
    void shouldCreateUser_Successfully() {
        UserRequestDto requestDto = new UserRequestDto(
                "jan_kowalski", "jan@taskhub.com", "tajneHaslo123", Role.ROLE_EMPLOYEE
        );
        when(userRepository.findByUsername("jan_kowalski")).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(sampleUser);
        UserResponseDto result = userService.createUser(requestDto);
        assertNotNull(result);
        assertEquals("jan_kowalski", result.username());
        assertEquals("jan@taskhub.com", result.email());
        verify(userRepository, times(1)).findByUsername("jan_kowalski");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void shouldThrowException_WhenUsernameAlreadyExists() {
        UserRequestDto requestDto = new UserRequestDto("jan_kowalski", "nowy@taskhub.com", "haslo", Role.ROLE_EMPLOYEE);
        when(userRepository.findByUsername("jan_kowalski")).thenReturn(Optional.of(sampleUser));
        assertThrows(ResourceNotFoundException.class, () -> {
            userService.createUser(requestDto);
        });
        verify(userRepository, never()).save(any(User.class));
    }
    @Test
    void shouldFindUserById_Successfully(){
        when(userRepository.findById(1L)).thenReturn(Optional.of(sampleUser));
        UserResponseDto result = userService.findById(1L);
        assertNotNull(result);
        assertEquals(1L,result.id());
        assertEquals("jan_kowalski",result.username());
        verify(userRepository,times(1)).findById(1L);
    }
    @Test
    void shouldThrowException_WhenUserNotFoundById(){
        when(userRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class,() -> {
            userService.findById(99L);
        });
        verify(userRepository,times(1)).findById(99L);
    }
    @Test
    void shouldReturnAllUsersList(){
        List<User> userList = List.of(sampleUser);
        when(userRepository.findAll()).thenReturn(userList);
        List<UserResponseDto> result = userService.findAllUsers();
        assertNotNull(result);
        assertEquals(1,result.size());
        assertEquals("jan_kowalski",result.get(0).username());
        verify(userRepository,times(1)).findAll();
    }
    @Test
    void shouldReturnUsersByRole(){
        List<User> userList = List.of(sampleUser);
        when(userRepository.findByRole(Role.ROLE_EMPLOYEE)).thenReturn(userList);
        List<UserResponseDto> result = userService.findUsersByRole(Role.ROLE_EMPLOYEE);
        assertNotNull(result);
        assertEquals(1,result.size());
        assertEquals(Role.ROLE_EMPLOYEE,result.get(0).role());
        verify(userRepository,times(1)).findByRole(Role.ROLE_EMPLOYEE);
    }
    @Test
    void shouldUpdateUser_Successfully_WithNewPasswordAndRole(){
        UserRequestDto requestDto = new UserRequestDto(
                "jan_zaktualizowany", "nowy_mail@taskhub.com", "noweHaslo", Role.ROLE_ADMIN
        );
        when(userRepository.findById(1L)).thenReturn(Optional.of(sampleUser));
        when(passwordEncoder.encode("noweHaslo")).thenReturn("encoded_new_password");
        when(userRepository.save(any(User.class))).thenReturn(sampleUser);
        UserResponseDto result = userService.updateUser(1L,requestDto);
        assertNotNull(result);
        assertEquals("jan_zaktualizowany", sampleUser.getUsername());
        assertEquals("nowy_mail@taskhub.com", sampleUser.getEmail());
        assertEquals("encoded_new_password", sampleUser.getPassword());
        assertEquals(Role.ROLE_ADMIN, sampleUser.getRole());
        verify(userRepository, times(1)).findById(1L);
        verify(passwordEncoder, times(1)).encode("noweHaslo");
        verify(userRepository, times(1)).save(sampleUser);
    }
    @Test
    void shouldUpdateUser_WithoutChangingPassword_WhenPasswordIsBlank(){
        UserRequestDto requestDto = new UserRequestDto(
                "jan_zmieniony", "jan@taskhub.com", "   ", Role.ROLE_EMPLOYEE
        );
        when(userRepository.findById(1L)).thenReturn(Optional.of(sampleUser));
        when(userRepository.save(any(User.class))).thenReturn(sampleUser);
        userService.updateUser(1L,requestDto);
        assertEquals("encoded_password",sampleUser.getPassword());
        verify(passwordEncoder,never()).encode(anyString());
    }
    @Test
    void shouldDeleteUserById_Successfully() {

        doNothing().when(userRepository).deleteById(1L);


        assertDoesNotThrow(() -> {
            userService.deleteUserById(1L);
        });


        verify(userRepository, times(1)).deleteById(1L);
    }
}


