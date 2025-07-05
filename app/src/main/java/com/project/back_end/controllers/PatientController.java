package com.project.back_end.controllers;

import com.project.back_end.models.Login;
import com.project.back_end.models.Patient;
import com.project.back_end.services.PatientService;
import com.project.back_end.services.Service;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/patient")
public class PatientController {

    private final PatientService patientService;
    private final Service service;

    public PatientController(PatientService patientService, Service service) {
        this.patientService = patientService;
        this.service = service;
    }

    // 3. Get patient details by token
    @GetMapping("/{token}")
    public ResponseEntity<?> getPatient(@PathVariable String token) {
        if (!service.validateToken(token, "patient")) {
            return ResponseEntity.status(401).body("Unauthorized: Invalid or expired token");
        }
        return patientService.getPatientByToken(token);
    }

    // 4. Register a new patient
    @PostMapping("/register")
    public ResponseEntity<?> createPatient(@Valid @RequestBody Patient patient) {
        boolean isValid = service.validatePatient(patient.getEmail(), patient.getPhone());
        if (!isValid) {
            return ResponseEntity.status(409).body("Patient with given email or phone already exists");
        }
        return patientService.savePatient(patient);
    }

    // 5. Patient login
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody Login login) {
        return service.validatePatientLogin(login);
    }

    // 6. Get patient appointments by patientId, token and role
    @GetMapping("/appointments/{patientId}/{token}/{user}")
    public ResponseEntity<?> getPatientAppointment(
            @PathVariable Long patientId,
            @PathVariable String token,
            @PathVariable String user) {

        if (!service.validateToken(token, user)) {
            return ResponseEntity.status(401).body("Unauthorized: Invalid or expired token");
        }
        return patientService.getAppointments(patientId);
    }

    // 7. Filter patient's appointments by condition and doctor name
    @GetMapping("/appointments/filter/{condition}/{name}/{token}")
    public ResponseEntity<?> filterPatientAppointment(
            @PathVariable String condition,
            @PathVariable String name,
            @PathVariable String token) {

        if (!service.validateToken(token, "patient")) {
            return ResponseEntity.status(401).body("Unauthorized: Invalid or expired token");
        }

        return ResponseEntity.ok(service.filterPatient(condition, name, token));
    }
}
