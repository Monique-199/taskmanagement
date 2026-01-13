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
}
