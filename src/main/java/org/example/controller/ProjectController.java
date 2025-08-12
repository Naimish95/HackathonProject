package org.example.controller;

import org.example.entity.Project;
import org.example.entity.User;
import org.example.repository.ProjectRepository;
import org.example.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {
    
    @Autowired
    private ProjectRepository projectRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @GetMapping
    public List<Project> getAllProjects() {
        User user = userRepository.findById(1L).orElse(null);
        return projectRepository.findByOwnerOrderByCreatedAtDesc(user);
    }
    
    @PostMapping
    public ResponseEntity<String> createProject(@RequestBody Project project) {
        System.out.println("Received project creation request: " + project.getTitle());
        try {
            // Create or get demo user
            User user = userRepository.findById(1L).orElse(createDemoUser());
            System.out.println("User found/created: " + user.getName());
            
            project.setOwner(user);
            project.setCreatedAt(LocalDateTime.now());
            
            System.out.println("Saving project to database...");
            Project savedProject = projectRepository.save(project);
            System.out.println("Project saved with ID: " + savedProject.getId());
            
            return ResponseEntity.ok("{\"status\":\"success\",\"id\":" + savedProject.getId() + "}");
        } catch (Exception e) {
            System.err.println("Error creating project: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body("{\"status\":\"error\"}");
        }
    }
    
    private User createDemoUser() {
        // Check if user already exists
        return userRepository.findByEmail("alex@creativeflow.com")
            .orElseGet(() -> {
                User user = new User();
                user.setName("Alex Creator");
                user.setEmail("alex@creativeflow.com");
                user.setRole("creator");
                user.setBio("Gen-Z content creator passionate about tech and lifestyle");
                return userRepository.save(user);
            });
    }
    

    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProject(@PathVariable Long id) {
        return projectRepository.findById(id)
            .map(project -> {
                projectRepository.delete(project);
                return ResponseEntity.ok().build();
            })
            .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/category/{category}")
    public List<Project> getProjectsByCategory(@PathVariable String category) {
        User user = userRepository.findById(1L).orElse(null);
        return projectRepository.findByOwnerAndCategoryOrderByCreatedAtDesc(user, category);
    }
    
    @GetMapping("/test")
    public String testEndpoint() {
        return "API is working!";
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Project> getProject(@PathVariable Long id) {
        return projectRepository.findById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<String> updateProject(@PathVariable Long id, @RequestBody Project projectDetails) {
        try {
            return projectRepository.findById(id)
                .map(project -> {
                    project.setTitle(projectDetails.getTitle());
                    project.setDescription(projectDetails.getDescription());
                    project.setCategory(projectDetails.getCategory());
                    projectRepository.save(project);
                    return ResponseEntity.ok("{\"status\":\"success\"}");
                })
                .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("{\"status\":\"error\"}");
        }
    }
}