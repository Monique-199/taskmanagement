package com.kerubo.TaskManagement.dto;

public class TaskSummaryDto {
    private Long id;
    private String title;
    private boolean completed;
    private String categoryName;

    public TaskSummaryDto(Long id, String title, boolean completed, String categoryName) {
        this.id = id;
        this.title = title;
        this.completed = completed;
        this.categoryName = categoryName;
    }
    //getters
    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public boolean isCompleted() {
        return completed;
    }

}
