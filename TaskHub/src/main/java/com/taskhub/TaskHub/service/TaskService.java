package com.taskhub.TaskHub.service;

import com.taskhub.TaskHub.dto.TaskRequestDto;
import com.taskhub.TaskHub.dto.TaskResponseDto;
import com.taskhub.TaskHub.entity.Task;
import com.taskhub.TaskHub.enums.TaskStatus;
import com.taskhub.TaskHub.repository.TaskRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskService {
    private final TaskRepository taskRepository;

    @Transactional
    public TaskResponseDto createTask(TaskRequestDto dto){
       Task task = toEntity(dto);
       Task savedTask = taskRepository.save(task);
       return toDto(savedTask);
    }
    public TaskResponseDto findById(Long id){
       Task task = taskRepository.findById(id)
               .orElseThrow(() -> new RuntimeException("Nie znaleziono zadania o ID: " + id));
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
                .orElseThrow(() -> new RuntimeException("Nie znaleziono zadania o ID: " + id));
        task.setTitle(requestDto.title());
        task.setDescription(requestDto.description());
        task.setPriority(requestDto.priority());
        task.setCategory(requestDto.category());
        Task updatedTask = taskRepository.save(task);
        return toDto(updatedTask);
    }
    @Transactional
    public void deleteTask(Long id){
       if (!taskRepository.existsById(id)){
           throw new RuntimeException("Nie znaleziono zadania o Id: " + id);

       }
       taskRepository.deleteById(id);
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
                task.getUpdatedAt()
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
