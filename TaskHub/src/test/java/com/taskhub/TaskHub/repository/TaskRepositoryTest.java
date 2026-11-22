package com.taskhub.TaskHub.repository;

import com.taskhub.TaskHub.entity.Task;
import com.taskhub.TaskHub.enums.TaskPriority;
import com.taskhub.TaskHub.enums.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class TaskRepositoryTest {

    @Autowired
    private TaskRepository taskRepository;


    private Task backendTask;
    private Task frontendTask;
    private Task oldBackendTask;

    @BeforeEach
    void setUp() {

        LocalDateTime now = LocalDateTime.now();


        backendTask = new Task();
        backendTask.setTitle("Konfiguracja API");
        backendTask.setDescription("Endpointy REST dla zadań");
        backendTask.setCategory("BACKEND");
        backendTask.setStatus(TaskStatus.IN_PROGRESS);
        backendTask.setPriority(TaskPriority.MEDIUM);
        backendTask.setCreatedAt(now);


        frontendTask = new Task();
        frontendTask.setTitle("Widok listy zadań");
        frontendTask.setDescription("Komponent React/UI");
        frontendTask.setCategory("FRONTEND");
        frontendTask.setStatus(TaskStatus.RESOLVED);
        frontendTask.setPriority(TaskPriority.MEDIUM);
        frontendTask.setCreatedAt(now);


        oldBackendTask = new Task();
        oldBackendTask.setTitle("Stary refaktor bazy");
        oldBackendTask.setDescription("Migracja sprzed kilku dni");
        oldBackendTask.setCategory("BACKEND");
        oldBackendTask.setStatus(TaskStatus.IN_PROGRESS);
        oldBackendTask.setPriority(TaskPriority.MEDIUM);
        oldBackendTask.setCreatedAt(now.minusDays(5));


        taskRepository.save(backendTask);
        taskRepository.save(frontendTask);
        taskRepository.save(oldBackendTask);
    }
    @Test
    void shouldFindTasksByCategory(){
        List<Task> result = taskRepository.findByCategory("BACKEND");
        assertNotNull(result);
        assertEquals(2,result.size());
        assertTrue(result.stream().allMatch(t -> "BACKEND".equals(t.getCategory())));

    }
    @Test
    void shouldFindTasksByStatus(){
        List<Task> result = taskRepository.findByStatus(TaskStatus.IN_PROGRESS);
        assertNotNull(result);
        assertEquals(2,result.size());
        assertTrue(result.stream().allMatch(t -> t.getStatus() == TaskStatus.IN_PROGRESS));
    }
    @Test
    void shouldFindTasksByStatusAndCategory(){
        List<Task> result = taskRepository.findByStatusAndCategory(TaskStatus.RESOLVED,"FRONTEND");
        assertNotNull(result);
        assertEquals(1,result.size());
        assertEquals("Widok listy zadań",result.get(0).getTitle());
    }
    @Test
    void shouldReturnEmptyList_WhenStatusAndCategoryDoNotMatchTogether(){
        List<Task> result = taskRepository.findByStatusAndCategory(TaskStatus.IN_PROGRESS,"FRONTEND");
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
    @Test
    void shouldFindTasksCreatedAfterGivenDate(){
        LocalDateTime cutOffDate = LocalDateTime.now().minusDays(2);
        List<Task> result = taskRepository.findByCreatedAtAfter(cutOffDate);
        assertNotNull(result);
        assertEquals(2,result.size());
        assertTrue(result.stream().noneMatch(t -> t.getTitle().equals("stary refaktor bazy")));
    }
}
