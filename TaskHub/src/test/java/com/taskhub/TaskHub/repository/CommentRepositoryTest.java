package com.taskhub.TaskHub.repository;

import com.taskhub.TaskHub.dto.UserRequestDto;
import com.taskhub.TaskHub.entity.Comment;
import com.taskhub.TaskHub.entity.Task;
import com.taskhub.TaskHub.entity.User;
import com.taskhub.TaskHub.enums.Role;
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
public class CommentRepositoryTest {
    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    private Task task1;

    private Task task2;
    private User author;

    @BeforeEach
    void setUp() {
        author = userRepository.saveAndFlush(User.builder()
                .username("jan_kowalski")
                .email("jan@taskhub.com")
                .password("secret")
                .role(Role.ROLE_EMPLOYEE)
                .build());


        task1 = new Task();
        task1.setTitle("Zadanie nr 1");
        task1.setDescription("Opis pierwszego zadania");
        task1.setPriority(TaskPriority.MEDIUM);
        task1.setStatus(TaskStatus.IN_PROGRESS);
        task1 = taskRepository.saveAndFlush(task1);


        task2 = new Task();
        task2.setTitle("Zadanie nr 2");
        task2.setDescription("Opis drugiego zadania");
        task2.setPriority(TaskPriority.MEDIUM);
        task2.setStatus(TaskStatus.IN_PROGRESS);
        task2 = taskRepository.saveAndFlush(task2);
    }
    @Test
    void shouldFindCommentsByTaskId_WhenCommentsExistForTask(){
        Comment comment1ForTask = new Comment();
        comment1ForTask.setContent("Pierwszy komentarz do zadania 1");
        comment1ForTask.setTask(task1);
        comment1ForTask.setAuthor(author);
        comment1ForTask.setCreatedAt(LocalDateTime.now());
        Comment comment2ForTask1 = new Comment();
        comment2ForTask1.setContent("Drugi komentarz do zadania 1");
        comment2ForTask1.setTask(task1);
        comment2ForTask1.setAuthor(author);
        comment2ForTask1.setCreatedAt(LocalDateTime.now());

        Comment commentForTask2 = new Comment();
        commentForTask2.setContent("Komentarz do innego zadania (task2)");
        commentForTask2.setTask(task2);
        commentForTask2.setAuthor(author);
        commentForTask2.setCreatedAt(LocalDateTime.now());
        commentRepository.save(comment1ForTask);
        commentRepository.save(comment2ForTask1);
        commentRepository.save(commentForTask2);
        List<Comment> foundComments = commentRepository.findByTaskId(task1.getId());
        assertNotNull(foundComments );
        assertEquals(2,foundComments.size());
        assertTrue(foundComments.stream().allMatch(c -> c.getTask().getId().equals(task1.getId())));

    }
    @Test
    void shouldReturnEmptyList_WhenNoCommentsExsitForTask(){
        List<Comment> foundComments = commentRepository.findByTaskId(task1.getId());
        assertNotNull(foundComments);
        assertTrue(foundComments.isEmpty());
    }
}
