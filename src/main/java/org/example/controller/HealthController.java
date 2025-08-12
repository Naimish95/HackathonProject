package org.example.controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/health")
public class HealthController {
    
    @GetMapping
    public String health() {
        return "OK";
    }
    
    @PostMapping("/test")
    public String testPost() {
        return "POST OK";
    }
}