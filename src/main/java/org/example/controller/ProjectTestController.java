package org.example.controller;

import org.example.entity.Project;
import org.example.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/check")
public class ProjectTestController {
    
    @Autowired
    private ProjectRepository projectRepository;
    
    @GetMapping("/projects")
    public List<Project> getAllProjects() {
        System.out.println("Checking all projects in database...");
        List<Project> projects = projectRepository.findAll();
        System.out.println("Found " + projects.size() + " projects");
        return projects;
    }
    
    @GetMapping("/count")
    public long getProjectCount() {
        long count = projectRepository.count();
        System.out.println("Total projects in database: " + count);
        return count;
    }
}