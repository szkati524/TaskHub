package com.taskhub.TaskHub.dto;

import com.taskhub.TaskHub.enums.TaskPriority;

public record TaskRequestDto(String title,
                             String description,
                             TaskPriority priority,
                              String category) {
}
