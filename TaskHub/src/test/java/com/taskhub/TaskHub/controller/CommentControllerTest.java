package com.taskhub.TaskHub.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.taskhub.TaskHub.dto.CommentRequestDto;
import com.taskhub.TaskHub.dto.CommentResponseDto;
import com.taskhub.TaskHub.security.JwtService;
import com.taskhub.TaskHub.service.CommentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CommentController.class)
public class CommentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    @MockitoBean
    private CommentService commentService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserDetailsService userDetailsService;

    private CommentResponseDto sampleResponseDto;

    @BeforeEach
    void setUp() {
        sampleResponseDto = new CommentResponseDto(
                1L,
                "Przykładowa treść komentarza",
                LocalDateTime.now(),
                "jan_kowalski"
        );
    }

    @Test
    @WithMockUser(username = "jan_kowalski")
    void shouldAddComment_AndReturnCreatedStatus() throws Exception {
        CommentRequestDto requestDto = new CommentRequestDto("Nowy komentarz do zadania");

        when(commentService.addComment(eq(10L), any(CommentRequestDto.class), eq("jan_kowalski")))
                .thenReturn(sampleResponseDto);

        mockMvc.perform(post("/api/tasks/{taskId}/comments", 10L)
                        .principal(() -> "jan_kowalski")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.content").value("Przykładowa treść komentarza"))
                .andExpect(jsonPath("$.authorUsername").value("jan_kowalski"));

        verify(commentService, times(1))
                .addComment(eq(10L), any(CommentRequestDto.class), eq("jan_kowalski"));
    }

    @Test
    @WithMockUser(username = "jan_kowalski")
    void shouldReturnBadRequest_WhenAddingCommentWithInvalidBody() throws Exception {
        CommentRequestDto invalidDto = new CommentRequestDto("");

        mockMvc.perform(post("/api/tasks/{taskId}/comments", 10L)
                        .principal(() -> "jan_kowalski")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());

        verify(commentService, never()).addComment(any(), any(), any());
    }

    @Test
    @WithMockUser
    void shouldGetCommentsForTask_AndReturnOkStatus() throws Exception {
        when(commentService.getCommentsByTaskId(10L)).thenReturn(List.of(sampleResponseDto));

        mockMvc.perform(get("/api/tasks/{taskId}/comments", 10L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].content").value("Przykładowa treść komentarza"))
                .andExpect(jsonPath("$[0].authorUsername").value("jan_kowalski"));

        verify(commentService, times(1)).getCommentsByTaskId(10L);
    }

    @Test
    @WithMockUser(username = "jan_kowalski")
    void shouldUpdateComment_AndReturnOkStatus() throws Exception {
        CommentRequestDto updateDto = new CommentRequestDto("Zaktualizowana treść komentarza");
        CommentResponseDto updatedResponse = new CommentResponseDto(
                1L,
                "Zaktualizowana treść komentarza",
                LocalDateTime.now(),
                "jan_kowalski"
        );

        when(commentService.updateComment(eq(1L), any(CommentRequestDto.class), eq("jan_kowalski")))
                .thenReturn(updatedResponse);

        mockMvc.perform(put("/api/tasks/comments/{commentId}", 1L)
                        .principal(() -> "jan_kowalski")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.content").value("Zaktualizowana treść komentarza"))
                .andExpect(jsonPath("$.authorUsername").value("jan_kowalski"));

        verify(commentService, times(1))
                .updateComment(eq(1L), any(CommentRequestDto.class), eq("jan_kowalski"));
    }

    @Test
    @WithMockUser(username = "jan_kowalski")
    void shouldDeleteComment_AndReturnNoContentStatus() throws Exception {
        doNothing().when(commentService).deleteComment(1L, "jan_kowalski");

        mockMvc.perform(delete("/api/tasks/comments/{commentId}", 1L)
                        .principal(() -> "jan_kowalski")
                        .with(csrf()))
                .andExpect(status().isNoContent());

        verify(commentService, times(1)).deleteComment(1L, "jan_kowalski");
    }
}
