package com.maryam.masar.repository;

import com.maryam.masar.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.maryam.masar.entity.BookingStatus;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
public interface BookingRepository extends JpaRepository <Booking, Long> {
    @Query("SELECT COALESCE(SUM(b.seatCount), 0) FROM Booking b WHERE b.trip.id = :tripId AND b.status = 'CONFIRMED'")
    Integer sumConfirmedSeatsByTripId(@Param("tripId") Long tripId);

    boolean existsByPassenger_IdAndTrip_IdAndStatus(Long passengerId, Long tripId, BookingStatus status);

    List<Booking> findByTrip_IdAndStatus(Long tripId, BookingStatus status);

    Page<Booking> findByPassenger_Id(Long passengerId, Pageable pageable);
    Page<Booking> findByPassenger_IdAndStatus(Long passengerId, BookingStatus status, Pageable pageable);

    Page<Booking> findByTrip_Operator_Id(Long operatorId, Pageable pageable);
}
