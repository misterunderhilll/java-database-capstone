package com.project.back_end.services;

import com.project.back_end.DTO.AppointmentDTO;
import com.project.back_end.models.Appointment;
import com.project.back_end.models.Patient;
import com.project.back_end.repo.AppointmentRepository;
import com.project.back_end.repo.PatientRepository;
import com.project.back_end.serv.TokenService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service // 1. Mark as a Spring service
public class PatientService {

    private final PatientRepository patientRepository;
    private final AppointmentRepository appointmentRepository;
    private final TokenService tokenService;

    // 2. Constructor Injection
    public PatientService(PatientRepository patientRepository,
                          AppointmentRepository appointmentRepository,
                          TokenService tokenService) {
        this.patientRepository = patientRepository;
        this.appointmentRepository = appointmentRepository;
        this.tokenService = tokenService;
    }

    // 3. Create patient
    public int createPatient(Patient patient) {
        try {
            patientRepository.save(patient);
            return 1;
        } catch (Exception e) {
            e.printStackTrace(); // Or use logger
            return 0;
        }
    }

    // 4. Get appointments for a patient
    @Transactional(readOnly = true)
    public List<AppointmentDTO> getPatientAppointment(Long patientId) {
        try {
            List<Appointment> appointments = appointmentRepository.findByPatientId(patientId);
            return appointments.stream()
                    .map(AppointmentDTO::new)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            e.printStackTrace();
            return List.of(); // Return empty list on error
        }
    }

    // 5. Filter by condition: past or future
    @Transactional(readOnly = true)
    public List<AppointmentDTO> filterByCondition(Long patientId, String condition) {
        try {
            int status = switch (condition.toLowerCase()) {
                case "future" -> 0;
                case "past" -> 1;
                default -> throw new IllegalArgumentException("Invalid condition: " + condition);
            };

            List<Appointment> appointments = appointmentRepository.findByPatient_IdAndStatusOrderByAppointmentTimeAsc(patientId, status);
            return appointments.stream().map(AppointmentDTO::new).collect(Collectors.toList());
        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
            return List.of();
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }

    // 6. Filter by doctor name
    @Transactional(readOnly = true)
    public List<AppointmentDTO> filterByDoctor(Long patientId, String doctorName) {
        try {
            List<Appointment> appointments = appointmentRepository.filterByDoctorNameAndPatientId(doctorName, patientId);
            return appointments.stream().map(AppointmentDTO::new).collect(Collectors.toList());
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }

    // 7. Filter by doctor and condition
    @Transactional(readOnly = true)
    public List<AppointmentDTO> filterByDoctorAndCondition(Long patientId, String doctorName, String condition) {
        try {
            int status = switch (condition.toLowerCase()) {
                case "future" -> 0;
                case "past" -> 1;
                default -> throw new IllegalArgumentException("Invalid condition: " + condition);
            };

            List<Appointment> appointments = appointmentRepository.filterByDoctorNameAndPatientIdAndStatus(doctorName, patientId, status);
            return appointments.stream().map(AppointmentDTO::new).collect(Collectors.toList());
        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
            return List.of();
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }

    // 8. Get patient details from token
    public Optional<Patient> getPatientDetails(String token) {
        try {
            String email = tokenService.extractEmail(token);
            return Optional.ofNullable(patientRepository.findByEmail(email));
        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }
}
