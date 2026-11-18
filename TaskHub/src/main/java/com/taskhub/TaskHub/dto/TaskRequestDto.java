package com.taskhub.TaskHub.dto;

import com.taskhub.TaskHub.enums.TaskPriority;
import com.taskhub.TaskHub.validation.ValidCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record TaskRequestDto(@NotBlank(message = "tytuł nie może być pusty")
                             @Size(min = 3,max = 100,message = "Tytuł musi mieć od 3 do 100 znaków") String title,
                             String description,
                             @NotNull(message = "Priorytet jest wymagany")
                             TaskPriority priority,
                              @ValidCategory
                              String category,
                             Long assigneeId) {
}
