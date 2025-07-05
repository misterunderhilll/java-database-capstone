package com.project.back_end.controllers;

import com.project.back_end.models.Prescription;
import com.project.back_end.services.AppointmentService;
import com.project.back_end.services.PrescriptionService;
import com.project.back_end.services.Service;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${api.path}prescription")
public class PrescriptionController {

    private final PrescriptionService prescriptionService;
    private final Service service;
    private final AppointmentService appointmentService;

    public PrescriptionController(PrescriptionService prescriptionService, Service service, AppointmentService appointmentService) {
        this.prescriptionService = prescriptionService;
        this.service = service;
        this.appointmentService = appointmentService;
    }

    // 3. Save a new prescription for an appointment
    @PostMapping("/save/{token}")
    public ResponseEntity<?> savePrescription(@Valid @RequestBody Prescription prescription, @PathVariable String token) {
        if (!service.validateToken(token, "doctor")) {
            return ResponseEntity.status(401).body("Unauthorized: Invalid or expired token");
        }

        // Update appointment status before saving prescription
        boolean updated = appointmentService.updateAppointmentStatus(prescription.getAppointment().getId(), "PRESCRIBED");
        if (!updated) {
            return ResponseEntity.status(400).body("Failed to update appointment status");
        }

        return prescriptionService.savePrescription(prescription);
    }

    // 4. Retrieve a prescription by appointment ID
    @GetMapping("/{appointmentId}/{token}")
    public ResponseEntity<?> getPrescription(@PathVariable Long appointmentId, @PathVariable String token) {
        if (!service.validateToken(token, "doctor")) {
            return ResponseEntity.status(401).body("Unauthorized: Invalid or expired token");
        }

        return prescriptionService.getPrescription(appointmentId);
    }
}
