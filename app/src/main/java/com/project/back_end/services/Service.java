package com.project.back_end.services;

import com.project.back_end.models.Admin;
import com.project.back_end.models.Doctor;
import com.project.back_end.models.Patient;
import com.project.back_end.repo.AdminRepository;
import com.project.back_end.repo.DoctorRepository;
import com.project.back_end.repo.PatientRepository;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class Service {

    private final TokenService tokenService;
    private final AdminRepository adminRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final PatientService patientService;
    private final DoctorService doctorService;

    // 2. Constructor Injection
    public Service(TokenService tokenService,
                   AdminRepository adminRepository,
                   DoctorRepository doctorRepository,
                   PatientRepository patientRepository,
                   PatientService patientService,
                   DoctorService doctorService) {
        this.tokenService = tokenService;
        this.adminRepository = adminRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
        this.patientService = patientService;
        this.doctorService = doctorService;
    }

    // 3. validateToken Method
    public ResponseEntity<String> validateToken(String token, String userEmail) {
        try {
            if (!tokenService.validateToken(token, userEmail)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Invalid or expired token");
            }
            return ResponseEntity.ok("Token is valid");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error validating token");
        }
    }

    // 4. validateAdmin Method
    public ResponseEntity<?> validateAdmin(String username, String password) {
        try {
            Optional<Admin> adminOpt = adminRepository.findByUsername(username);
            if (adminOpt.isPresent()) {
                Admin admin = adminOpt.get();
                if (admin.getPassword().equals(password)) {
                    String token = tokenService.generateToken(admin.getUsername());
                    return ResponseEntity.ok(token);
                } else {
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                            .body("Incorrect password");
                }
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Admin not found");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Internal server error");
        }
    }

    // 5. filterDoctor Method
    public List<Doctor> filterDoctor(String name, String specialty, String time) {
        // If no filters provided, return all doctors
        if ((name == null || name.isEmpty()) &&
            (specialty == null || specialty.isEmpty()) &&
            (time == null || time.isEmpty())) {
            return doctorRepository.findAll();
        }

        // Use DoctorService filtering logic
        if (name != null && !name.isEmpty() && specialty != null && !specialty.isEmpty() && time != null && !time.isEmpty()) {
            return doctorService.filterDoctorsByNameSpecilityandTime(name, specialty, time);
        }

        if (name != null && !name.isEmpty() && specialty != null && !specialty.isEmpty()) {
            return doctorService.filterDoctorByNameAndSpecility(name, specialty);
        }

        if (name != null && !name.isEmpty() && time != null && !time.isEmpty()) {
            return doctorService.filterDoctorByNameAndTime(name, time);
        }

        if (specialty != null && !specialty.isEmpty() && time != null && !time.isEmpty()) {
            return doctorService.filterDoctorByTimeAndSpecility(time, specialty);
        }

        if (name != null && !name.isEmpty()) {
            return doctorService.findDoctorByName(name);
        }

        if (specialty != null && !specialty.isEmpty()) {
            return doctorService.filterDoctorBySpecility(specialty);
        }

        if (time != null && !time.isEmpty()) {
            return doctorService.filterDoctorsByTime(time);
        }

        return doctorRepository.findAll();
    }

    // 6. validateAppointment Method
    public int validateAppointment(Long doctorId, LocalDate date, String appointmentTime) {
        Optional<Doctor> doctorOpt = doctorRepository.findById(doctorId);
        if (doctorOpt.isEmpty()) {
            return -1; // Doctor not found
        }

        List<String> availableSlots = doctorService.getDoctorAvailability(doctorId, date);
        for (String slot : availableSlots) {
            if (slot.equalsIgnoreCase(appointmentTime)) {
                return 1; // Valid appointment time
            }
        }
        return 0; // Invalid appointment time
    }

    // 7. validatePatient Method
    public boolean validatePatient(String email, String phone) {
        Patient existing = patientRepository.findByEmailOrPhone(email, phone);
        return existing == null;
    }

    // 8. validatePatientLogin Method
    public ResponseEntity<?> validatePatientLogin(String email, String password) {
        try {
            Patient patient = patientRepository.findByEmail(email);
            if (patient == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Patient not found");
            }
            if (patient.getPassword().equals(password)) {
                String token = tokenService.generateToken(patient.getEmail());
                return ResponseEntity.ok(token);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Incorrect password");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error");
        }
    }

    // 9. filterPatient Method
    public ResponseEntity<?> filterPatient(String token, String condition, String doctorName) {
        try {
            String email = tokenService.extractEmail(token);
            if (email == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
            }

            if ((condition == null || condition.isEmpty()) && (doctorName == null || doctorName.isEmpty())) {
                // Get all appointments for patient
                return ResponseEntity.ok(patientService.getPatientAppointment(email));
            }

            if ((condition == null || condition.isEmpty())) {
                return ResponseEntity.ok(patientService.filterByDoctor(email, doctorName));
            }

            if ((doctorName == null || doctorName.isEmpty())) {
                return ResponseEntity.ok(patientService.filterByCondition(email, condition));
            }

            return ResponseEntity.ok(patientService.filterByDoctorAndCondition(email, doctorName, condition));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error");
        }
    }
}
