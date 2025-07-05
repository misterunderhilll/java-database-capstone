package com.project.back_end.services;

import com.project.back_end.models.Admin;
import com.project.back_end.models.Doctor;
import com.project.back_end.models.Patient;
import com.project.back_end.repo.AdminRepository;
import com.project.back_end.repo.DoctorRepository;
import com.project.back_end.repo.PatientRepository;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Optional;

@Component
public class TokenService {

    private final AdminRepository adminRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;

    // Injected from application.properties or environment variable
    @Value("${jwt.secret}")
    private String jwtSecret;

    private SecretKey signingKey;

    // 2. Constructor injection
    public TokenService(AdminRepository adminRepository,
                        DoctorRepository doctorRepository,
                        PatientRepository patientRepository) {
        this.adminRepository = adminRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
    }

    // Initialize signing key once the jwtSecret is injected
    @PostConstruct
    private void init() {
        this.signingKey = getSigningKey();
    }

    // 3. getSigningKey Method
    private SecretKey getSigningKey() {
        // Convert secret string to a SecretKey for signing JWTs
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    // 4. generateToken Method
    public String generateToken(String email) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + 7 * 24 * 60 * 60 * 1000L); // 7 days

        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(signingKey, SignatureAlgorithm.HS256)
                .compact();
    }

    // 5. extractEmail Method
    public String extractEmail(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(signingKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            return claims.getSubject();
        } catch (Exception e) {
            // Log exception if needed
            return null;
        }
    }

    // 6. validateToken Method
    public boolean validateToken(String token, String role) {
        try {
            String email = extractEmail(token);
            if (email == null) {
                return false;
            }

            switch (role.toLowerCase()) {
                case "admin":
                    Optional<Admin> admin = adminRepository.findByUsername(email);
                    return admin.isPresent();
                case "doctor":
                    Optional<Doctor> doctor = doctorRepository.findByEmail(email);
                    return doctor.isPresent();
                case "patient":
                    Optional<Patient> patient = patientRepository.findByEmail(email);
                    return patient.isPresent();
                default:
                    return false;
            }
        } catch (Exception e) {
            // Log exception if needed
            return false;
        }
    }
}
