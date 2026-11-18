package com.taskhub.TaskHub.controller;

import com.taskhub.TaskHub.dto.TaskRequestDto;
import com.taskhub.TaskHub.dto.TaskResponseDto;
import com.taskhub.TaskHub.entity.Task;
import com.taskhub.TaskHub.enums.TaskStatus;
import com.taskhub.TaskHub.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;


    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('PM')")
    public ResponseEntity<TaskResponseDto> createTask(@Valid @RequestBody TaskRequestDto dto){
        TaskResponseDto savedTask =  taskService.createTask(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedTask);
    }
    @GetMapping("/{id}")
    public ResponseEntity<TaskResponseDto> getTaskById(@PathVariable Long id){
        TaskResponseDto dto = taskService.findById(id);
        return ResponseEntity.ok(dto);
    }
@GetMapping
    public ResponseEntity<List<TaskResponseDto>> getAllTasks(){
        List<TaskResponseDto> tasks = taskService.findAllTasks();
        return ResponseEntity.ok(tasks);
}
@PutMapping("/{id}")
    public ResponseEntity<TaskResponseDto> updateTask(@PathVariable Long id,@Valid @RequestBody TaskRequestDto dto){
        TaskResponseDto updatedTask = taskService.updateTask(id,dto);
        return ResponseEntity.ok(updatedTask);
}
@PatchMapping("/{id}/status")
public ResponseEntity<TaskResponseDto> updateTaskStatus(@PathVariable Long id, @RequestParam TaskStatus status, Principal principal){
        TaskResponseDto dto = taskService.updateTaskStatus(id,status,principal.getName());
        return ResponseEntity.ok(dto);
}
@GetMapping("/search")
public ResponseEntity<List<TaskResponseDto>> searchTasks(
        @RequestParam(required = false) TaskStatus status,
        @RequestParam(required = false) String category,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)LocalDateTime createdAfter
        ){
        List<TaskResponseDto> tasks = taskService.searchTasks(status,category,createdAfter);
        return ResponseEntity.ok(tasks);
}
@DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id){
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
}

}
