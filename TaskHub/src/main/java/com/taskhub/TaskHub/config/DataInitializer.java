package com.taskhub.TaskHub.config;

import com.taskhub.TaskHub.entity.User;
import com.taskhub.TaskHub.enums.Role;
import com.taskhub.TaskHub.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    @Override
    public void run(String... args) throws Exception {
        if (userRepository.findByUsername("admin").isEmpty()){
            User admin = User.builder()
                    .username("admin")
                    .email("admin@taskhub.com")
                    .password(passwordEncoder.encode("admin123"))
                    .role(Role.ROLE_ADMIN)
                    .build();
            userRepository.save(admin);
        }
    }
}
