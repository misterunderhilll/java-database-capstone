package com.project.back_end.services;

import com.project.back_end.models.Appointment;
import com.project.back_end.repo.*;
import com.project.back_end.services.TokenService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service  // 1. Marking this class as a Spring service component
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final TokenService tokenService;

    // 2. Constructor Injection
    public AppointmentService(AppointmentRepository appointmentRepository,
                              PatientRepository patientRepository,
                              DoctorRepository doctorRepository,
                              TokenService tokenService) {
        this.appointmentRepository = appointmentRepository;
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
        this.tokenService = tokenService;
    }

    // 4. Book Appointment
    @Transactional
    public int bookAppointment(Appointment appointment) {
        try {
            appointmentRepository.save(appointment);
            return 1;
        } catch (Exception e) {
            // log error if needed
            return 0;
        }
    }

    // 5. Update Appointment
    @Transactional
    public String updateAppointment(Long appointmentId, Appointment updatedAppointment, Long patientId) {
        Optional<Appointment> existingOpt = appointmentRepository.findById(appointmentId);
        if (existingOpt.isEmpty()) {
            return "Appointment not found.";
        }

        Appointment existing = existingOpt.get();

        if (!existing.getPatient().getId().equals(patientId)) {
            return "Unauthorized update attempt.";
        }

        // Example validation: check if the time is already taken for the doctor
        boolean conflict = appointmentRepository.findByDoctorIdAndAppointmentTimeBetween(
                updatedAppointment.getDoctor().getId(),
                updatedAppointment.getAppointmentTime().minusMinutes(59),
                updatedAppointment.getAppointmentTime().plusMinutes(59)
        ).stream().anyMatch(a -> !a.getId().equals(appointmentId));

        if (conflict) {
            return "Doctor already has an appointment at that time.";
        }

        // Update fields
        existing.setAppointmentTime(updatedAppointment.getAppointmentTime());
        existing.setStatus(updatedAppointment.getStatus());
        appointmentRepository.save(existing);

        return "Appointment updated successfully.";
    }

    // 6. Cancel Appointment
    @Transactional
    public String cancelAppointment(Long appointmentId, Long patientId) {
        Optional<Appointment> appointmentOpt = appointmentRepository.findById(appointmentId);
        if (appointmentOpt.isEmpty()) {
            return "Appointment not found.";
        }

        Appointment appointment = appointmentOpt.get();

        if (!appointment.getPatient().getId().equals(patientId)) {
            return "Unauthorized cancellation attempt.";
        }

        appointmentRepository.deleteById(appointmentId);
        return "Appointment canceled.";
    }

    // 7. Get Appointments by doctor and optional patient name
    @Transactional(readOnly = true)
    public List<Appointment> getAppointments(Long doctorId, LocalDate date, String patientName) {
        var start = date.atStartOfDay();
        var end = date.atTime(23, 59, 59);

        if (patientName != null && !patientName.isBlank()) {
            return appointmentRepository.findByDoctorIdAndPatient_NameContainingIgnoreCaseAndAppointmentTimeBetween(
                    doctorId, patientName, start, end);
        }

        return appointmentRepository.findByDoctorIdAndAppointmentTimeBetween(doctorId, start, end);
    }

    // 8. Change appointment status
    @Transactional
    public String changeStatus(Long appointmentId, int status) {
        appointmentRepository.updateStatus(status, appointmentId);
        return "Status updated.";
    }
}
