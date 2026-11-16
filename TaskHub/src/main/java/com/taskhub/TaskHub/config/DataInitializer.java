package com.taskhub.TaskHub.config;

import com.taskhub.TaskHub.entity.User;
import com.taskhub.TaskHub.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {
    private final UserRepository userRepository;
    @Override
    public void run(String... args) throws Exception {
        if (userRepository.count() == 0 ){
            User defaultUser = new User();
            defaultUser.setUsername("test_user");
            defaultUser.setEmail("test@taskhub.com");
            userRepository.save(defaultUser);
            System.out.println(">>> [INIT] Dodano domyślnego użytkownika testowego: test_user");
        }
    }
}
