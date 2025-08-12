package org.example.service;

import org.example.entity.Project;
import org.example.entity.Task;
import org.example.entity.User;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class AIService {
    
    public Map<String, Object> generateProjectSuggestions(User user, String projectType) {
        Map<String, Object> suggestions = new HashMap<>();
        
        // AI-suggested deadlines based on project type
        LocalDateTime suggestedDeadline = calculateSuggestedDeadline(projectType);
        suggestions.put("suggestedDeadline", suggestedDeadline);
        
        // Content ideas based on project type
        List<String> contentIdeas = generateContentIdeas(projectType);
        suggestions.put("contentIdeas", contentIdeas);
        
        // Time allocation suggestions
        Map<String, Integer> timeAllocation = generateTimeAllocation(projectType);
        suggestions.put("timeAllocation", timeAllocation);
        
        // Suggested collaborators (mock data)
        List<String> suggestedCollaborators = generateCollaboratorSuggestions(user.getRole());
        suggestions.put("suggestedCollaborators", suggestedCollaborators);
        
        return suggestions;
    }
    
    public List<String> generateSmartReminders(User user) {
        List<String> reminders = new ArrayList<>();
        
        // Mock AI-generated reminders
        reminders.add("Don't forget to review your content calendar for next week");
        reminders.add("Your freelance project deadline is approaching in 3 days");
        reminders.add("Time to brainstorm new content ideas for your social media");
        reminders.add("Consider taking a break - you've been working for 2 hours straight");
        
        return reminders;
    }
    
    private LocalDateTime calculateSuggestedDeadline(String projectType) {
        LocalDateTime now = LocalDateTime.now();
        
        return switch (projectType.toLowerCase()) {
            case "content" -> now.plusDays(7);
            case "study" -> now.plusDays(14);
            case "freelance" -> now.plusDays(21);
            case "personal" -> now.plusDays(10);
            default -> now.plusDays(7);
        };
    }
    
    private List<String> generateContentIdeas(String projectType) {
        Map<String, List<String>> ideaBank = Map.of(
            "content", Arrays.asList(
                "Behind-the-scenes content creation process",
                "Day-in-the-life vlogs",
                "Tutorial series on your expertise",
                "Collaboration with other creators"
            ),
            "study", Arrays.asList(
                "Study with me sessions",
                "Note-taking techniques",
                "Exam preparation strategies",
                "Subject-specific deep dives"
            ),
            "freelance", Arrays.asList(
                "Portfolio showcase",
                "Client testimonials",
                "Work process documentation",
                "Skill development journey"
            )
        );
        
        return ideaBank.getOrDefault(projectType.toLowerCase(), 
            Arrays.asList("Creative brainstorming", "Goal setting", "Progress tracking"));
    }
    
    private Map<String, Integer> generateTimeAllocation(String projectType) {
        Map<String, Integer> allocation = new HashMap<>();
        
        switch (projectType.toLowerCase()) {
            case "content" -> {
                allocation.put("Planning", 20);
                allocation.put("Creation", 50);
                allocation.put("Editing", 20);
                allocation.put("Publishing", 10);
            }
            case "study" -> {
                allocation.put("Research", 30);
                allocation.put("Note-taking", 25);
                allocation.put("Practice", 35);
                allocation.put("Review", 10);
            }
            case "freelance" -> {
                allocation.put("Client Communication", 15);
                allocation.put("Work Execution", 60);
                allocation.put("Revisions", 15);
                allocation.put("Delivery", 10);
            }
            default -> {
                allocation.put("Planning", 25);
                allocation.put("Execution", 50);
                allocation.put("Review", 25);
            }
        }
        
        return allocation;
    }
    
    private List<String> generateCollaboratorSuggestions(String userRole) {
        Map<String, List<String>> collaborators = Map.of(
            "creator", Arrays.asList("Video Editor", "Graphic Designer", "Social Media Manager"),
            "student", Arrays.asList("Study Buddy", "Tutor", "Research Partner"),
            "freelancer", Arrays.asList("Project Manager", "Designer", "Developer")
        );
        
        return collaborators.getOrDefault(userRole, Arrays.asList("Collaborator", "Mentor", "Peer"));
    }
    
    public Map<String, Object> generateUserStorySuggestions(Project project, String storyName) {
        Map<String, Object> suggestions = new HashMap<>();
        
        // AI-suggested effort based on story complexity
        int suggestedEffort = calculateStoryEffort(storyName, project.getCategory());
        suggestions.put("suggestedEffort", suggestedEffort);
        
        // AI-suggested team member allocation
        String suggestedAllocateTo = suggestTeamMember(project.getCategory(), storyName);
        suggestions.put("suggestedAllocateTo", suggestedAllocateTo);
        
        // AI-suggested deadline
        LocalDateTime suggestedDeadline = calculateStoryDeadline(suggestedEffort);
        suggestions.put("suggestedDeadline", suggestedDeadline);
        
        // AI-suggested description
        String suggestedDescription = generateStoryDescription(storyName, project.getCategory());
        suggestions.put("suggestedDescription", suggestedDescription);
        
        return suggestions;
    }
    
    private int calculateStoryEffort(String storyName, String projectCategory) {
        // Simple AI logic based on keywords and project type
        int baseEffort = switch (projectCategory.toLowerCase()) {
            case "content" -> 3;
            case "study" -> 5;
            case "freelance" -> 8;
            default -> 5;
        };
        
        // Adjust based on story complexity keywords
        String lowerName = storyName.toLowerCase();
        if (lowerName.contains("complex") || lowerName.contains("advanced")) baseEffort += 3;
        if (lowerName.contains("simple") || lowerName.contains("basic")) baseEffort -= 2;
        
        return Math.max(1, Math.min(baseEffort, 13)); // Keep within 1-13 story points
    }
    
    private String suggestTeamMember(String projectCategory, String storyName) {
        Map<String, List<String>> teamMembers = Map.of(
            "content", Arrays.asList("Content Creator", "Video Editor", "Graphic Designer"),
            "study", Arrays.asList("Researcher", "Study Partner", "Subject Expert"),
            "freelance", Arrays.asList("Developer", "Designer", "Project Manager")
        );
        
        List<String> members = teamMembers.getOrDefault(projectCategory.toLowerCase(), 
            Arrays.asList("Team Member", "Collaborator"));
        
        // Simple keyword matching
        String lowerName = storyName.toLowerCase();
        if (lowerName.contains("design")) return "Designer";
        if (lowerName.contains("code") || lowerName.contains("develop")) return "Developer";
        if (lowerName.contains("research")) return "Researcher";
        
        return members.get(0); // Default to first suggestion
    }
    
    private LocalDateTime calculateStoryDeadline(int effort) {
        // Calculate deadline based on effort (1 story point = 1 day)
        return LocalDateTime.now().plusDays(effort);
    }
    
    private String generateStoryDescription(String storyName, String projectCategory) {
        return String.format("As a %s user, I want to %s so that I can achieve my project goals efficiently.", 
            projectCategory, storyName.toLowerCase());
    }
}