package com.taskhub.TaskHub.dto;

import com.taskhub.TaskHub.enums.Role;

public record UserResponseDto(Long id,
                              String username,
                              String email,
                              Role role) {
}
