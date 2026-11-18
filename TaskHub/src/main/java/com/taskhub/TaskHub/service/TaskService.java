package com.taskhub.TaskHub.service;

import com.taskhub.TaskHub.dto.TaskRequestDto;
import com.taskhub.TaskHub.dto.TaskResponseDto;
import com.taskhub.TaskHub.entity.Task;
import com.taskhub.TaskHub.entity.User;
import com.taskhub.TaskHub.enums.Role;
import com.taskhub.TaskHub.enums.TaskStatus;
import com.taskhub.TaskHub.exception.ResourceNotFoundException;
import com.taskhub.TaskHub.repository.TaskRepository;
import com.taskhub.TaskHub.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskService {
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    @Transactional
    public TaskResponseDto createTask(TaskRequestDto dto){
       Task task = new Task();
       task.setTitle(dto.title());
       task.setDescription(dto.description());
       task.setPriority(dto.priority());
       task.setCategory(dto.category());
       task.setStatus(TaskStatus.OPEN);
       if (dto.assigneeId() != null){
           User assignee = userRepository.findById(dto.assigneeId())
                   .orElseThrow(() -> new ResourceNotFoundException("Nie znaleziono użytkownika o ID: " + dto.assigneeId()) );
           task.setAssignee(assignee);
       }
       Task savedTask = taskRepository.save(task);
       return toDto(savedTask);
    }
    public TaskResponseDto findById(Long id){
       Task task = taskRepository.findById(id)
               .orElseThrow(() -> new ResourceNotFoundException("Nie znaleziono zadania o ID: " + id));
       return toDto(task);
    }
    public List<TaskResponseDto> findAllTasks(){
        return taskRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
    @Transactional
    public TaskResponseDto updateTask(Long id,TaskRequestDto requestDto){
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Nie znaleziono zadania o ID: " + id));
        task.setTitle(requestDto.title());
        task.setDescription(requestDto.description());
        task.setPriority(requestDto.priority());
        task.setCategory(requestDto.category());
        if (requestDto.assigneeId() != null){
            User assignee = userRepository.findById(requestDto.assigneeId())
                    .orElseThrow(() -> new ResourceNotFoundException("Nie znaleziono użytkownika o Id: " + requestDto.assigneeId()));
            task.setAssignee(assignee);

        } else {
            task.setAssignee(null);
        }
        task.setUpdatedAt(LocalDateTime.now());
        Task updatedTask = taskRepository.save(task);
        return toDto(updatedTask);
    }
    @Transactional
    public TaskResponseDto updateTaskStatus(Long id, TaskStatus newStatus, String currentUsername) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Nie znaleziono zadania o ID: " + id));

        User currentUser = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new ResourceNotFoundException("Użytkownik nie istnieje"));

        boolean isAdminOrPm = currentUser.getRole() == Role.ROLE_ADMIN || currentUser.getRole() == Role.ROLE_PM;
        boolean isAssignee = task.getAssignee() != null && task.getAssignee().getId().equals(currentUser.getId());


        if (!isAdminOrPm && !isAssignee) {
            throw new AccessDeniedException("Nie masz uprawnień do zmiany statusu tego zadania.");
        }

        task.setStatus(newStatus);
        task.setUpdatedAt(LocalDateTime.now());
        return toDto(taskRepository.save(task));
    }

    @Transactional
    public void deleteTask(Long id){
       if (!taskRepository.existsById(id)){
           throw new ResourceNotFoundException("Nie znaleziono zadania o Id: " + id);

       }
       taskRepository.deleteById(id);

    }
    public List<TaskResponseDto> searchTasks(TaskStatus status, String category, LocalDateTime createdAfter){
        List<Task> tasks;
        if (status != null && category != null){
            tasks = taskRepository.findByStatusAndCategory(status,category);
        } else if (status != null){
            tasks = taskRepository.findByStatus(status);
        } else if (category != null){
            tasks = taskRepository.findByCategory( category);
        }else if (createdAfter != null){
            tasks = taskRepository.findByCreatedAtAfter(createdAfter);
        } else {
            tasks = taskRepository.findAll();
        }
        return tasks.stream()
                .map(this::toDto)
                .toList();
    }
    private TaskResponseDto toDto(Task task){
       return new TaskResponseDto(
               task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getStatus(),
                task.getPriority(),
                task.getCategory(),
                task.getCreatedAt(),
                task.getUpdatedAt(),
               task.getAssignee() != null ? task.getAssignee().getId() : null,
               task.getAssignee() != null ? task.getAssignee().getUsername() : null
        );
    }
    private Task toEntity(TaskRequestDto dto){
        Task task = new Task();
        task.setTitle(dto.title());
        task.setDescription(dto.description());
        task.setPriority(dto.priority());
        task.setCategory(dto.category());
        task.setStatus(TaskStatus.OPEN);
        return task;
    }
}
