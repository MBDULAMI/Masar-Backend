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

import com.maryam.masar.entity.BookingStatus;
import org.springframework.data.domain.Page;
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

    @PostMapping("/{id}/cancel")
    @PreAuthorize("hasRole('PASSENGER')")
    public ResponseEntity<BookingResponse> cancelBooking(@PathVariable Long id,
                                                         @AuthenticationPrincipal CustomUserDetails userDetails) {
        Passenger currentUser = userDetails.getPassenger();
        BookingResponse response = bookingService.cancelBooking(id, currentUser);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @PreAuthorize("hasRole('PASSENGER')")
    public ResponseEntity<Page<BookingResponse>> getMyBookings(
            @RequestParam(required = false) BookingStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Passenger currentUser = userDetails.getPassenger();
        Page<BookingResponse> bookings = bookingService.getMyBookings(currentUser, status, page, size);
        return ResponseEntity.ok(bookings);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('PASSENGER')")
    public ResponseEntity<BookingResponse> getMyBookingById(@PathVariable Long id,
                                                            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Passenger currentUser = userDetails.getPassenger();
        BookingResponse response = bookingService.getMyBookingById(id, currentUser);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/operator")
    @PreAuthorize("hasRole('OPERATOR')")
    public ResponseEntity<Page<BookingResponse>> getOperatorBookings(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Passenger currentUser = userDetails.getPassenger();
        Page<BookingResponse> bookings = bookingService.getBookingsForOperator(currentUser, page, size);
        return ResponseEntity.ok(bookings);
    }
}