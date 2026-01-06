package com.kerubo.TaskManagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.kerubo.TaskManagement.model.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
