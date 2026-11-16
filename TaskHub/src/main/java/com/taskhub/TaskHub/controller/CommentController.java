package com.taskhub.TaskHub.controller;

import com.taskhub.TaskHub.dto.CommentRequestDto;
import com.taskhub.TaskHub.dto.CommentResponseDto;
import com.taskhub.TaskHub.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;

    @PostMapping("/{taskId}/comments")
    public ResponseEntity<CommentResponseDto> addComment(@PathVariable Long taskId,@Valid @RequestBody CommentRequestDto dto){
        CommentResponseDto createdComment = commentService.addComment(taskId,dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdComment);
    }
    @GetMapping("/{taskId}/comments")
    public ResponseEntity<List<CommentResponseDto>> getCommentsForTask(@PathVariable Long taskId){
        List<CommentResponseDto> comments = commentService.getCommentsByTaskId(taskId);
        return ResponseEntity.ok(comments);
    }
    @PutMapping("/comments/{commentId}")
    public ResponseEntity<CommentResponseDto> updateComment(@PathVariable Long commentId,@Valid @RequestBody CommentRequestDto dto){
        CommentResponseDto updatedComment = commentService.updateComment(commentId,dto);
        return ResponseEntity.ok(updatedComment);
    }
    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long commentId){
        commentService.deleteComment(commentId);
        return ResponseEntity.noContent().build();
    }

}
