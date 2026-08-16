package com.maryam.masar.controller;

import com.maryam.masar.dto.BookingRequest;
import com.maryam.masar.dto.BookingResponse;
import com.maryam.masar.entity.Passenger;
import com.maryam.masar.security.CustomUserDetails;
import com.maryam.masar.service.BookingService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/bookings")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    @PreAuthorize("hasRole('PASSENGER')")
    public ResponseEntity<BookingResponse> createBooking(@Valid @RequestBody BookingRequest request,
                                                         @AuthenticationPrincipal CustomUserDetails userDetails) {
        Passenger currentUser = userDetails.getPassenger();
        BookingResponse response = bookingService.createBooking(request, currentUser);
        return ResponseEntity.status(201).body(response);
    }
}