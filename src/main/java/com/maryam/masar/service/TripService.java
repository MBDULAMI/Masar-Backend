package com.maryam.masar.service;

import com.maryam.masar.dto.TripPublishRequest;
import com.maryam.masar.dto.TripResponse;
import com.maryam.masar.dto.TripSearchResponse;
import com.maryam.masar.entity.Operator;
import com.maryam.masar.entity.Passenger;
import com.maryam.masar.entity.Trip;
import com.maryam.masar.entity.TripStatus;
import com.maryam.masar.repository.BookingRepository;
import com.maryam.masar.repository.OperatorRepository;
import com.maryam.masar.repository.TripRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;

@Service
public class TripService {

    private final TripRepository tripRepository;
    private final OperatorRepository operatorRepository;
    private final BookingRepository bookingRepository;

    public TripService(TripRepository tripRepository,
                       OperatorRepository operatorRepository,
                       BookingRepository bookingRepository) {
        this.tripRepository = tripRepository;
        this.operatorRepository = operatorRepository;
        this.bookingRepository = bookingRepository;
    }

    public TripResponse publishTrip(TripPublishRequest request, Passenger currentUser) {
        Operator operator = operatorRepository.findByOwner_Id(currentUser.getId())
                .orElseThrow(() -> new IllegalArgumentException("No operator company found for this account"));

        if (request.getArrivalTime().isBefore(request.getDepartureTime())
                || request.getArrivalTime().isEqual(request.getDepartureTime())) {
            throw new IllegalArgumentException("Arrival time must be after departure time");
        }

        Trip trip = new Trip();
        trip.setOperator(operator);
        trip.setOriginCity(request.getOriginCity());
        trip.setDestinationCity(request.getDestinationCity());
        trip.setDepartureTime(request.getDepartureTime());
        trip.setArrivalTime(request.getArrivalTime());
        trip.setBusCode(request.getBusCode());
        trip.setTotalSeats(request.getTotalSeats());
        trip.setSeatPrice(request.getSeatPrice());
        trip.setStatus(TripStatus.SCHEDULED);

        Trip saved = tripRepository.save(trip);

        return new TripResponse(
                saved.getId(), saved.getOriginCity(), saved.getDestinationCity(),
                saved.getDepartureTime(), saved.getArrivalTime(), saved.getBusCode(),
                saved.getTotalSeats(), saved.getSeatPrice(), saved.getStatus().name(),
                saved.getOperator().getName()
        );
    }

    public Page<TripSearchResponse> searchTrips(String origin, String destination,
                                                OffsetDateTime afterDate, int page, int size) {
        int safeSize = Math.min(size, 50); // enforced maximum page size
        Pageable pageable = PageRequest.of(page, safeSize);

        Page<Trip> trips = tripRepository.searchTrips(origin, destination, afterDate, pageable);

        return trips.map(this::toSearchResponse);
    }

    public TripSearchResponse getTripById(Long tripId) {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new IllegalArgumentException("Trip not found"));
        return toSearchResponse(trip);
    }

    private TripSearchResponse toSearchResponse(Trip trip) {
        int confirmedSeats = bookingRepository.sumConfirmedSeatsByTripId(trip.getId());
        int availableSeats = trip.getTotalSeats() - confirmedSeats;

        return new TripSearchResponse(
                trip.getId(),
                trip.getOriginCity(),
                trip.getDestinationCity(),
                trip.getDepartureTime(),
                trip.getArrivalTime(),
                trip.getOperator().getName(),
                trip.getSeatPrice(),
                availableSeats,
                trip.getStatus().name()
        );
    }
}