package com.maryam.masar.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public class TripSearchResponse {
    private Long id;
    private String originCity;
    private String destinationCity;
    private OffsetDateTime departureTime;
    private OffsetDateTime arrivalTime;
    private String operatorName;
    private BigDecimal seatPrice;
    private int availableSeats;
    private String status;

    public TripSearchResponse() {}

    public TripSearchResponse(Long id, String originCity, String destinationCity,
                              OffsetDateTime departureTime, OffsetDateTime arrivalTime,
                              String operatorName, BigDecimal seatPrice,
                              int availableSeats, String status) {
        this.id = id;
        this.originCity = originCity;
        this.destinationCity = destinationCity;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.operatorName = operatorName;
        this.seatPrice = seatPrice;
        this.availableSeats = availableSeats;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOriginCity() {
        return originCity;
    }

    public void setOriginCity(String originCity) {
        this.originCity = originCity;
    }

    public String getDestinationCity() {
        return destinationCity;
    }

    public void setDestinationCity(String destinationCity) {
        this.destinationCity = destinationCity;
    }

    public OffsetDateTime getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(OffsetDateTime departureTime) {
        this.departureTime = departureTime;
    }

    public OffsetDateTime getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(OffsetDateTime arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public String getOperatorName() {
        return operatorName;
    }

    public void setOperatorName(String operatorName) {
        this.operatorName = operatorName;
    }

    public BigDecimal getSeatPrice() {
        return seatPrice;
    }

    public void setSeatPrice(BigDecimal seatPrice) {
        this.seatPrice = seatPrice;
    }

    public int getAvailableSeats() {
        return availableSeats;
    }

    public void setAvailableSeats(int availableSeats) {
        this.availableSeats = availableSeats;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}