package com.taskhub.TaskHub.service;

import com.taskhub.TaskHub.dto.CommentRequestDto;
import com.taskhub.TaskHub.dto.CommentResponseDto;
import com.taskhub.TaskHub.entity.Comment;
import com.taskhub.TaskHub.entity.Task;
import com.taskhub.TaskHub.entity.User;
import com.taskhub.TaskHub.enums.Role;
import com.taskhub.TaskHub.exception.ResourceNotFoundException;
import com.taskhub.TaskHub.repository.CommentRepository;
import com.taskhub.TaskHub.repository.TaskRepository;
import com.taskhub.TaskHub.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    @Transactional
    public CommentResponseDto addComment(Long taskId, CommentRequestDto dto,String currentUserName){
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Nie znaleziono zadania o Id: " + taskId));
        User author = userRepository.findByUsername(currentUserName)
                .orElseThrow(() -> new ResourceNotFoundException("Nie znaleziono użytkownika " + currentUserName)   );


        Comment comment = new Comment();
        comment.setContent(dto.content());
        comment.setTask(task);
        comment.setAuthor(author);
        Comment savedComment = commentRepository.save(comment);
return toDto(savedComment);
    }

    public List<CommentResponseDto > getCommentsByTaskId(Long taskId){
        if (!taskRepository.existsById(taskId)){
            throw  new ResourceNotFoundException("Nie znaleziono zadania o Id: " + taskId    );
        }
        return commentRepository.findByTaskId(taskId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
    @Transactional
    public CommentResponseDto updateComment(Long commentId,CommentRequestDto dto,String currentUsername){
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Nie znaleziono komentarza o ID: " + commentId));
        boolean isOwner = comment.getAuthor().getUsername().equals(currentUsername);
        if (!isOwner){
            throw new AccessDeniedException("Nie masz uprawnień do edycji tego komentarza.");
        }
        comment.setContent(dto.content());
        Comment updatedComment = commentRepository.save(comment);
        return toDto(updatedComment);
    }
    @Transactional
    public void deleteComment(Long commentId,String currentUsername){
      Comment comment = commentRepository.findById(commentId)
              .orElseThrow(() -> new ResourceNotFoundException("Nie znaleziono komentarza o Id: " + commentId));
      User currentUser = userRepository.findByUsername(currentUsername)
              .orElseThrow(() -> new ResourceNotFoundException("Użytkownik nie istnieje "));
      boolean isOwner = comment.getAuthor().getUsername().equals(currentUsername);
      boolean isAdminOrPm = currentUser.getRole() == Role.ROLE_ADMIN || currentUser.getRole() == Role.ROLE_PM;
      if (!isOwner && !isAdminOrPm){
          throw new AccessDeniedException("Nie masz uprawnień do usunięcia tego komentarza" );
        }
        commentRepository.deleteById(commentId);
    }
    private CommentResponseDto toDto(Comment comment){
        return new CommentResponseDto(comment.getId(),
                comment.getContent(),
                comment.getCreatedAt(),
                comment.getAuthor() != null ? comment.getAuthor().getUsername() : "Nieznany");
    }
}
