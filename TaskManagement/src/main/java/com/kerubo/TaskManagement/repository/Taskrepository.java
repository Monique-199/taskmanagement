package com.kerubo.TaskManagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.kerubo.TaskManagement.model.Task;

import java.util.List;

public interface Taskrepository extends JpaRepository<Task, Long> {
    // Custom query: find tasks by title containing keyword
    List<Task> findByTitleContainingIgnoreCase(String keyword);

    // Custom query: find tasks by category id
    List<Task> findByCategoryId(Long categoryId);


}
