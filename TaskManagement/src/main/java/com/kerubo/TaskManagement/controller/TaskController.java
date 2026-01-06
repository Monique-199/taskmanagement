package com.kerubo.TaskManagement.controller;

import com.kerubo.TaskManagement.dto.TaskDto;
import com.kerubo.TaskManagement.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

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
    @DeleteMapping("/{id}")
    public void deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
    }
}
