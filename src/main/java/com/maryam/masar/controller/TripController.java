package com.maryam.masar.controller;

import com.maryam.masar.dto.TripPublishRequest;
import com.maryam.masar.dto.TripResponse;
import com.maryam.masar.dto.TripSearchResponse;
import com.maryam.masar.entity.Passenger;
import com.maryam.masar.security.CustomUserDetails;
import com.maryam.masar.service.TripService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;

@RestController
@RequestMapping("/api/v1/trips")
public class TripController {

    private final TripService tripService;

    public TripController(TripService tripService) {
        this.tripService = tripService;
    }

    @PostMapping
    @PreAuthorize("hasRole('OPERATOR')")
    public ResponseEntity<TripResponse> publishTrip(@Valid @RequestBody TripPublishRequest request,
                                                    @AuthenticationPrincipal CustomUserDetails userDetails) {
        Passenger currentUser = userDetails.getPassenger();
        TripResponse response = tripService.publishTrip(request, currentUser);
        return ResponseEntity.status(201).body(response);
    }

    @GetMapping
    public ResponseEntity<Page<TripSearchResponse>> searchTrips(
            @RequestParam(required = false) String origin,
            @RequestParam(required = false) String destination,
            @RequestParam(required = false) OffsetDateTime afterDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<TripSearchResponse> results = tripService.searchTrips(origin, destination, afterDate, page, size);
        return ResponseEntity.ok(results);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TripSearchResponse> getTrip(@PathVariable Long id) {
        return ResponseEntity.ok(tripService.getTripById(id));
    }
}