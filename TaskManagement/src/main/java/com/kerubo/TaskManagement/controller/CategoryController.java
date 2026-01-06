package com.kerubo.TaskManagement.controller;

import com.kerubo.TaskManagement.dto.CategoryDto;
import com.kerubo.TaskManagement.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categories")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    @PostMapping
    public CategoryDto createCategory(@RequestBody CategoryDto dto) {
        return categoryService.createCategory(dto);
    }
    @GetMapping
    public List<CategoryDto> getAllCategories() {
        return categoryService.getAllCategories();
    }

    // GET BY ID
    @GetMapping("/{id}")
    public CategoryDto getCategoryById(@PathVariable Long id) {
        return categoryService.getCategoryById(id);
    }

    // UPDATE
    @PutMapping("/{id}")
    public CategoryDto updateCategory(@PathVariable Long id, @RequestBody CategoryDto dto) {
        return categoryService.updateCategory(id, dto);
    }


    // DELETE
    @DeleteMapping("/{id}")
    public void deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
    }
}
