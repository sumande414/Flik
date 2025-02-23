package io.flik.app.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/healthCheck/")
public class HealthCheckController {
    @GetMapping("/check")
    public ResponseEntity<String> healthCheckHandler(){
        return ResponseEntity.ok("Service is live!!");
    }
}
