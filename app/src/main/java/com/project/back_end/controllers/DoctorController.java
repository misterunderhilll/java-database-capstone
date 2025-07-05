package com.project.back_end.controllers;

import com.project.back_end.models.Doctor;
import com.project.back_end.models.Login;
import com.project.back_end.services.DoctorService;
import com.project.back_end.services.Service;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("${api.path}doctor")
public class DoctorController {

    private final DoctorService doctorService;
    private final Service service;

    public DoctorController(DoctorService doctorService, Service service) {
        this.doctorService = doctorService;
        this.service = service;
    }

    // 3. Check doctor availability
    @GetMapping("/availability/{user}/{doctorId}/{date}/{token}")
    public ResponseEntity<?> getDoctorAvailability(
            @PathVariable String user,
            @PathVariable Long doctorId,
            @PathVariable String date,
            @PathVariable String token) {

        if (!service.validateToken(token, user)) {
            return ResponseEntity.status(401).body("Unauthorized: Invalid or expired token");
        }

        return doctorService.getDoctorAvailability(doctorId, date);
    }

    // 4. Get all doctors
    @GetMapping
    public ResponseEntity<Map<String, Object>> getDoctor() {
        return ResponseEntity.ok(Map.of("doctors", doctorService.getAllDoctors()));
    }

    // 5. Register a new doctor (admin only)
    @PostMapping("/register/{token}")
    public ResponseEntity<?> saveDoctor(
            @Valid @RequestBody Doctor doctor,
            @PathVariable String token) {

        if (!service.validateToken(token, "admin")) {
            return ResponseEntity.status(401).body("Unauthorized: Invalid or expired token");
        }

        boolean exists = doctorService.existsByEmailOrPhone(doctor.getEmail(), doctor.getPhone());
        if (exists) {
            return ResponseEntity.status(409).body("Doctor already exists");
        }

        return doctorService.saveDoctor(doctor);
    }

    // 6. Doctor login
    @PostMapping("/login")
    public ResponseEntity<?> doctorLogin(@Valid @RequestBody Login login) {
        return doctorService.validateDoctorLogin(login);
    }

    // 7. Update doctor info (admin only)
    @PutMapping("/update/{token}")
    public ResponseEntity<?> updateDoctor(
            @Valid @RequestBody Doctor doctor,
            @PathVariable String token) {

        if (!service.validateToken(token, "admin")) {
            return ResponseEntity.status(401).body("Unauthorized: Invalid or expired token");
        }

        return doctorService.updateDoctor(doctor);
    }

    // 8. Delete doctor (admin only)
    @DeleteMapping("/delete/{doctorId}/{token}")
    public ResponseEntity<?> deleteDoctor(
            @PathVariable Long doctorId,
            @PathVariable String token) {

        if (!service.validateToken(token, "admin")) {
            return ResponseEntity.status(401).body("Unauthorized: Invalid or expired token");
        }

        return doctorService.deleteDoctor(doctorId);
    }

    // 9. Filter doctors by name, time, and specialty
    @GetMapping("/filter/{name}/{time}/{speciality}")
    public ResponseEntity<?> filter(
            @PathVariable String name,
            @PathVariable String time,
            @PathVariable String speciality) {

        return ResponseEntity.ok(service.filterDoctor(name, time, speciality));
    }
}
