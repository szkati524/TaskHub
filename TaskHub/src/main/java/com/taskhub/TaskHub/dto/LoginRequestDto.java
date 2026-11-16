package com.taskhub.TaskHub.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginRequestDto(@NotBlank(message = "Nazwa użytkownika nie jest wymagana")
                              String username,
                              @NotBlank(message = "Hasło jest wymagane")
                              String password) {
}
