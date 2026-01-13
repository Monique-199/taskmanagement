package com.kerubo.TaskManagement.service;

import com.kerubo.TaskManagement.dto.CategoryDto;
import com.kerubo.TaskManagement.exception.ResourceNotFoundException;
import com.kerubo.TaskManagement.model.Category;
import com.kerubo.TaskManagement.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryService {
    @Autowired
    private CategoryRepository categoryRepository;

    public CategoryDto createCategory(CategoryDto dto) {
        Category category = new Category();
        category.setName(dto.getName());

        Category savedCategory = categoryRepository.save(category);

        dto.setId(savedCategory.getId());
        return dto;
    }

    public List<CategoryDto> getAllCategories() {
        return categoryRepository.findAll()
                .stream()
                .map(cat -> {
                    CategoryDto dto = new CategoryDto();
                    dto.setId(cat.getId());
                    dto.setName(cat.getName());
                    return dto;
                })
                .collect(Collectors.toList());
    }
    public CategoryDto getCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id " + id));

        CategoryDto dto = new CategoryDto();
        dto.setId(category.getId());
        dto.setName(category.getName());
        return dto;
    }

    // UPDATE category
    public CategoryDto updateCategory(Long id, CategoryDto dto) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id " + id));

        category.setName(dto.getName());
        Category updated = categoryRepository.save(category);

        dto.setId(updated.getId());
        return dto;
    }

    // DELETE category
    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Category not found with id " + id));

        if (!category.getTasks().isEmpty()) {
            throw new IllegalStateException(
                    "Cannot delete category with existing tasks");
        }

        categoryRepository.delete(category);
    }


}

