package com.maryam.masar.config;

import com.maryam.masar.entity.*;
import com.maryam.masar.repository.OperatorRepository;
import com.maryam.masar.repository.PassengerRepository;
import com.maryam.masar.repository.TripRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Component
public class DataSeeder implements CommandLineRunner {

    private final PassengerRepository passengerRepository;
    private final OperatorRepository operatorRepository;
    private final TripRepository tripRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(PassengerRepository passengerRepository,
                      OperatorRepository operatorRepository,
                      TripRepository tripRepository,
                      PasswordEncoder passwordEncoder) {
        this.passengerRepository = passengerRepository;
        this.operatorRepository = operatorRepository;
        this.tripRepository = tripRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (passengerRepository.findByEmail("admin@masar.com").isPresent()) {
            System.out.println("Seed data already present, skipping.");
            return;
        }

        // --- Admin ---
        Passenger admin = new Passenger();
        admin.setNationalId("0000000000");
        admin.setFullName("MaryamD");
        admin.setMobile("0500000000");
        admin.setEmail("admin@masar.com");
        admin.setPasswordHash(passwordEncoder.encode("admin123"));
        admin.setRole(Role.ADMIN);
        admin.setWalletBalance(BigDecimal.ZERO);
        passengerRepository.save(admin);

        // --- 3 Operators (each needs an owning Passenger account) ---
        Operator op1 = createOperator("owner1@masar.com", "Sendalah", "0511111111",
                "1111111111", "Sendalah Railways", "SNDLH01");
        Operator op2 = createOperator("owner2@masar.com", "Tuwaiq", "0522222222",
                "2222222222", "Tuwaiq Transit Company", "TUWQ01");
        Operator op3 = createOperator("owner3@masar.com", "NEOM", "0533333333",
                "3333333333", "NEOM Rail Transport", "NEOM01");

        // --- 3 Passengers with different wallet balances ---
        createPassenger("passenger1@masar.com", "Latifah Zyad", "0544444444",
                "4444444444", new BigDecimal("1000.00")); // plenty
        createPassenger("passenger2@masar.com", "Omar Sh", "0555555555",
                "5555555555", new BigDecimal("300.00")); // moderate
        createPassenger("passenger3@masar.com", "Nehad Mohammed", "0566666666",
                "6666666666", new BigDecimal("100.00")); // too low for a 4-seat booking at 150/seat

        // --- 4 cities used across trips ---
        String[] cities = {"Riyadh", "Hassa", "Jeddah", "Makkah"};

        // --- 10 trips across the next 7 days, spread across the 3 operators ---
        Operator[] operators = {op1, op2, op3};
        OffsetDateTime now = OffsetDateTime.now();

        int[][] routePairs = {
                {0, 1}, {1, 0}, {0, 2}, {2, 0}, {0, 3},
                {3, 0}, {1, 2}, {2, 1}, {1, 3}, {3, 1}
        };

        for (int i = 0; i < 10; i++) {
            Trip trip = new Trip();
            trip.setOperator(operators[i % operators.length]);
            trip.setOriginCity(cities[routePairs[i][0]]);
            trip.setDestinationCity(cities[routePairs[i][1]]);
            trip.setDepartureTime(now.plusDays((i % 7) + 1).withHour(9).withMinute(0).withSecond(0).withNano(0));
            trip.setArrivalTime(now.plusDays((i % 7) + 1).withHour(19).withMinute(0).withSecond(0).withNano(0));
            trip.setBusCode("BUS-" + (100 + i));
            trip.setTotalSeats(40);
            trip.setSeatPrice(new BigDecimal("150.00"));
            trip.setStatus(TripStatus.SCHEDULED);
            tripRepository.save(trip);
        }

        System.out.println("Seed data created: 1 admin, 3 operators, 3 passengers, 10 trips.");
        System.out.println("Admin login: admin@masar.com / admin123");
        System.out.println("Operator logins: owner1@masar.com (Sendalah Railways), owner2@masar.com (Tuwaiq Transit Company), owner3@masar.com (NEOM Rail Transport) / operator123");
        System.out.println("Passenger logins: passenger1@masar.com (Latifah Zyad, 1000 SAR), passenger2@masar.com (Omar Sh, 300 SAR), passenger3@masar.com (Nehad Mohammed, 100 SAR) / passenger123");
    }

    private Operator createOperator(String email, String ownerFullName, String mobile,
                                    String nationalId, String companyName, String companyCode) {
        Passenger owner = new Passenger();
        owner.setNationalId(nationalId);
        owner.setFullName(ownerFullName);
        owner.setMobile(mobile);
        owner.setEmail(email);
        owner.setPasswordHash(passwordEncoder.encode("operator123"));
        owner.setRole(Role.OPERATOR);
        owner.setWalletBalance(BigDecimal.ZERO);
        Passenger savedOwner = passengerRepository.save(owner);

        Operator operator = new Operator();
        operator.setName(companyName);
        operator.setCode(companyCode);
        operator.setStatus(OperatorStatus.ACTIVE);
        operator.setOwner(savedOwner);
        return operatorRepository.save(operator);
    }

    private Passenger createPassenger(String email, String fullName, String mobile,
                                      String nationalId, BigDecimal balance) {
        Passenger passenger = new Passenger();
        passenger.setNationalId(nationalId);
        passenger.setFullName(fullName);
        passenger.setMobile(mobile);
        passenger.setEmail(email);
        passenger.setPasswordHash(passwordEncoder.encode("passenger123"));
        passenger.setRole(Role.PASSENGER);
        passenger.setWalletBalance(balance);
        return passengerRepository.save(passenger);
    }
}