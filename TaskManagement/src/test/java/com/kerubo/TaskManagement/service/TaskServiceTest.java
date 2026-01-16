package com.kerubo.TaskManagement.service;

import com.kerubo.TaskManagement.dto.TaskDto;
import com.kerubo.TaskManagement.exception.ConflictException;
import com.kerubo.TaskManagement.exception.ResourceNotFoundException;
import com.kerubo.TaskManagement.model.Category;
import com.kerubo.TaskManagement.model.Task;
import com.kerubo.TaskManagement.repository.CategoryRepository;
import com.kerubo.TaskManagement.repository.Taskrepository;
import jakarta.persistence.OptimisticLockException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private Taskrepository taskRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private TaskService taskService;

    //  Test 1: Create task successfully
    @Test
    void shouldCreateTaskSuccessfully() {
        // GIVEN
        TaskDto dto = new TaskDto();
        dto.setTitle("Learn Testing");
        dto.setCategoryId(1L);

        Category category = new Category();
        category.setId(1L);

        Task task = new Task();
        task.setTitle("Learn Testing");

        // WHEN
        when(categoryRepository.findById(1L))
                .thenReturn(Optional.of(category));
        when(modelMapper.map(dto, Task.class)).thenReturn(task);
        when(taskRepository.save(task)).thenReturn(task);
        when(modelMapper.map(task, TaskDto.class)).thenReturn(dto);

        // THEN
        TaskDto result = taskService.createTask(dto);

        assertNotNull(result);
        assertEquals("Learn Testing", result.getTitle());
        verify(taskRepository, times(1)).save(task);
    }

    //  Test 2: Category not found
    @Test
    void shouldThrowExceptionWhenCategoryNotFound() {
        TaskDto dto = new TaskDto();
        dto.setCategoryId(99L);

        when(categoryRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> taskService.createTask(dto));

        verify(taskRepository, never()).save(any());
    }

    @Test
    void shouldUpdateTaskSuccessfully() {
        // GIVEN
        TaskDto inputDto = new TaskDto();
        inputDto.setTitle("New Title");
        inputDto.setCompleted(true);
        inputDto.setVersion(1L); // IMPORTANT for optimistic locking

        Task savedTask = new Task();
        savedTask.setId(1L);
        savedTask.setTitle("New Title");
        savedTask.setCompleted(true);
        savedTask.setVersion(2L); // incremented version

        TaskDto outputDto = new TaskDto();
        outputDto.setTitle("New Title");

        when(taskRepository.save(any(Task.class)))
                .thenReturn(savedTask);

        when(modelMapper.map(any(Task.class), eq(TaskDto.class)))
                .thenReturn(outputDto);

        // WHEN
        TaskDto result = taskService.updateTask(1L, inputDto);

        // THEN
        assertNotNull(result);
        assertEquals("New Title", result.getTitle());

        verify(taskRepository).save(any(Task.class));
    }


    @Test
    void shouldFailWhenUpdatingWithMissingVersion() {
        // GIVEN
        TaskDto dto = new TaskDto();
        dto.setTitle("Doesn't matter");

        // THEN
        assertThrows(NullPointerException.class,
                () -> taskService.updateTask(99L, dto));

        verify(taskRepository).save(any(Task.class));
    }


    @Test
    void shouldDeleteTaskSuccessfully() {
        // GIVEN
        when(taskRepository.existsById(1L))
                .thenReturn(true);

        // WHEN
        taskService.deleteTask(1L);

        // THEN
        verify(taskRepository).existsById(1L);
        verify(taskRepository).deleteById(1L);
    }

    @Test
    void shouldThrowExceptionWhenDeletingNonExistingTask() {
        // GIVEN
        when(taskRepository.existsById(100L))
                .thenReturn(false);

        // THEN
        assertThrows(ResourceNotFoundException.class,
                () -> taskService.deleteTask(100L));

        verify(taskRepository, never()).deleteById(any());
    }

    @Test
    void shouldThrowOptimisticLockExceptionWhenVersionMismatch() {
        // GIVEN
        TaskDto dto = new TaskDto();
        dto.setId(99L);
        dto.setTitle("Old toy");
        dto.setVersion(1L); //  old magic number

        // Mockito magic: save goes BOOM
        when(taskRepository.save(any(Task.class)))
                .thenThrow(new OptimisticLockException("Version mismatch"));

        // THEN
        assertThrows(ConflictException.class,
                () -> taskService.updateTask(99L, dto));

        verify(taskRepository).save(any(Task.class));
    }

    @Test
    void shouldConvertOptimisticLockExceptionToConflictException() {
        // GIVEN
        TaskDto dto = new TaskDto();
        dto.setId(1L);
        dto.setTitle("Toy");
        dto.setVersion(1L);

        when(taskRepository.save(any(Task.class)))
                .thenThrow(new OptimisticLockException("Boom"));

        // THEN
        assertThrows(ConflictException.class,
                () -> taskService.updateTask(1L, dto));
    }

}
