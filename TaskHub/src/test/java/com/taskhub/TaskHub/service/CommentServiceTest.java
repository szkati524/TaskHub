package com.taskhub.TaskHub.service;

import com.taskhub.TaskHub.dto.CommentRequestDto;
import com.taskhub.TaskHub.dto.CommentResponseDto;
import com.taskhub.TaskHub.dto.TaskResponseDto;
import com.taskhub.TaskHub.entity.Comment;
import com.taskhub.TaskHub.entity.Task;
import com.taskhub.TaskHub.entity.User;
import com.taskhub.TaskHub.enums.Role;
import com.taskhub.TaskHub.exception.ResourceNotFoundException;
import com.taskhub.TaskHub.repository.CommentRepository;
import com.taskhub.TaskHub.repository.TaskRepository;
import com.taskhub.TaskHub.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.client.ResourceAccessException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CommentService commentService;

    private User workerUser;
    private User adminUser;
    private User otherUser;
    private Task sampleTask;
    private Comment sampleComment;

    @BeforeEach
    void setUp() {

        workerUser = User.builder()
                .id(1L)
                .username("worker_jan")
                .role(Role.ROLE_EMPLOYEE)
                .build();


        adminUser = User.builder()
                .id(2L)
                .username("admin")
                .role(Role.ROLE_ADMIN)
                .build();


        otherUser = User.builder()
                .id(3L)
                .username("obcy")
                .role(Role.ROLE_EMPLOYEE)
                .build();


        sampleTask = new Task();
        sampleTask.setId(1L);
        sampleTask.setTitle("Zadanie testowe");


        sampleComment = new Comment();
        sampleComment.setId(1L);
        sampleComment.setContent("To jest testowy komentarz");
        sampleComment.setTask(sampleTask);
        sampleComment.setAuthor(workerUser);
        sampleComment.setCreatedAt(LocalDateTime.now());
    }



    @Test
    void shouldAddComment_Successfully() {
        CommentRequestDto dto = new CommentRequestDto("Nowy komentarz");
        when(taskRepository.findById(1L)).thenReturn(Optional.of(sampleTask));
        when(userRepository.findByUsername("worker_jan")).thenReturn(Optional.of(workerUser));
        when(commentRepository.save(any(Comment.class))).thenReturn(sampleComment);
        CommentResponseDto result = commentService.addComment(1L,dto,"worker_jan");
        assertNotNull(result);
        assertEquals("To jest testowy komentarz", result.content());
        verify(taskRepository,times(1)).findById(1L);
        verify(userRepository,times(1)).findByUsername("worker_jan" );
        verify(commentRepository,times(1)).save(any(Comment.class));
    }

    @Test
    void shouldThrowException_WhenAddingCommentToNonExistingTask() {
        CommentRequestDto dto = new CommentRequestDto("test");
     when(taskRepository.findById(99L)).thenReturn(Optional.empty());
     assertThrows(ResourceNotFoundException.class,() -> {
         commentService.addComment(99L,dto,"worker_jan" );
     });
    }

    @Test
    void shouldThrowException_WhenAuthorNotFound() {
        CommentRequestDto dto = new CommentRequestDto("test");
       when(taskRepository.findById(99L)).thenReturn(Optional.of(sampleTask)  );
       when(userRepository.findByUsername("nieznany")).thenReturn(Optional.empty());
       assertThrows(ResourceNotFoundException.class,() -> {
           commentService.addComment(99L,dto,"nieznany");
       });
    }



    @Test
    void shouldReturnCommentsList_WhenTaskExists() {
        List<Comment> resList = List.of(sampleComment   );
       when(taskRepository.existsById(sampleTask.getId())).thenReturn(true);
       when(commentRepository.findByTaskId(sampleTask.getId())).thenReturn(resList);
       List<CommentResponseDto> result = commentService.getCommentsByTaskId(sampleTask.getId());
       assertNotNull(result);
       assertEquals(1,result.size());
       assertEquals("To jest testowy komentarz",result.get(0).content());
       verify(taskRepository,times(1)).existsById(sampleTask.getId());
       verify(commentRepository,times(1)).findByTaskId(sampleTask.getId());
    }

    @Test
    void shouldThrowException_WhenGettingCommentsForNonExistingTask() {
        when(taskRepository.existsById(99L)).thenReturn(false);
        assertThrows(ResourceNotFoundException.class,() -> {
            commentService.getCommentsByTaskId(99L);
        });
        verify(taskRepository,times(1)).existsById(99L);
    }



    @Test
    void shouldUpdateComment_Successfully_WhenUserIsOwner() {
       CommentRequestDto dto = new CommentRequestDto("Zaktualizowano treść");
       when(commentRepository.findById(1L)).thenReturn(Optional.of(sampleComment));
       when(commentRepository.save(any(Comment.class))).thenReturn(sampleComment);
       CommentResponseDto result = commentService.updateComment(1L,dto,"worker_jan");
       assertNotNull(result);
       verify(commentRepository,times(1)).findById(1L);
       verify(commentRepository,times(1)).save(any(Comment.class));
    }

    @Test
    void shouldThrowAccessDeniedException_WhenUserIsNotCommentOwner() {
      CommentRequestDto dto = new CommentRequestDto("Próba zhakowania treści");
      when(commentRepository.findById(1L)).thenReturn(Optional.of(sampleComment));
      assertThrows(AccessDeniedException.class,() -> {
          commentService.updateComment(1L,dto,"obcy");
      });
      verify(commentRepository,never()).save(any(Comment.class));
    }



    @Test
    void shouldDeleteComment_Successfully_WhenUserIsOwner() {
        when(commentRepository.findById(1L)).thenReturn(Optional.of(sampleComment));
        when(userRepository.findByUsername("worker_jan")).thenReturn(Optional.of(workerUser));
        doNothing().when(commentRepository).deleteById(1L);
        assertDoesNotThrow(() -> {
            commentService.deleteComment(1L,"worker_jan");
        });
        verify(commentRepository,times(1)).deleteById(1L);
    }

    @Test
    void shouldDeleteComment_Successfully_WhenUserIsAdmin() {
        when(commentRepository.findById(1L)).thenReturn(Optional.of(sampleComment));
        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(adminUser));
        doNothing().when(commentRepository).deleteById(1L);
        assertDoesNotThrow(() -> {
            commentService.deleteComment(1L, "admin");
        });
        verify(commentRepository,times(1)).deleteById(1L);
    }

    @Test
    void shouldThrowAccessDeniedException_WhenNormalUserTriesToDeleteOthersComment() {
        when(commentRepository.findById(1L)).thenReturn(Optional.of(sampleComment));
        when(userRepository.findByUsername("obcy")).thenReturn(Optional.of(otherUser));
        assertThrows(AccessDeniedException.class,() -> {
            commentService.deleteComment(1L,"obcy");
        });
        verify(commentRepository,never()).deleteById(anyLong());
    }
}

