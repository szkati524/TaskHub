package com.taskhub.TaskHub.dto;

import com.taskhub.TaskHub.enums.TaskPriority;
import com.taskhub.TaskHub.enums.TaskStatus;

import java.time.LocalDateTime;

public record TaskResponseDto(Long id,
                              String title,
                              String description,
                              TaskStatus status,
                              TaskPriority priority,
                              String category,
                              LocalDateTime createdAt,
                              LocalDateTime updatedAt,
                              Long assigneeId,
                              String assigneeUsername) {
}
