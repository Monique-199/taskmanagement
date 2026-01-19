package com.kerubo.TaskManagement.controller;

import com.kerubo.TaskManagement.dto.TaskDto;
import com.kerubo.TaskManagement.exception.ConflictException;
import com.kerubo.TaskManagement.service.TaskService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(
        controllers = TaskController.class,
        excludeAutoConfiguration = {
                org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class,
                org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration.class
        }
)
public class TaskControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean //fake service injected into controller
    private TaskService taskService;

    @Test
    void shouldReturn409WhenOptimisticLockConflictOccurs() throws Exception {
        // GIVEN
        TaskDto dto = new TaskDto();
        dto.setTitle("Updated task");
        dto.setVersion(1L);

        when(taskService.updateTask(eq(1L), any(TaskDto.class)))
                .thenThrow(new ConflictException(
                        "This task was updated by someone else. Please refresh and try again."
                ));

        // WHEN + THEN
        mockMvc.perform(put("/api/tasks/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                          "title": "Updated task",
                          "version": 1
                        }
                    """))
                .andExpect(status().isConflict());
    }

}
