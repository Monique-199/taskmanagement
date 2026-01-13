package com.kerubo.TaskManagement.model;

import jakarta.persistence.*;

import java.util.List;

@Entity
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;
   // FetchType.LAZY -tasks loaded only when needed
    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, fetch = FetchType.LAZY) //CASCADE //saving/deleting category affects tasks
    private List<Task> tasks;

    public Long getId() {
        return id;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
