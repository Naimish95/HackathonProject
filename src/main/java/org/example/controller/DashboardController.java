package org.example.controller;

import org.example.entity.Project;
import org.example.entity.User;
import org.example.repository.ProjectRepository;
import org.example.repository.UserRepository;
import org.example.service.AIService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@Controller
public class DashboardController {
    
    @Autowired
    private ProjectRepository projectRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private AIService aiService;
    
    @GetMapping("/")
    public String dashboard(Model model) {
        // Mock user for demo (in real app, get from session/security context)
        User user = userRepository.findById(1L).orElse(createDemoUser());
        
        List<Project> recentProjects = projectRepository.findByOwnerOrderByCreatedAtDesc(user);
        List<String> aiReminders = aiService.generateSmartReminders(user);
        
        model.addAttribute("user", user);
        model.addAttribute("recentProjects", recentProjects);
        model.addAttribute("aiReminders", aiReminders);
        
        return "dashboard";
    }
    
    @GetMapping("/projects")
    public String projects(Model model) {
        User user = userRepository.findById(1L).orElse(createDemoUser());
        List<Project> projects = projectRepository.findByOwnerOrderByCreatedAtDesc(user);
        
        model.addAttribute("projects", projects);
        model.addAttribute("user", user);
        
        return "projects";
    }
    
    @GetMapping("/projects/{id}")
    public String projectDetail(@PathVariable Long id, Model model) {
        Project project = projectRepository.findById(id).orElse(null);
        User user = userRepository.findById(1L).orElse(createDemoUser());
        
        model.addAttribute("project", project);
        model.addAttribute("user", user);
        
        return "project-detail";
    }
    
    @PostMapping("/api/ai-suggestions")
    @ResponseBody
    public Map<String, Object> getAISuggestions(@RequestParam String projectType) {
        User user = userRepository.findById(1L).orElse(createDemoUser());
        return aiService.generateProjectSuggestions(user, projectType);
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
}