package org.example.controller;

import org.example.entity.Moodboard;
import org.example.entity.Project;
import org.example.repository.MoodboardRepository;
import org.example.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.time.LocalDateTime;
import java.util.List;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/api/moodboards")
public class MoodboardController {
    
    @Autowired
    private MoodboardRepository moodboardRepository;
    
    @Autowired
    private ProjectRepository projectRepository;
    
    private final String UPLOAD_DIR = "uploads/moodboards/";
    
    @GetMapping("/project/{projectId}")
    public List<Moodboard> getMoodboardsByProject(@PathVariable Long projectId) {
        Project project = projectRepository.findById(projectId).orElse(null);
        return moodboardRepository.findByProjectOrderByCreatedAtDesc(project);
    }
    
    @PostMapping("/upload")
    public ResponseEntity<String> uploadMoodboardImage(@RequestParam("file") MultipartFile file,
                                                      @RequestParam("projectId") Long projectId,
                                                      @RequestParam("name") String name,
                                                      @RequestParam(value = "x", defaultValue = "0") Integer x,
                                                      @RequestParam(value = "y", defaultValue = "0") Integer y) {
        try {
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            
            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(file.getInputStream(), filePath);
            
            Moodboard moodboard = new Moodboard();
            moodboard.setName(name);
            moodboard.setImagePath(fileName);
            moodboard.setPositionX(x);
            moodboard.setPositionY(y);
            moodboard.setCreatedAt(LocalDateTime.now());
            
            Project project = projectRepository.findById(projectId).orElse(null);
            moodboard.setProject(project);
            
            Moodboard saved = moodboardRepository.save(moodboard);
            return ResponseEntity.ok("{\"status\":\"success\",\"id\":" + saved.getId() + "}");
            
        } catch (IOException e) {
            return ResponseEntity.status(500).body("{\"status\":\"error\"}");
        }
    }
    
    @PutMapping("/{id}/position")
    public ResponseEntity<String> updateMoodboardPosition(@PathVariable Long id, 
                                                         @RequestParam Integer x, 
                                                         @RequestParam Integer y) {
        try {
            return moodboardRepository.findById(id)
                .map(moodboard -> {
                    moodboard.setPositionX(x);
                    moodboard.setPositionY(y);
                    moodboardRepository.save(moodboard);
                    return ResponseEntity.ok("{\"status\":\"success\"}");
                })
                .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("{\"status\":\"error\"}");
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteMoodboard(@PathVariable Long id) {
        try {
            return moodboardRepository.findById(id)
                .map(moodboard -> {
                    moodboardRepository.delete(moodboard);
                    return ResponseEntity.ok("{\"status\":\"success\"}");
                })
                .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("{\"status\":\"error\"}");
        }
    }
}