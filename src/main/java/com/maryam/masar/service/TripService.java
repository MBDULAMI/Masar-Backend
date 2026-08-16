package com.maryam.masar.service;

import com.maryam.masar.dto.TripPublishRequest;
import com.maryam.masar.dto.TripResponse;
import com.maryam.masar.dto.TripSearchResponse;
import com.maryam.masar.entity.*;
import com.maryam.masar.exception.ConflictException;
import com.maryam.masar.exception.NotFoundException;
import com.maryam.masar.repository.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

@Service
public class TripService {

    private final TripRepository tripRepository;
    private final OperatorRepository operatorRepository;
    private final BookingRepository bookingRepository;
    private final PassengerRepository passengerRepository;
    private final WalletTransactionRepository walletTransactionRepository;

    public TripService(TripRepository tripRepository,
                       OperatorRepository operatorRepository,
                       BookingRepository bookingRepository,
                       PassengerRepository passengerRepository,
                       WalletTransactionRepository walletTransactionRepository) {
        this.tripRepository = tripRepository;
        this.operatorRepository = operatorRepository;
        this.bookingRepository = bookingRepository;
        this.passengerRepository = passengerRepository;
        this.walletTransactionRepository = walletTransactionRepository;
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
        int safeSize = Math.min(size, 50);
        Pageable pageable = PageRequest.of(page, safeSize);

        Page<Trip> trips = tripRepository.searchTrips(origin, destination, afterDate, pageable);

        return trips.map(this::toSearchResponse);
    }

    public TripSearchResponse getTripById(Long tripId) {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new NotFoundException("Trip not found"));
        return toSearchResponse(trip);
    }

    // R7: operator cancels trip -> cancel + 100% refund every CONFIRMED booking on it
    @Transactional
    public TripResponse cancelTrip(Long tripId, Passenger currentUser) {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new NotFoundException("Trip not found"));

        Operator operator = operatorRepository.findByOwner_Id(currentUser.getId())
                .orElseThrow(() -> new NotFoundException("Trip not found"));

        // R8: ownership check — same error whether missing or not yours
        if (!trip.getOperator().getId().equals(operator.getId())) {
            throw new NotFoundException("Trip not found");
        }

        if (trip.getStatus() == TripStatus.CANCELLED) {
            throw new ConflictException("Trip is already cancelled");
        }

        List<Booking> confirmedBookings = bookingRepository.findByTrip_IdAndStatus(tripId, BookingStatus.CONFIRMED);
        OffsetDateTime now = OffsetDateTime.now();

        for (Booking booking : confirmedBookings) {
            booking.setStatus(BookingStatus.CANCELLED);
            booking.setUpdatedAt(now);
            bookingRepository.save(booking);

            // R7 overrides R5 — always 100% refund regardless of time to departure
            BigDecimal refundAmount = booking.getTotalAmount();
            Passenger passenger = booking.getPassenger();
            BigDecimal newBalance = passenger.getWalletBalance().add(refundAmount);
            passenger.setWalletBalance(newBalance);
            passengerRepository.save(passenger);

            WalletTransaction refundTransaction = new WalletTransaction();
            refundTransaction.setType(TransactionType.REFUND);
            refundTransaction.setAmount(refundAmount);
            refundTransaction.setBalanceAfter(newBalance);
            refundTransaction.setPassenger(passenger);
            refundTransaction.setBooking(booking);
            refundTransaction.setCreatedAt(now);
            walletTransactionRepository.save(refundTransaction);
        }

        trip.setStatus(TripStatus.CANCELLED);
        Trip savedTrip = tripRepository.save(trip);

        return new TripResponse(
                savedTrip.getId(), savedTrip.getOriginCity(), savedTrip.getDestinationCity(),
                savedTrip.getDepartureTime(), savedTrip.getArrivalTime(), savedTrip.getBusCode(),
                savedTrip.getTotalSeats(), savedTrip.getSeatPrice(), savedTrip.getStatus().name(),
                savedTrip.getOperator().getName()
        );
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