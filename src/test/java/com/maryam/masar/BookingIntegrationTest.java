package com.maryam.masar;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.maryam.masar.dto.RegisterRequest;
import com.maryam.masar.dto.TripPublishRequest;
import com.maryam.masar.entity.Passenger;
import com.maryam.masar.entity.Role;
import com.maryam.masar.repository.PassengerRepository;
import com.maryam.masar.security.JwtUtil;
import com.maryam.masar.service.AuthService;
import com.maryam.masar.service.TripService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class BookingIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private AuthService authService;

    @Autowired
    private TripService tripService;

    @Autowired
    private PassengerRepository passengerRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    private String passengerAToken;
    private String passengerBToken;

    @BeforeEach
    void setUp() {
        // Register two passengers directly via the repository to avoid
        // colliding with any data the DataSeeder already created
        Passenger passengerA = new Passenger();
        passengerA.setNationalId("1111111199");
        passengerA.setFullName("Test Passenger A");
        passengerA.setMobile("0511111199");
        passengerA.setEmail("passengerA.test@masar.com");
        passengerA.setPasswordHash(passwordEncoder.encode("password123"));
        passengerA.setRole(Role.PASSENGER);
        passengerA.setWalletBalance(new BigDecimal("1000.00"));
        passengerA = passengerRepository.save(passengerA);

        Passenger passengerB = new Passenger();
        passengerB.setNationalId("2222222299");
        passengerB.setFullName("Test Passenger B");
        passengerB.setMobile("0522222299");
        passengerB.setEmail("passengerB.test@masar.com");
        passengerB.setPasswordHash(passwordEncoder.encode("password123"));
        passengerB.setRole(Role.PASSENGER);
        passengerB.setWalletBalance(new BigDecimal("1000.00"));
        passengerB = passengerRepository.save(passengerB);

        passengerAToken = jwtUtil.generateToken(passengerA.getId(), passengerA.getEmail(), "PASSENGER");
        passengerBToken = jwtUtil.generateToken(passengerB.getId(), passengerB.getEmail(), "PASSENGER");
    }

    @Test
    void unauthenticatedRequest_toProtectedEndpoint_isRejected() throws Exception {
        mockMvc.perform(get("/api/v1/bookings"))
                .andExpect(status().isForbidden());
    }

    @Test
    void passengerCannotAccessAnotherPassengersBooking() throws Exception {
        // This test assumes at least one trip exists (seeded by DataSeeder on app startup).
        // We search for any scheduled trip and use its id to create a real booking as Passenger A.
        String tripsResponse = mockMvc.perform(get("/api/v1/trips").param("size", "1"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        Long tripId = objectMapper.readTree(tripsResponse).get("content").get(0).get("id").asLong();

        String bookingRequestJson = """
                {"tripId": %d, "seatCount": 1}
                """.formatted(tripId);

        String bookingResponse = mockMvc.perform(post("/api/v1/bookings")
                        .header("Authorization", "Bearer " + passengerAToken)
                        .contentType("application/json")
                        .content(bookingRequestJson))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Long bookingId = objectMapper.readTree(bookingResponse).get("id").asLong();

        // R8: Passenger B tries to access Passenger A's booking — must get 404, not 403,
        // so the existence of the booking is never revealed to a non-owner.
        mockMvc.perform(get("/api/v1/bookings/" + bookingId)
                        .header("Authorization", "Bearer " + passengerBToken))
                .andExpect(status().isNotFound());

        // Passenger A can access their own booking without issue
        mockMvc.perform(get("/api/v1/bookings/" + bookingId)
                        .header("Authorization", "Bearer " + passengerAToken))
                .andExpect(status().isOk());
    }
}