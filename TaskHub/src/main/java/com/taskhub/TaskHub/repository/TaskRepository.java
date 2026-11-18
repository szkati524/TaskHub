package com.taskhub.TaskHub.repository;

import com.taskhub.TaskHub.entity.Task;
import com.taskhub.TaskHub.enums.TaskStatus;
import jakarta.persistence.Id;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long>, JpaSpecificationExecutor<Task> {
    List<Task> findByCategory(String category);
    List<Task> findByStatus(TaskStatus status);
    List<Task> findByStatusAndCategory(TaskStatus status,String category);
    List<Task> findByCreatedAtAfter(LocalDateTime date);
}
