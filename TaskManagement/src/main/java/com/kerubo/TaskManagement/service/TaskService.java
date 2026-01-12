package com.kerubo.TaskManagement.service;

import com.kerubo.TaskManagement.dto.TaskDto;
import com.kerubo.TaskManagement.dto.TaskSummaryDto;
import com.kerubo.TaskManagement.exception.ResourceNotFoundException;
import com.kerubo.TaskManagement.model.Category;
import com.kerubo.TaskManagement.model.Task;
import com.kerubo.TaskManagement.repository.CategoryRepository;
import com.kerubo.TaskManagement.repository.Taskrepository;
import org.springframework.transaction.annotation.Transactional;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.PageImpl;


import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaskService {
    private final Taskrepository taskrepository;
    private final CategoryRepository categoryRepository;
    private final ModelMapper modelMapper;

    public TaskService(Taskrepository taskrepository, CategoryRepository categoryRepository, ModelMapper modelMapper) {
        this.taskrepository = taskrepository;
        this.categoryRepository = categoryRepository;
        this.modelMapper = modelMapper;
    }

    @Transactional(readOnly = true)
    public List<TaskDto> getAllTasks() {
        return taskrepository.findAllWithCategory()
                .stream()
                .map(task -> {
                    TaskDto dto = modelMapper.map(task, TaskDto.class);
                    if (task.getCategory() != null) dto.setCategoryId(task.getCategory().getId());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    // This method is transactional
    // If ANY exception happens, rollback
@Transactional
    public TaskDto createTask(TaskDto taskDTO) {
        Task task = modelMapper.map(taskDTO, Task.class);
        if (taskDTO.getCategoryId() != null) {
            Category category = categoryRepository.findById(taskDTO.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
            task.setCategory(category);
        }
        Task savedTask = taskrepository.save(task);
        TaskDto responseDTO = modelMapper.map(savedTask, TaskDto.class);
        if (savedTask.getCategory() != null) responseDTO.setCategoryId(savedTask.getCategory().getId());
        return responseDTO;
    }
    // -------- GET TASK BY ID --------
    public TaskDto getTaskById(Long id) {
        Task task = taskrepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id " + id));
        return convertToDTO(task);
    }

    // -------- UPDATE TASK --------
    @Transactional
    public TaskDto updateTask(Long id, TaskDto taskDTO) {

        Task task = new Task();

        //  Attach client state (including version)
        task.setId(id);
        task.setVersion(taskDTO.getVersion()); // THIS enables locking
        task.setTitle(taskDTO.getTitle());
        task.setDescription(taskDTO.getDescription());
        task.setCompleted(taskDTO.isCompleted());

        if (taskDTO.getCategoryId() != null) {
            Category category = categoryRepository.findById(taskDTO.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
            task.setCategory(category);
        }

        Task saved = taskrepository.save(task); // Hibernate compares versions here
        return convertToDTO(saved);
    }



    // -------- DELETE TASK --------
    @Transactional
    public void deleteTask(Long id) {
        if (!taskrepository.existsById(id)) {
            throw new ResourceNotFoundException("Task not found with id " + id);
        }
        taskrepository.deleteById(id);
    }

    // -------- HELPER METHODS FOR DTO CONVERSION --------
    private TaskDto convertToDTO(Task task) {
        TaskDto dto = modelMapper.map(task, TaskDto.class);
        if (task.getCategory() != null) {
            dto.setCategoryId(task.getCategory().getId());
        }
        if (task.getDueDate() != null) {
            dto.setDueDate(task.getDueDate().toString());
        }
        return dto;
    }

    private Task convertToEntity(TaskDto dto) {
        Task task = modelMapper.map(dto, Task.class);
        if (dto.getDueDate() != null) {
            task.setDueDate(java.time.LocalDate.parse(dto.getDueDate()));
        }
        return task;
    }

    public Page<TaskDto> getTasksPaginatedAndSorted(
            int page,
            int size,
            String sortBy) {

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(sortBy).ascending()
        );

        return taskrepository.findAll(pageable)
                .map(this::convertToDTO);
    }

    public List<TaskDto> searchTasks(String keyword) {
        return taskrepository.findByTitleContainingIgnoreCase(keyword)
                .stream()
                .map(this::convertToDTO)
                .toList();
    }

    public List<TaskDto> getTasksByCategoryName(String name) {
        return taskrepository.findTasksByCategoryName(name)
                .stream()
                .map(this::convertToDTO)
                .toList();
    }

    public Page<TaskDto> getTasksByCategoryPaged(
            String categoryName,
            int page,
            int size,
            String sortBy
    ) {

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(sortBy).descending()
        );
        //JOIN FETCH with filtering and pagination
        // 1️. Get paged IDs
        Page<Long> taskIdsPage =
                taskrepository.findTaskIdsByCategory(categoryName, pageable);

        if (taskIdsPage.isEmpty()) {
            return Page.empty(pageable);
        }

        // 2️ Fetch full entities with JOIN FETCH
        List<Task> tasks =
                taskrepository.findTasksWithCategoryByIds(taskIdsPage.getContent());

        // 3️ Map to DTOs
        List<TaskDto> dtos = tasks.stream()
                .map(task -> {
                    TaskDto dto = modelMapper.map(task, TaskDto.class);
                    dto.setCategoryId(task.getCategory().getId());
                    dto.setCategoryName(task.getCategory().getName());
                    return dto;
                })
                .toList();

        // 4️ Return paged result
        return new PageImpl<>(dtos, pageable, taskIdsPage.getTotalElements());
    }

    public Page<TaskSummaryDto> getTaskSummaries(
            String category,
            int page,
            int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return taskrepository.findTaskSummariesByCategory(category, pageable);
    }
    @Transactional
    public void riskyOperation() {
        taskrepository.save(new Task());

        if (true) {
            throw new RuntimeException("Boom"); // rollback
        }
    }




}
