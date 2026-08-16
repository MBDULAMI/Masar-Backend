package com.maryam.masar.service;

import com.maryam.masar.dto.AuthResponse;
import com.maryam.masar.dto.LoginRequest;
import com.maryam.masar.dto.RegisterRequest;
import com.maryam.masar.entity.Operator;
import com.maryam.masar.entity.OperatorStatus;
import com.maryam.masar.entity.Passenger;
import com.maryam.masar.entity.Role;
import com.maryam.masar.repository.OperatorRepository;
import com.maryam.masar.repository.PassengerRepository;
import com.maryam.masar.security.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class AuthService {

    private final PassengerRepository passengerRepository;
    private final OperatorRepository operatorRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(PassengerRepository passengerRepository,
                       OperatorRepository operatorRepository,
                       PasswordEncoder passwordEncoder,
                       JwtUtil jwtUtil) {
        this.passengerRepository = passengerRepository;
        this.operatorRepository = operatorRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @Transactional
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

        if (role == Role.OPERATOR) {
            if (request.getCompanyName() == null || request.getCompanyName().isBlank()
                    || request.getCompanyCode() == null || request.getCompanyCode().isBlank()) {
                throw new IllegalArgumentException("companyName and companyCode are required for OPERATOR registration");
            }
            if (operatorRepository.findByCode(request.getCompanyCode()).isPresent()) {
                throw new IllegalArgumentException("Company code already in use");
            }
        }

        Passenger passenger = new Passenger();
        passenger.setNationalId(request.getNationalId());
        passenger.setFullName(request.getFullName());
        passenger.setMobile(request.getMobile());
        passenger.setEmail(request.getEmail());
        passenger.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        passenger.setRole(role);
        passenger.setWalletBalance(BigDecimal.ZERO);

        Passenger savedPassenger = passengerRepository.save(passenger);

        if (role == Role.OPERATOR) {
            Operator operator = new Operator();
            operator.setName(request.getCompanyName());
            operator.setCode(request.getCompanyCode());
            operator.setStatus(OperatorStatus.ACTIVE);
            operator.setOwner(savedPassenger);
            operatorRepository.save(operator);
        }

        String token = jwtUtil.generateToken(savedPassenger.getId(), savedPassenger.getEmail(), savedPassenger.getRole().name());
        return new AuthResponse(token, savedPassenger.getId(), savedPassenger.getRole().name());
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