package org.example.controller;

import org.example.entity.UserStory;
import org.example.entity.Project;
import org.example.repository.UserStoryRepository;
import org.example.repository.ProjectRepository;
import org.example.service.AIService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/user-stories")
public class UserStoryController {
    
    @Autowired
    private UserStoryRepository userStoryRepository;
    
    @Autowired
    private ProjectRepository projectRepository;
    
    @Autowired
    private AIService aiService;
    
    @GetMapping("/project/{projectId}")
    public List<UserStory> getUserStoriesByProject(@PathVariable Long projectId) {
        Project project = projectRepository.findById(projectId).orElse(null);
        return userStoryRepository.findByProjectOrderByCreatedAtDesc(project);
    }
    
    @PostMapping
    public ResponseEntity<String> createUserStory(@RequestBody UserStory userStory) {
        try {
            userStory.setCreatedAt(LocalDateTime.now());
            UserStory savedStory = userStoryRepository.save(userStory);
            return ResponseEntity.ok("{\"status\":\"success\",\"id\":" + savedStory.getId() + "}");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("{\"status\":\"error\"}");
        }
    }
    
    @PostMapping("/ai-suggest/{projectId}")
    public ResponseEntity<Map<String, Object>> getAISuggestionsForStory(@PathVariable Long projectId, @RequestBody Map<String, String> request) {
        try {
            Project project = projectRepository.findById(projectId).orElse(null);
            String storyName = request.get("name");
            
            Map<String, Object> suggestions = aiService.generateUserStorySuggestions(project, storyName);
            return ResponseEntity.ok(suggestions);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Failed to get AI suggestions"));
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<String> updateUserStory(@PathVariable Long id, @RequestBody UserStory storyDetails) {
        try {
            return userStoryRepository.findById(id)
                .map(story -> {
                    story.setName(storyDetails.getName());
                    story.setDescription(storyDetails.getDescription());
                    story.setEffort(storyDetails.getEffort());
                    story.setAllocateTo(storyDetails.getAllocateTo());
                    story.setStatus(storyDetails.getStatus());
                    story.setDeadline(storyDetails.getDeadline());
                    userStoryRepository.save(story);
                    return ResponseEntity.ok("{\"status\":\"success\"}");
                })
                .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("{\"status\":\"error\"}");
        }
    }
}