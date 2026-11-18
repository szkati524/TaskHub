package com.taskhub.TaskHub.service;

import com.taskhub.TaskHub.dto.UserRequestDto;
import com.taskhub.TaskHub.dto.UserResponseDto;
import com.taskhub.TaskHub.entity.User;
import com.taskhub.TaskHub.enums.Role;
import com.taskhub.TaskHub.exception.ResourceNotFoundException;
import com.taskhub.TaskHub.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

   @Transactional
    public UserResponseDto createUser(UserRequestDto dto) {
       if (userRepository.findByUsername(dto.username()).isPresent()) {
           throw new ResourceNotFoundException("Użytkownik o nazwie " + dto.username() + " już istnieje");
       }
       User user = new User();
       user.setUsername(dto.username());
       user.setEmail(dto.email());
       User savedUser = userRepository.save(user);
       return toDto(savedUser);
   }
    public UserResponseDto findById(Long id){
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Nie znaleziono użytkownika o Id: " + id));
        return toDto(user);
    }
    public List<UserResponseDto> findAllUsers(){
        return userRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
    public List<UserResponseDto> findUsersByRole(Role role){
       return userRepository.findByRole(role).stream()
               .map(this::toDto)
               .toList();
    }
    @Transactional
    public UserResponseDto updateUser(Long id,UserRequestDto dto){
       User user = userRepository.findById(id)
               .orElseThrow(() -> new ResourceNotFoundException("nie znaleziono użytkownika o id: " + id));
       user.setUsername(dto.username());
       user.setEmail(dto.email());
       if (dto.password() != null && !dto.password().isBlank()){
           user.setPassword(passwordEncoder.encode(dto.password()));
       }
       if (dto.role() != null){
           user.setRole(dto.role());
       }
       return toDto(userRepository.save(user));
    }
    public void deleteUserById(Long id){
        userRepository.deleteById(id);
    }

    private UserResponseDto toDto(User user){
       return new UserResponseDto(
               user.getId(),
               user.getUsername(),
               user.getEmail(),
               user.getRole()
       );
    }
}
