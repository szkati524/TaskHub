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
        if (userRepository.findByUsername("pm_user").isEmpty()) {
            User pm = User.builder()
                    .username("pm_user")
                    .email("pm@taskhub.com")
                    .password(passwordEncoder.encode("pm123"))
                    .role(Role.ROLE_PM)
                    .build();
            userRepository.save(pm);
        }
        if (userRepository.findByUsername("worker_jan").isEmpty()) {
            User worker = User.builder()
                    .username("worker_jan")
                    .email("jan@taskhub.com")
                    .password(passwordEncoder.encode("worker123"))
                    .role(Role.ROLE_PM)
                    .build();
            userRepository.save(worker);
        }
    }
}
