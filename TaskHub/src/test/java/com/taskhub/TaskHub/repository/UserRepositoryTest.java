package com.taskhub.TaskHub.repository;

import com.taskhub.TaskHub.entity.User;
import com.taskhub.TaskHub.enums.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;


    private User employeeUser;
    private User adminUser;


    @BeforeEach
    void setUp() {

        employeeUser = userRepository.save(User.builder()
                .username("jan_kowalski")
                .email("jan@taskhub.com")
                .password("encoded_password_1")
                .role(Role.ROLE_EMPLOYEE)
                .build());

        adminUser = userRepository.save(User.builder()
                .username("anna_admin")
                .email("anna@taskhub.com")
                .password("encoded_password_2")
                .role(Role.ROLE_ADMIN)
                .build());
    }
    @Test
    void shouldFindUserByUsername_WhenUserExists(){
        Optional<User> foundUser = userRepository.findByUsername("jan_kowalski");
        assertTrue(foundUser.isPresent()    );
        assertEquals("jan_kowalski",foundUser.get().getUsername());
        assertEquals("jan@taskhub.com",foundUser.get().getEmail());
        assertEquals(Role.ROLE_EMPLOYEE,foundUser.get().getRole());

    }
    @Test
    void shouldReturnEmptyOptional_WhenUserDoesNotExist(){
        Optional<User> foundUser = userRepository.findByUsername("nieistniejący_user");
        assertTrue(foundUser.isEmpty());
    }
    @Test
    void shouldFindUsersByRole_WhenMatchingUsersExist(){
        userRepository.save(User.builder()
                .username("marek_nowak")
                .email("marek@taskhub.com")
                .password("encoded_password_3")
                .role(Role.ROLE_EMPLOYEE)
                .build());
        List<User> employees = userRepository.findByRole(Role.ROLE_EMPLOYEE);
        assertNotNull(employees);
        assertEquals(2,employees.size());
        assertTrue(employees.stream().allMatch(u -> u.getRole() == Role.ROLE_EMPLOYEE));

    }
    @Test
    void shouldReturnEmptyList_WhenNoUsersHaveGivenRole(){
        userRepository.delete(adminUser);
        List<User> admins = userRepository.findByRole(Role.ROLE_ADMIN);
        assertNotNull(admins);
        assertTrue(admins.isEmpty());

    }
}
