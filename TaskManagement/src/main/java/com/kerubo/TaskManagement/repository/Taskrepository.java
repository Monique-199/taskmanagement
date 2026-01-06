package com.kerubo.TaskManagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.kerubo.TaskManagement.model.Task;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface Taskrepository extends JpaRepository<Task, Long> {
    // Custom query: find tasks by title containing keyword
    List<Task> findByTitleContainingIgnoreCase(String keyword);

    // Custom query: find tasks by category id
    List<Task> findByCategoryId(Long categoryId);

    // JPQL JOIN using entity relationships
    // We JOIN Task with Category using the 'category' field in Task entity
    @Query("SELECT t FROM Task t JOIN t.category c") //equivalent SQL SELECT * FROM tasks t
    //JOIN categories c ON t.category_id = c.id;

    List<Task> findAllTasksWithCategory();

    @Query("SELECT t FROM Task t JOIN t.category c WHERE c.name = :categoryName")
    List<Task> findTasksByCategoryName(@Param("categoryName") String categoryName);

}
