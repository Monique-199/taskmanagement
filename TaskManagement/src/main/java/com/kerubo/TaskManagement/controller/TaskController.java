package com.kerubo.TaskManagement.controller;

import com.kerubo.TaskManagement.dto.TaskDto;
import com.kerubo.TaskManagement.dto.TaskSummaryDto;
import com.kerubo.TaskManagement.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping
    public List<TaskDto> getTasks() {
        return taskService.getAllTasks();
    }

    @PostMapping
    public TaskDto createTask(@Valid @RequestBody TaskDto taskDTO) {
        return taskService.createTask(taskDTO);
    }

    // -------- GET TASK BY ID --------
    @GetMapping("/{id}")
    public TaskDto getTaskById(@PathVariable Long id) {
        return taskService.getTaskById(id);
    }



    // -------- UPDATE TASK --------
    @PutMapping("/{id}")
    public TaskDto updateTask(
            @PathVariable Long id,
            @Valid @RequestBody TaskDto taskDto) {
        return taskService.updateTask(id, taskDto);
    }

    // -------- DELETE TASK --------
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public void deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
    }

    @GetMapping("/paginated-sorted")
    public Page<TaskDto> getTasksPaginatedAndSorted(
            @RequestParam int page,
            @RequestParam int size,
            @RequestParam String sortBy) {

        return taskService.getTasksPaginatedAndSorted(page, size, sortBy);
    }

    @GetMapping("/search")
    public List<TaskDto> searchTasks(@RequestParam String keyword) {
        return taskService.searchTasks(keyword);
    }

    @GetMapping("/by-category-name")
    public List<TaskDto> getTasksByCategoryName(@RequestParam String name) {
        return taskService.getTasksByCategoryName(name);
    }

    @GetMapping("/search/paged")
    public Page<TaskDto> searchTasks(
            @RequestParam String category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "id") String sortBy
    ) {
        return taskService.getTasksByCategoryPaged(category, page, size, sortBy);
    }

    @GetMapping("/summary")
    public Page<TaskSummaryDto> getTaskSummaries(
            @RequestParam String category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ) {
        return taskService.getTaskSummaries(category, page, size);
    }



}
