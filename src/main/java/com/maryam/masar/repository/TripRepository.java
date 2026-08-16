package com.maryam.masar.repository;

import com.maryam.masar.entity.Trip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TripRepository extends JpaRepository<Trip, Long> {
    @Query("SELECT t FROM Trip t WHERE " +
            "(:origin IS NULL OR t.originCity = :origin) AND " +
            "(:destination IS NULL OR t.destinationCity = :destination) AND " +
            "(:afterDate IS NULL OR t.departureTime >= :afterDate) AND " +
            "t.status = 'SCHEDULED' " +
            "ORDER BY t.departureTime ASC")
    Page<Trip> searchTrips(@Param("origin") String origin,
                           @Param("destination") String destination,
                           @Param("afterDate") java.time.OffsetDateTime afterDate,
                           Pageable pageable);
}
