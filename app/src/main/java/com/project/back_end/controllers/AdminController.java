package com.project.back_end.controllers;

import com.project.back_end.models.Admin;
import com.project.back_end.services.Service;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("${api.path}admin")
public class AdminController {

    private final Service service;

    // 2. Constructor injection for Service dependency
    public AdminController(Service service) {
        this.service = service;
    }

    // 3. adminLogin method handling POST requests
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> adminLogin(@RequestBody Admin admin) {
        // Delegates validation to service
        return service.validateAdmin(admin);
    }
}
