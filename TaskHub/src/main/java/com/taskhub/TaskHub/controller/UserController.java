package com.taskhub.TaskHub.controller;

import com.taskhub.TaskHub.dto.UserRequestDto;
import com.taskhub.TaskHub.dto.UserResponseDto;
import com.taskhub.TaskHub.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserResponseDto> createUser(@RequestBody UserRequestDto requestDto){
        UserResponseDto createdUser = userService.createUser(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> getUserById(@PathVariable Long id){
        UserResponseDto dto = userService.findById(id);
        return ResponseEntity.ok(dto);
    }
    @GetMapping
    public ResponseEntity<List<UserResponseDto>> getAllUsers(){
        List<UserResponseDto> users = userService.findAllUsers();
        return ResponseEntity.ok(users);
    }
}
