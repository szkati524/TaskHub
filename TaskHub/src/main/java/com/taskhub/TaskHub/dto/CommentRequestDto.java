package com.taskhub.TaskHub.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CommentRequestDto(
        @NotBlank(message = "Treść komentarza nie może być pusta")
        @Size(min=1,max = 500,message = "Komentarz musi mieć od 1 do 500 znaków")
        String content
) {
}
