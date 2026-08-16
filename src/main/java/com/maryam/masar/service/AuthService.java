package com.maryam.masar.service;

import com.maryam.masar.dto.AuthResponse;
import com.maryam.masar.dto.LoginRequest;
import com.maryam.masar.dto.RegisterRequest;
import com.maryam.masar.entity.Passenger;
import com.maryam.masar.entity.Role;
import com.maryam.masar.repository.PassengerRepository;
import com.maryam.masar.security.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class AuthService {

    private final PassengerRepository passengerRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(PassengerRepository passengerRepository,
                       PasswordEncoder passwordEncoder,
                       JwtUtil jwtUtil) {
        this.passengerRepository = passengerRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public AuthResponse register(RegisterRequest request) {
        // R10: ADMIN can never be assigned through the API
        String requestedRole = request.getRole().trim().toUpperCase();
        if (requestedRole.equals("ADMIN")) {
            throw new IllegalArgumentException("Cannot self-register as ADMIN");
        }

        Role role;
        try {
            role = Role.valueOf(requestedRole);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Role must be PASSENGER or OPERATOR");
        }

        if (passengerRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already registered");
        }

        Passenger passenger = new Passenger();
        passenger.setNationalId(request.getNationalId());
        passenger.setFullName(request.getFullName());
        passenger.setMobile(request.getMobile());
        passenger.setEmail(request.getEmail());
        passenger.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        passenger.setRole(role);
        passenger.setWalletBalance(BigDecimal.ZERO);

        Passenger saved = passengerRepository.save(passenger);

        String token = jwtUtil.generateToken(saved.getId(), saved.getEmail(), saved.getRole().name());
        return new AuthResponse(token, saved.getId(), saved.getRole().name());
    }

    public AuthResponse login(LoginRequest request) {
        Passenger passenger = passengerRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), passenger.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid email or password");
        }

        String token = jwtUtil.generateToken(passenger.getId(), passenger.getEmail(), passenger.getRole().name());
        return new AuthResponse(token, passenger.getId(), passenger.getRole().name());
    }
}