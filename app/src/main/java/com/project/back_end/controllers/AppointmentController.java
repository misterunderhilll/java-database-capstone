package com.project.back_end.controllers;

import com.project.back_end.models.Appointment;
import com.project.back_end.services.AppointmentService;
import com.project.back_end.services.Service;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;
    private final Service service;

    // 2. Constructor injection of dependencies
    public AppointmentController(AppointmentService appointmentService, Service service) {
        this.appointmentService = appointmentService;
        this.service = service;
    }

    // 3. Get appointments by date and patient name, validate token for "doctor"
    @GetMapping("/{date}/{patientName}/{token}")
    public ResponseEntity<?> getAppointments(
            @PathVariable String date,
            @PathVariable String patientName,
            @PathVariable String token) {

        if (!service.validateToken(token, "doctor")) {
            return ResponseEntity.status(401).body("Unauthorized: Invalid or expired token");
        }
        return appointmentService.getAppointmentsByDateAndPatientName(date, patientName);
    }

    // 4. Book appointment, validate token for "patient"
    @PostMapping("/book/{token}")
    public ResponseEntity<?> bookAppointment(
            @Valid @RequestBody Appointment appointment,
            @PathVariable String token) {

        if (!service.validateToken(token, "patient")) {
            return ResponseEntity.status(401).body("Unauthorized: Invalid or expired token");
        }

        // Validate appointment availability and doctor existence
        int validationResult = service.validateAppointment(appointment.getDoctorId(), appointment.getDate(), appointment.getStartTime());
        if (validationResult == -1) {
            return ResponseEntity.badRequest().body("Invalid doctor ID");
        } else if (validationResult == 0) {
            return ResponseEntity.badRequest().body("Requested time slot is not available");
        }

        return appointmentService.bookAppointment(appointment);
    }

    // 5. Update appointment, validate token for "patient"
    @PutMapping("/update/{token}")
    public ResponseEntity<?> updateAppointment(
            @Valid @RequestBody Appointment appointment,
            @PathVariable String token) {

        if (!service.validateToken(token, "patient")) {
            return ResponseEntity.status(401).body("Unauthorized: Invalid or expired token");
        }

        return appointmentService.updateAppointment(appointment);
    }

    // 6. Cancel appointment, validate token for "patient"
    @DeleteMapping("/cancel/{appointmentId}/{token}")
    public ResponseEntity<?> cancelAppointment(
            @PathVariable Long appointmentId,
            @PathVariable String token) {

        if (!service.validateToken(token, "patient")) {
            return ResponseEntity.status(401).body("Unauthorized: Invalid or expired token");
        }

        return appointmentService.cancelAppointment(appointmentId);
    }
}
