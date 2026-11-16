package com.taskhub.TaskHub.dto;

import com.taskhub.TaskHub.enums.Role;

public record AuthResponseDto(String token,
                              String username,
                              Role role) {
}
