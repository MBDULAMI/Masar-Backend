package com.maryam.masar.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class BookingRequest {

    @NotNull
    private Long tripId;

    @NotNull
    @Min(value = 1, message = "Must book at least 1 seat")
    @Max(value = 4, message = "Cannot book more than 4 seats")
    private Integer seatCount;

    public BookingRequest() {}

    public Long getTripId() { return tripId; }
    public void setTripId(Long tripId) { this.tripId = tripId; }
    public Integer getSeatCount() { return seatCount; }
    public void setSeatCount(Integer seatCount) { this.seatCount = seatCount; }
}