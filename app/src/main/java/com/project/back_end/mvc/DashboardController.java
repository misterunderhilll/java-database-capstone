package com.project.back_end.mvc;

import com.project.back_end.service.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
public class DashboardController {

    // 2. Autowire the Shared Service for token validation
    @Autowired
    private Service service;

    // 3. Admin Dashboard View Mapping
    @GetMapping("/adminDashboard/{token}")
    public String adminDashboard(@PathVariable String token) {
        Map<String, Object> validation = service.validateToken(token, "admin");

        // If token is valid, return the admin dashboard view
        if (validation.isEmpty()) {
            return "admin/adminDashboard"; // Thymeleaf template path
        } else {
            return "redirect:/"; // Redirect to home/login page
        }
    }

    // 4. Doctor Dashboard View Mapping
    @GetMapping("/doctorDashboard/{token}")
    public String doctorDashboard(@PathVariable String token) {
        Map<String, Object> validation = service.validateToken(token, "doctor");

        // If token is valid, return the doctor dashboard view
        if (validation.isEmpty()) {
            return "doctor/doctorDashboard";
        } else {
            return "redirect:/"; // Redirect to home/login page
        }
    }
}
