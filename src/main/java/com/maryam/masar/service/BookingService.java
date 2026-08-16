package com.maryam.masar.service;

import com.maryam.masar.dto.BookingRequest;
import com.maryam.masar.dto.BookingResponse;
import com.maryam.masar.dto.TicketResponse;
import com.maryam.masar.entity.*;
import com.maryam.masar.exception.ConflictException;
import com.maryam.masar.exception.NotFoundException;
import com.maryam.masar.exception.UnprocessableEntityException;
import com.maryam.masar.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final TripRepository tripRepository;
    private final PassengerRepository passengerRepository;
    private final TicketRepository ticketRepository;
    private final WalletTransactionRepository walletTransactionRepository;

    private static final String REF_CHARS = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    private static final SecureRandom RANDOM = new SecureRandom();

    public BookingService(BookingRepository bookingRepository,
                          TripRepository tripRepository,
                          PassengerRepository passengerRepository,
                          TicketRepository ticketRepository,
                          WalletTransactionRepository walletTransactionRepository) {
        this.bookingRepository = bookingRepository;
        this.tripRepository = tripRepository;
        this.passengerRepository = passengerRepository;
        this.ticketRepository = ticketRepository;
        this.walletTransactionRepository = walletTransactionRepository;
    }

    @Transactional
    public BookingResponse createBooking(BookingRequest request, Passenger currentUser) {
        Trip trip = tripRepository.findById(request.getTripId())
                .orElseThrow(() -> new NotFoundException("Trip not found"));

        // R3: booking closes 30 minutes before departure; no booking on DEPARTED/CANCELLED trips
        if (trip.getStatus() != TripStatus.SCHEDULED) {
            throw new ConflictException("Trip is not open for booking");
        }
        OffsetDateTime cutoff = trip.getDepartureTime().minusMinutes(30);
        if (OffsetDateTime.now().isAfter(cutoff)) {
            throw new ConflictException("Booking is closed for this trip");
        }

        // R2: one confirmed booking per passenger per trip
        boolean alreadyBooked = bookingRepository.existsByPassenger_IdAndTrip_IdAndStatus(
                currentUser.getId(), trip.getId(), BookingStatus.CONFIRMED);
        if (alreadyBooked) {
            throw new ConflictException("You already have a confirmed booking on this trip");
        }

        // R1: seat availability
        int confirmedSeats = bookingRepository.sumConfirmedSeatsByTripId(trip.getId());
        int availableSeats = trip.getTotalSeats() - confirmedSeats;
        if (request.getSeatCount() > availableSeats) {
            throw new ConflictException("Not enough seats available");
        }

        // R4: wallet debit — reject and persist nothing if balance too low
        BigDecimal totalAmount = trip.getSeatPrice().multiply(BigDecimal.valueOf(request.getSeatCount()));
        Passenger passenger = passengerRepository.findById(currentUser.getId())
                .orElseThrow(() -> new NotFoundException("Passenger not found"));

        if (passenger.getWalletBalance().compareTo(totalAmount) < 0) {
            throw new UnprocessableEntityException("Insufficient wallet balance");
        }

        BigDecimal newBalance = passenger.getWalletBalance().subtract(totalAmount);
        passenger.setWalletBalance(newBalance);
        passengerRepository.save(passenger);

        // Create the booking
        Booking booking = new Booking();
        booking.setReference(generateReference());
        booking.setPassenger(passenger);
        booking.setTrip(trip);
        booking.setSeatCount(request.getSeatCount());
        booking.setTotalAmount(totalAmount);
        booking.setStatus(BookingStatus.CONFIRMED);
        booking.setCreatedAt(OffsetDateTime.now());
        booking.setUpdatedAt(OffsetDateTime.now());
        Booking savedBooking = bookingRepository.save(booking);

        // Create one ticket per seat
        List<Ticket> tickets = new ArrayList<>();
        int startSeat = confirmedSeats + 1;
        for (int i = 0; i < request.getSeatCount(); i++) {
            Ticket ticket = new Ticket();
            ticket.setBooking(savedBooking);
            ticket.setSeatNumber(startSeat + i);
            ticket.setSerial(generateSerial());
            tickets.add(ticketRepository.save(ticket));
        }

        // Wallet ledger entry (append-only)
        WalletTransaction transaction = new WalletTransaction();
        transaction.setType(TransactionType.DEBIT);
        transaction.setAmount(totalAmount);
        transaction.setBalanceAfter(newBalance);
        transaction.setPassenger(passenger);
        transaction.setBooking(savedBooking);
        transaction.setCreatedAt(OffsetDateTime.now());
        walletTransactionRepository.save(transaction);

        return toBookingResponse(savedBooking, tickets);
    }

    private String generateReference() {
        StringBuilder sb = new StringBuilder("MSR-");
        for (int i = 0; i < 6; i++) {
            sb.append(REF_CHARS.charAt(RANDOM.nextInt(REF_CHARS.length())));
        }
        return sb.toString();
    }

    private String generateSerial() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            sb.append(REF_CHARS.charAt(RANDOM.nextInt(REF_CHARS.length())));
        }
        return sb.toString();
    }

    private BookingResponse toBookingResponse(Booking booking, List<Ticket> tickets) {
        List<TicketResponse> ticketResponses = tickets.stream()
                .map(t -> new TicketResponse(t.getId(), t.getSeatNumber(), t.getSerial()))
                .toList();

        return new BookingResponse(
                booking.getId(),
                booking.getReference(),
                booking.getTrip().getId(),
                booking.getTrip().getOriginCity(),
                booking.getTrip().getDestinationCity(),
                booking.getTrip().getDepartureTime(),
                booking.getSeatCount(),
                booking.getTotalAmount(),
                booking.getStatus().name(),
                booking.getCreatedAt(),
                ticketResponses
        );
    }
}