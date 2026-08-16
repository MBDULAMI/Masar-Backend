package com.maryam.masar.config;

import com.maryam.masar.entity.*;
import com.maryam.masar.repository.PassengerRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class DataSeeder implements CommandLineRunner {

    private final PassengerRepository passengerRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(PassengerRepository passengerRepository, PasswordEncoder passwordEncoder) {
        this.passengerRepository = passengerRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (passengerRepository.findByEmail("admin@masar.com").isEmpty()) {
            Passenger admin = new Passenger();
            admin.setNationalId("0000000000");
            admin.setFullName("System Admin");
            admin.setMobile("0500000000");
            admin.setEmail("admin@masar.com");
            admin.setPasswordHash(passwordEncoder.encode("admin123"));
            admin.setRole(Role.ADMIN);
            admin.setWalletBalance(BigDecimal.ZERO);
            passengerRepository.save(admin);
            System.out.println("Seeded admin account: admin@masar.com / admin123");
        }
    }
}