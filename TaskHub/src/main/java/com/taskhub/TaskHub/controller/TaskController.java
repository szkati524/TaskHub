package com.taskhub.TaskHub.controller;

import com.taskhub.TaskHub.dto.TaskRequestDto;
import com.taskhub.TaskHub.dto.TaskResponseDto;
import com.taskhub.TaskHub.entity.Task;
import com.taskhub.TaskHub.enums.TaskStatus;
import com.taskhub.TaskHub.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;


    @PostMapping
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
@DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id){
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
}
}
