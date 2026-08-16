package com.maryam.masar.repository;

import com.maryam.masar.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BookingRepository extends JpaRepository <Booking, Long> {
    @Query("SELECT COALESCE(SUM(b.seatCount), 0) FROM Booking b WHERE b.trip.id = :tripId AND b.status = 'CONFIRMED'")
    Integer sumConfirmedSeatsByTripId(@Param("tripId") Long tripId);
}
