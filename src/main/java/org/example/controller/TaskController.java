package org.example.controller;

import org.example.entity.Task;
import org.example.entity.Project;
import org.example.repository.TaskRepository;
import org.example.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {
    
    @Autowired
    private TaskRepository taskRepository;
    
    @Autowired
    private ProjectRepository projectRepository;
    
    @GetMapping("/project/{projectId}")
    public List<Task> getTasksByProject(@PathVariable Long projectId) {
        Project project = projectRepository.findById(projectId).orElse(null);
        return taskRepository.findByProjectOrderByCreatedAtDesc(project);
    }
    
    @PostMapping("/{projectId}")
    public ResponseEntity<String> createTask(@PathVariable Long projectId, @RequestBody Task task) {
        System.out.println("Creating task: " + task.getTitle() + " for project: " + projectId);
        try {
            Project project = projectRepository.findById(projectId).orElse(null);
            if (project == null) {
                System.out.println("Project not found: " + projectId);
                return ResponseEntity.status(400).body("{\"status\":\"error\",\"message\":\"Project not found\"}");
            }
            
            task.setProject(project);
            task.setCreatedAt(LocalDateTime.now());
            Task savedTask = taskRepository.save(task);
            System.out.println("Task saved with ID: " + savedTask.getId());
            return ResponseEntity.ok("{\"status\":\"success\",\"id\":" + savedTask.getId() + "}");
        } catch (Exception e) {
            System.err.println("Error creating task: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body("{\"status\":\"error\",\"message\":\"" + e.getMessage() + "\"}");
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<String> updateTask(@PathVariable Long id, @RequestBody Task taskDetails) {
        try {
            return taskRepository.findById(id)
                .map(task -> {
                    task.setTitle(taskDetails.getTitle());
                    task.setDescription(taskDetails.getDescription());
                    task.setPriority(taskDetails.getPriority());
                    task.setStatus(taskDetails.getStatus());
                    task.setEstimatedHours(taskDetails.getEstimatedHours());
                    task.setDueDate(taskDetails.getDueDate());
                    task.setDeadline(taskDetails.getDeadline());
                    
                    if ("completed".equals(taskDetails.getStatus()) && task.getCompletedAt() == null) {
                        task.setCompletedAt(LocalDateTime.now());
                    }
                    
                    taskRepository.save(task);
                    return ResponseEntity.ok("{\"status\":\"success\"}");
                })
                .orElse(ResponseEntity.status(404).body("{\"status\":\"not_found\",\"message\":\"Task with id " + id + " not found\"}"));
        } catch (Exception e) {
            System.err.println("Error updating task: " + e.getMessage());
            return ResponseEntity.status(500).body("{\"status\":\"error\"}");
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Task> getTask(@PathVariable Long id) {
        return taskRepository.findById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTask(@PathVariable Long id) {
        return taskRepository.findById(id)
            .map(task -> {
                taskRepository.delete(task);
                return ResponseEntity.ok().build();
            })
            .orElse(ResponseEntity.notFound().build());
    }
    
    @PutMapping("/{id}/status")
    public ResponseEntity<Task> updateTaskStatus(@PathVariable Long id, @RequestParam String status) {
        return taskRepository.findById(id)
            .map(task -> {
                task.setStatus(status);
                if ("completed".equals(status)) {
                    task.setCompletedAt(LocalDateTime.now());
                }
                return ResponseEntity.ok(taskRepository.save(task));
            })
            .orElse(ResponseEntity.notFound().build());
    }
}