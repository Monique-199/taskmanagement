package com.kerubo.TaskManagement.service;

import com.kerubo.TaskManagement.dto.TaskDto;
import com.kerubo.TaskManagement.exception.ResourceNotFoundException;
import com.kerubo.TaskManagement.model.Category;
import com.kerubo.TaskManagement.model.Task;
import com.kerubo.TaskManagement.repository.CategoryRepository;
import com.kerubo.TaskManagement.repository.Taskrepository;
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
        Task existingTask = new Task();
        existingTask.setId(1L);
        existingTask.setTitle("Old Title");

        TaskDto dto = new TaskDto();
        dto.setTitle("New Title");

        TaskDto resultDto = new TaskDto();
        resultDto.setTitle("New Title");

        when(taskRepository.findById(1L))
                .thenReturn(Optional.of(existingTask));

        when(taskRepository.save(any(Task.class)))
                .thenReturn(existingTask);

        when(modelMapper.map(existingTask, TaskDto.class))
                .thenReturn(resultDto);

        // WHEN
        TaskDto result = taskService.updateTask(1L, dto);

        // THEN
        assertEquals("New Title", result.getTitle());

        verify(taskRepository).save(any(Task.class));
    }

    @Test
    void shouldThrowExceptionWhenUpdatingNonExistingTask() {
        // GIVEN
        TaskDto dto = new TaskDto();
        dto.setTitle("Doesn't matter");

        when(taskRepository.findById(99L))
                .thenReturn(Optional.empty());

        // THEN
        assertThrows(ResourceNotFoundException.class,
                () -> taskService.updateTask(99L, dto));

        verify(taskRepository, never()).save(any());
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

}
