package com.taskhub.TaskHub.service;

import com.taskhub.TaskHub.dto.UserRequestDto;
import com.taskhub.TaskHub.dto.UserResponseDto;
import com.taskhub.TaskHub.entity.User;
import com.taskhub.TaskHub.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

   @Transactional
    public UserResponseDto createUser(UserRequestDto dto) {
       if (userRepository.findByUsername(dto.username()).isPresent()) {
           throw new RuntimeException("Użytkownik o nazwie " + dto.username() + " już istnieje");
       }
       User user = new User();
       user.setUsername(dto.username());
       user.setEmail(dto.email());
       User savedUser = userRepository.save(user);
       return toDto(savedUser);
   }
    public UserResponseDto findById(Long id){
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Nie znaleziono użytkownika o Id: " + id));
        return toDto(user);
    }
    public List<UserResponseDto> findAllUsers(){
        return userRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
    public void deleteUserById(Long id){
        userRepository.deleteById(id);
    }

    private UserResponseDto toDto(User user){
       return new UserResponseDto(
               user.getId(),
               user.getUsername(),
               user.getEmail()
       );
    }
}
