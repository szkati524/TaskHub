package com.taskhub.TaskHub.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.taskhub.TaskHub.dto.TaskRequestDto;
import com.taskhub.TaskHub.dto.TaskResponseDto;
import com.taskhub.TaskHub.enums.TaskPriority;
import com.taskhub.TaskHub.enums.TaskStatus;
import com.taskhub.TaskHub.security.JwtService;
import com.taskhub.TaskHub.service.TaskService;
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

@WebMvcTest(TaskController.class)
public class TaskControllerTest {
    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    @MockitoBean
    private TaskService taskService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserDetailsService userDetailsService;

    private TaskResponseDto sampleResponseDto;
    private TaskRequestDto sampleRequestDto;

    @BeforeEach
    void setUp() {
        LocalDateTime now = LocalDateTime.now();
        sampleRequestDto = new TaskRequestDto(
                "Implementacja API",
                "Przygotowanie endpointów zadań",
                TaskPriority.HIGH,
                "Backend",
                5L
        );
        sampleResponseDto = new TaskResponseDto(
                1L,
                "Implementacja API",
                "Przygotowanie endpointów zadań",
                TaskStatus.IN_PROGRESS,
                TaskPriority.HIGH,
                "Backend",
                now,
                now,
                5L,
                "jan_kowalski"
        );
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldCreateTask_AndReturnCreatedStatus() throws Exception {
        when(taskService.createTask(any(TaskRequestDto.class))).thenReturn(sampleResponseDto);

        mockMvc.perform(post("/api/tasks")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleRequestDto)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("Implementacja API"))
                .andExpect(jsonPath("$.priority").value("HIGH"))
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"));

        verify(taskService, times(1)).createTask(any(TaskRequestDto.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldReturnBadRequest_WhenCreatingTaskWithInvalidData() throws Exception {
        TaskRequestDto invalidDto = new TaskRequestDto(
                "ab",
                "Opis",
                null,
                "Backend",
                5L
        );

        mockMvc.perform(post("/api/tasks")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());

        verify(taskService, never()).createTask(any());
    }

    @Test
    @WithMockUser
    void shouldGetTaskById_AndReturnOkStatus() throws Exception {
        when(taskService.findById(1L)).thenReturn(sampleResponseDto);

        mockMvc.perform(get("/api/tasks/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("Implementacja API"));

        verify(taskService, times(1)).findById(1L);
    }

    @Test
    @WithMockUser
    void shouldGetAllTasks_AndReturnOkStatus() throws Exception {
        when(taskService.findAllTasks()).thenReturn(List.of(sampleResponseDto));

        mockMvc.perform(get("/api/tasks"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1L));

        verify(taskService, times(1)).findAllTasks();
    }

    @Test
    @WithMockUser
    void shouldUpdateTask_AndReturnOkStatus() throws Exception {
        when(taskService.updateTask(eq(1L), any(TaskRequestDto.class))).thenReturn(sampleResponseDto);

        mockMvc.perform(put("/api/tasks/{id}", 1L)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleRequestDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("Implementacja API"));

        verify(taskService, times(1)).updateTask(eq(1L), any(TaskRequestDto.class));
    }

    @Test
    @WithMockUser(username = "jan_kowalski")
    void shouldUpdateTaskStatus_AndReturnOkStatus() throws Exception {
        when(taskService.updateTaskStatus(1L, TaskStatus.IN_PROGRESS, "jan_kowalski")).thenReturn(sampleResponseDto);

        mockMvc.perform(patch("/api/tasks/{id}/status", 1L)
                        .principal(() -> "jan_kowalski")
                        .param("status", TaskStatus.IN_PROGRESS.name())
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L));

        verify(taskService, times(1)).updateTaskStatus(1L, TaskStatus.IN_PROGRESS, "jan_kowalski");
    }

    @Test
    @WithMockUser
    void shouldSearchTasks_AndReturnOkStatus() throws Exception {
        LocalDateTime createdAfter = LocalDateTime.of(2026, 1, 1, 10, 0, 0);
        when(taskService.searchTasks(TaskStatus.IN_PROGRESS, "Backend", createdAfter))
                .thenReturn(List.of(sampleResponseDto));

        mockMvc.perform(get("/api/tasks/search")
                        .param("status", "IN_PROGRESS")
                        .param("category", "Backend")
                        .param("createdAfter", "2026-01-01T10:00:00"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1L));

        verify(taskService, times(1)).searchTasks(TaskStatus.IN_PROGRESS, "Backend", createdAfter);
    }

    @Test
    @WithMockUser
    void shouldDeleteTask_AndReturnNoContentStatus() throws Exception {
        doNothing().when(taskService).deleteTask(1L);

        mockMvc.perform(delete("/api/tasks/{id}", 1L)
                        .with(csrf()))
                .andExpect(status().isNoContent());

        verify(taskService, times(1)).deleteTask(1L);
    }
}
