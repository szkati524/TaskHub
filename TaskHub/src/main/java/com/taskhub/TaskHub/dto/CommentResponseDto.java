package com.taskhub.TaskHub.dto;

import java.time.LocalDateTime;

public record CommentResponseDto(Long id,
                                 String content,
                                 LocalDateTime createdAt,
                                 String authorUsername) {
}
