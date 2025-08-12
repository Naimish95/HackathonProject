package org.example.controller;

import org.example.entity.Template;
import org.example.entity.User;
import org.example.repository.TemplateRepository;
import org.example.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;

@Controller
public class TemplateController {
    
    @Autowired
    private TemplateRepository templateRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @GetMapping("/templates")
    public String templates(Model model) {
        List<Template> publicTemplates = templateRepository.findByIsPublicTrueOrderByUsageCountDesc();
        User user = userRepository.findById(1L).orElse(null);
        List<Template> userTemplates = templateRepository.findByCreatorIdOrderByCreatedAtDesc(user.getId());
        
        model.addAttribute("publicTemplates", publicTemplates);
        model.addAttribute("userTemplates", userTemplates);
        model.addAttribute("user", user);
        
        return "templates";
    }
    
    @GetMapping("/api/templates")
    @ResponseBody
    public List<Template> getAllTemplates() {
        return templateRepository.findByIsPublicTrueOrderByUsageCountDesc();
    }
    
    @GetMapping("/api/templates/category/{category}")
    @ResponseBody
    public List<Template> getTemplatesByCategory(@PathVariable String category) {
        return templateRepository.findByCategoryAndIsPublicTrueOrderByUsageCountDesc(category);
    }
    
    @PostMapping("/api/templates")
    @ResponseBody
    public ResponseEntity<Template> createTemplate(@RequestBody Template template) {
        User user = userRepository.findById(1L).orElse(null);
        template.setCreator(user);
        template.setCreatedAt(LocalDateTime.now());
        template.setUsageCount(0);
        
        Template savedTemplate = templateRepository.save(template);
        return ResponseEntity.ok(savedTemplate);
    }
    
    @PostMapping("/api/templates/{id}/use")
    @ResponseBody
    public ResponseEntity<Template> useTemplate(@PathVariable Long id) {
        return templateRepository.findById(id)
            .map(template -> {
                template.setUsageCount(template.getUsageCount() + 1);
                return ResponseEntity.ok(templateRepository.save(template));
            })
            .orElse(ResponseEntity.notFound().build());
    }
    
    @DeleteMapping("/api/templates/{id}")
    @ResponseBody
    public ResponseEntity<?> deleteTemplate(@PathVariable Long id) {
        return templateRepository.findById(id)
            .map(template -> {
                templateRepository.delete(template);
                return ResponseEntity.ok().build();
            })
            .orElse(ResponseEntity.notFound().build());
    }
}