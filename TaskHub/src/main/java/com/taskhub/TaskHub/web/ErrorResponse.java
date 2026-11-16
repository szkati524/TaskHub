package com.taskhub.TaskHub.web;

import java.time.LocalDateTime;

public record ErrorResponse(int status,
                            String message,
                            LocalDateTime timestamp) {
}
