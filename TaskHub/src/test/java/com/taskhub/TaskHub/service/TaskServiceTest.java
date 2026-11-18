package com.taskhub.TaskHub.service;

import com.taskhub.TaskHub.dto.TaskRequestDto;
import com.taskhub.TaskHub.dto.TaskResponseDto;
import com.taskhub.TaskHub.entity.Task;
import com.taskhub.TaskHub.entity.User;
import com.taskhub.TaskHub.enums.Role;
import com.taskhub.TaskHub.enums.TaskPriority;
import com.taskhub.TaskHub.enums.TaskStatus;
import com.taskhub.TaskHub.exception.ResourceNotFoundException;
import com.taskhub.TaskHub.repository.TaskRepository;
import com.taskhub.TaskHub.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.ResourceAccessException;

import javax.security.auth.login.AccountNotFoundException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import org.springframework.security.access.AccessDeniedException;
@ExtendWith(MockitoExtension.class)
public class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TaskService taskService;

    private User adminUser;
    private User workerUser;
    private User unassignedWorker;
    private Task sampleTask;
    @BeforeEach
    void setUp() {
        adminUser = User.builder()
                .id(1L)
                .username("admin")
                .email("admin@taskhub.com")
                .role(Role.ROLE_ADMIN)
                .build();


        workerUser = User.builder()
                .id(2L)
                .username("worker_jan")
                .email("jan@taskhub.com")
                .role(Role.ROLE_EMPLOYEE)
                .build();


        unassignedWorker = User.builder()
                .id(3L)
                .username("worker_obcy")
                .email("obcy@taskhub.com")
                .role(Role.ROLE_EMPLOYEE)
                .build();


        sampleTask = new Task();
        sampleTask.setId(1L);
        sampleTask.setTitle("Testowe zadanie");
        sampleTask.setDescription("Opis zadania");
        sampleTask.setStatus(TaskStatus.OPEN);
        sampleTask.setPriority(TaskPriority.HIGH);
        sampleTask.setCategory("Backend");
        sampleTask.setCreatedAt(LocalDateTime.now());
        sampleTask.setAssignee(workerUser);
    }

   @Test
    void shouldCreateTaskSuccessfully_WhenAssigneeExists(){
       TaskRequestDto requestDto = new TaskRequestDto(
               "Nowe zadanie","Opis",TaskPriority.MEDIUM,"Backend",2L
       );
       when(userRepository.findById(2L)).thenReturn(Optional.of(workerUser));
       when(taskRepository.save(any(Task.class))).thenReturn(sampleTask);
       TaskResponseDto result = taskService.createTask(requestDto);
       assertNotNull(result);
       assertEquals("Testowe zadanie",result.title());
       assertEquals(2L,result.assigneeId());
       verify(userRepository,times(1)).findById(2L);
       verify(taskRepository,times(1)).save(any(Task.class));
   }
   @Test
    void shouldThrowException_WhenCreatingTaskWithNonExistingAssignee(){
        TaskRequestDto requestDto = new TaskRequestDto(
                "Nowe zadanie","Opis",TaskPriority.MEDIUM,"Backend",999L
        );
        when(userRepository.findById(999L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class,() -> {
            taskService.createTask(requestDto);
        });
        verify(taskRepository,never()).save(any(Task.class));
   }
   @Test
    void shouldFindById_Successfully(){
        when(taskRepository.findById(1L)).thenReturn(Optional.of(sampleTask));
        TaskResponseDto result = taskService.findById(1L);
        assertNotNull(result);
        assertEquals(1L,result.id());
        assertEquals("worker_jan",result.assigneeUsername());
   }
   @Test
    void shouldThrowException_WhenTaskNotFoundById(){
        when(taskRepository.findById(55L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class,() -> {
            taskService.findById(55L);
        });
   }
   @Test
    void shouldUpdateTaskStatus_Successfully_WhenUserIsAdmin(){
        when(taskRepository.findById(1L)).thenReturn(Optional.of(sampleTask));
        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(adminUser));
        when(taskRepository.save(any(Task.class))).thenReturn(sampleTask);
        TaskResponseDto result = taskService.updateTaskStatus(1L,TaskStatus.RESOLVED,"admin");
       assertNotNull(result);
       assertEquals(TaskStatus.RESOLVED,sampleTask.getStatus());
       verify(taskRepository,times(1)).save(sampleTask);
   }
   @Test
    void shouldUpdateTaskStatus_Successfully_WhenUserIsAssignee(){
        when(taskRepository.findById(1L)).thenReturn(Optional.of(sampleTask));
        when(userRepository.findByUsername("worker_jan")).thenReturn(Optional.of(workerUser));
        when(taskRepository.save(any(Task.class))).thenReturn(sampleTask);
        TaskResponseDto result = taskService.updateTaskStatus(1L,TaskStatus.IN_PROGRESS,"worker_jan");
        assertNotNull(result);
        verify(taskRepository,times(1)).save(sampleTask);
   }
   @Test
    void shouldThrowAccessDeniedException_WhenWorkerTriesToUpdateUnassignedTask(){
        when(taskRepository.findById(1L)).thenReturn(Optional.of(sampleTask));
        when(userRepository.findByUsername("worker_obcy")).thenReturn(Optional.of(unassignedWorker));
        assertThrows(AccessDeniedException.class,() -> {
            taskService.updateTaskStatus(1L,TaskStatus.RESOLVED,"worker_obcy");
        });
        verify(taskRepository,never()).save(any(Task.class));
   }
   @Test
    void shouldDeleteTask_Successfully(){
        when(taskRepository.existsById(1L)).thenReturn(true);
        doNothing().when(taskRepository).deleteById(1L);
        assertDoesNotThrow(() -> taskService.deleteTask(1L));
        verify(taskRepository,times(1)).deleteById(1L);
   }
   @Test
    void shouldThrowException_WhenDeletingNonExistingTask(){
        when(taskRepository.existsById(99L)).thenReturn(false);
        assertThrows(ResourceNotFoundException.class,() -> {
            taskService.deleteTask(99L);
        });
        verify(taskRepository,never()).deleteById(anyLong());
   }
   @Test
    void shouldSearchTasks_ByStatusAndCategory(){
        TaskStatus status = TaskStatus.OPEN;
        String category = "Backend";
        List<Task> mockList = List.of(sampleTask);
        when(taskRepository.findByStatusAndCategory(status,category)).thenReturn(mockList);
        List<TaskResponseDto> result = taskService.searchTasks(status,category,null);
        assertNotNull(result);
        assertEquals(1,result.size());
        verify(taskRepository,times(1)).findByStatusAndCategory(status,category);
        verify(taskRepository,never()).findAll();
   }
   @Test
    void shouldSearchTasks_ByStatusOnly(){
        TaskStatus status = TaskStatus.OPEN;
        List<Task> mockList = List.of(sampleTask);
        when(taskRepository.findByStatus(status)).thenReturn(mockList);
        List<TaskResponseDto> result = taskService.searchTasks(status,null,null);
        assertNotNull(result);
        assertEquals(1,result.size());
        verify(taskRepository,times(1)).findByStatus(status);
   }
   @Test
    void shouldSearchTasks_ByCategoryOnly(){
        String category = "Backend";
        List<Task> mockList = List.of(sampleTask    );
        when(taskRepository.findByCategory(category)).thenReturn(mockList);
        List<TaskResponseDto> result = taskService.searchTasks(null,category,null);
        assertNotNull(result);
        assertEquals(1,result.size());
        verify(taskRepository,times(1)).findByCategory(category);
   }
   @Test
    void shouldSearchTasks_ByCreatedAfterOnly(){
        LocalDateTime date = LocalDateTime.now().minusDays(1);
        List<Task> mockList = List.of(sampleTask);
        when(taskRepository.findByCreatedAtAfter(date)).thenReturn(mockList);
        List<TaskResponseDto> result = taskService.searchTasks(null,null,date);
        assertNotNull(result);
        assertEquals(1,result.size());
        verify(taskRepository,times(1)).findByCreatedAtAfter(date);
   }
   @Test
    void shouldReturnAllTasks_WhenNoFiltersProvided(){
        List<Task> mockList = List.of(sampleTask    );
        when(taskRepository.findAll()).thenReturn(mockList);
        List<TaskResponseDto> result = taskService.searchTasks(null,null,null);
        assertNotNull(result);
        assertEquals(1,result.size());
        verify(taskRepository,times(1)).findAll();
   }
}