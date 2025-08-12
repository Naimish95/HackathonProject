package org.example.controller;

import org.example.entity.Note;
import org.example.entity.Project;
import org.example.repository.NoteRepository;
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
@RequestMapping("/api/notes")
public class NoteController {
    
    @Autowired
    private NoteRepository noteRepository;
    
    @Autowired
    private ProjectRepository projectRepository;
    
    private final String UPLOAD_DIR = "uploads/";
    
    @GetMapping("/project/{projectId}")
    public List<Note> getNotesByProject(@PathVariable Long projectId) {
        Project project = projectRepository.findById(projectId).orElse(null);
        return noteRepository.findByProjectOrderByCreatedAtDesc(project);
    }
    
    @PostMapping
    public ResponseEntity<Note> createNote(@RequestBody Note note) {
        note.setCreatedAt(LocalDateTime.now());
        note.setUpdatedAt(LocalDateTime.now());
        Note savedNote = noteRepository.save(note);
        return ResponseEntity.ok(savedNote);
    }
    
    @PostMapping("/voice")
    public ResponseEntity<Note> createVoiceNote(@RequestParam("file") MultipartFile file,
                                               @RequestParam("projectId") Long projectId,
                                               @RequestParam("title") String title) {
        try {
            // Create upload directory if it doesn't exist
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            
            // Save the file
            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(file.getInputStream(), filePath);
            
            // Create note
            Note note = new Note();
            note.setTitle(title);
            note.setType("voice");
            note.setVoiceFilePath(fileName);
            note.setContent("Voice note recorded"); // Placeholder for transcription
            note.setCreatedAt(LocalDateTime.now());
            note.setUpdatedAt(LocalDateTime.now());
            
            Project project = projectRepository.findById(projectId).orElse(null);
            note.setProject(project);
            
            Note savedNote = noteRepository.save(note);
            return ResponseEntity.ok(savedNote);
            
        } catch (IOException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Note> updateNote(@PathVariable Long id, @RequestBody Note noteDetails) {
        return noteRepository.findById(id)
            .map(note -> {
                note.setTitle(noteDetails.getTitle());
                note.setContent(noteDetails.getContent());
                note.setTags(noteDetails.getTags());
                note.setUpdatedAt(LocalDateTime.now());
                return ResponseEntity.ok(noteRepository.save(note));
            })
            .orElse(ResponseEntity.notFound().build());
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteNote(@PathVariable Long id) {
        return noteRepository.findById(id)
            .map(note -> {
                noteRepository.delete(note);
                return ResponseEntity.ok().build();
            })
            .orElse(ResponseEntity.notFound().build());
    }
}