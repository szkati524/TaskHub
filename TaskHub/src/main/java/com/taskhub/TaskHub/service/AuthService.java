package com.taskhub.TaskHub.service;

import com.taskhub.TaskHub.dto.AuthResponseDto;
import com.taskhub.TaskHub.dto.LoginRequestDto;
import com.taskhub.TaskHub.dto.UserRequestDto;
import com.taskhub.TaskHub.entity.User;
import com.taskhub.TaskHub.enums.Role;
import com.taskhub.TaskHub.exception.ResourceNotFoundException;
import com.taskhub.TaskHub.repository.UserRepository;
import com.taskhub.TaskHub.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthResponseDto register(UserRequestDto request){
        if (userRepository.findByUsername(request.username()).isPresent()) {
            throw new ResourceNotFoundException("Użytkownik o takiej nazwie nie istnieje!");
        }
        User user = User.builder()
                .username(request.username())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .role(request.role() != null ? request.role(): Role.ROLE_EMPLOYEE)
                .build();
        userRepository.save(user);
        String jwtToken = jwtService.generateToken(
                new org.springframework.security.core.userdetails.User(
                        user.getUsername(),
                        user.getPassword(),
                        java.util.Collections.singletonList(new org.springframework.security.core.authority.SimpleGrantedAuthority(user.getRole().name()))
                )

        );
        return new AuthResponseDto(jwtToken,user.getUsername(),user.getRole());
    }
    public AuthResponseDto login(LoginRequestDto request){
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.username(),
                        request.password()
                )
        );
        User user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new UsernameNotFoundException("Nie znaleziono użytkownika"));
        String jwtToken = jwtService.generateToken(
                new org.springframework.security.core.userdetails.User(
                        user.getUsername(),
                        user.getPassword(),
                        java.util.Collections.singletonList(new org.springframework.security.core.authority.SimpleGrantedAuthority(user.getRole().name()))
                )
        );
        return new AuthResponseDto(jwtToken,user.getUsername(),user.getRole());
    }
}
