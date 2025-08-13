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
import org.example.entity.Task;
import org.example.entity.Note;
import org.example.repository.TaskRepository;
import org.example.repository.NoteRepository;
import java.time.LocalDateTime;
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
    
    @Autowired
    private TaskRepository taskRepository;
    
    @Autowired
    private NoteRepository noteRepository;
    
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
        System.out.println("Loading project detail for ID: " + id);
        Project project = projectRepository.findById(id).orElse(null);
        if (project == null) {
            System.out.println("Project not found with ID: " + id);
            return "redirect:/";
        }
        System.out.println("Project found: " + project.getTitle());
        
        User user = userRepository.findById(1L).orElse(createDemoUser());
        
        // Explicitly load tasks and notes
        List<Task> tasks = taskRepository.findByProjectOrderByCreatedAtDesc(project);
        List<Note> notes = noteRepository.findByProjectOrderByCreatedAtDesc(project);
        
        System.out.println("Found " + tasks.size() + " tasks for project " + id);
        for (Task task : tasks) {
            System.out.println("Task: " + task.getTitle() + " (ID: " + task.getId() + ")");
        }
        
        // Set tasks and notes on project
        project.setTasks(tasks);
        project.setNotes(notes);
        
        // Calculate project statistics
        int totalTasks = tasks.size();
        int completedTasks = (int) tasks.stream().filter(t -> "completed".equals(t.getStatus())).count();
        int totalNotes = notes.size();
        
        model.addAttribute("project", project);
        model.addAttribute("user", user);
        model.addAttribute("totalTasks", totalTasks);
        model.addAttribute("completedTasks", completedTasks);
        model.addAttribute("totalNotes", totalNotes);
        
        System.out.println("Model attributes set - totalTasks: " + totalTasks + ", totalNotes: " + totalNotes);
        System.out.println("Returning project-detail view");
        
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
                User savedUser = userRepository.save(user);
                createSampleProjects(savedUser);
                return savedUser;
            });
    }
    
    private void createSampleProjects(User user) {
        if (projectRepository.findByOwnerOrderByCreatedAtDesc(user).isEmpty()) {
            // Create sample project
            Project project = new Project();
            project.setTitle("YouTube Channel Launch");
            project.setDescription("Launch my new tech review YouTube channel with 10 videos");
            project.setCategory("content");
            project.setStatus("active");
            project.setColor("bg-pink-400");
            project.setOwner(user);
            project.setDeadline(LocalDateTime.now().plusDays(30));
            Project savedProject = projectRepository.save(project);
            
            // Add sample tasks
            Task task1 = new Task();
            task1.setTitle("Create channel banner");
            task1.setDescription("Design and upload channel banner with brand colors");
            task1.setPriority("high");
            task1.setStatus("completed");
            task1.setProject(savedProject);
            task1.setDeadline(LocalDateTime.now().plusDays(5));
            taskRepository.save(task1);
            
            Task task2 = new Task();
            task2.setTitle("Record first video");
            task2.setDescription("Record introduction video about the channel");
            task2.setPriority("urgent");
            task2.setStatus("in_progress");
            task2.setProject(savedProject);
            task2.setDeadline(LocalDateTime.now().plusDays(7));
            taskRepository.save(task2);
            
            // Add sample notes
            Note note1 = new Note();
            note1.setTitle("Video Ideas");
            note1.setContent("1. iPhone 15 Pro Review\n2. Best Budget Laptops 2024\n3. Tech Setup Tour");
            note1.setType("text");
            note1.setProject(savedProject);
            noteRepository.save(note1);
        }
    }
}