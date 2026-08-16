package com.maryam.masar.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

public class TripPublishRequest {

    @NotBlank
    private String originCity;

    @NotBlank
    private String destinationCity;

    @NotNull
    @Future(message = "Departure time must be in the future")
    private OffsetDateTime departureTime;

    @NotNull
    private OffsetDateTime arrivalTime;

    @NotBlank
    private String busCode;

    @NotNull
    @Min(value = 1, message = "Trip must have at least 1 seat")
    private Integer totalSeats;

    @NotNull
    @DecimalMin(value = "0.01", message = "Seat price must be positive")
    private BigDecimal seatPrice;

    public TripPublishRequest() {}

    public String getOriginCity() { return originCity; }
    public void setOriginCity(String originCity) { this.originCity = originCity; }
    public String getDestinationCity() { return destinationCity; }
    public void setDestinationCity(String destinationCity) { this.destinationCity = destinationCity; }
    public OffsetDateTime getDepartureTime() { return departureTime; }
    public void setDepartureTime(OffsetDateTime departureTime) { this.departureTime = departureTime; }
    public OffsetDateTime getArrivalTime() { return arrivalTime; }
    public void setArrivalTime(OffsetDateTime arrivalTime) { this.arrivalTime = arrivalTime; }
    public String getBusCode() { return busCode; }
    public void setBusCode(String busCode) { this.busCode = busCode; }
    public Integer getTotalSeats() { return totalSeats; }
    public void setTotalSeats(Integer totalSeats) { this.totalSeats = totalSeats; }
    public BigDecimal getSeatPrice() { return seatPrice; }
    public void setSeatPrice(BigDecimal seatPrice) { this.seatPrice = seatPrice; }
}