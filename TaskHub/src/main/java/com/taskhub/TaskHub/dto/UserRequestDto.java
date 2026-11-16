package com.taskhub.TaskHub.dto;

import com.taskhub.TaskHub.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserRequestDto(
        @NotBlank(message = "Nazwa użytkownika nie może być pusta")
        @Size(min = 3,max=50,message = "Nazwa użytkownika musi mieć od 3 do 50 znaków")
        String username,
        @NotBlank(message = "Adres e-mail jest wymagany")
                             @Email(message = "Niepoprawny format adresu e-mail")
                             String email,
        String password, Role role) {
}
