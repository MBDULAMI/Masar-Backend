package com.maryam.masar.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public class TripResponse {
    private Long id;
    private String originCity;
    private String destinationCity;
    private OffsetDateTime departureTime;
    private OffsetDateTime arrivalTime;
    private String busCode;
    private Integer totalSeats;
    private BigDecimal seatPrice;
    private String status;
    private String operatorName;

    public TripResponse() {}

    public TripResponse(Long id, String originCity, String destinationCity,
                        OffsetDateTime departureTime, OffsetDateTime arrivalTime,
                        String busCode, Integer totalSeats, BigDecimal seatPrice,
                        String status, String operatorName) {
        this.id = id;
        this.originCity = originCity;
        this.destinationCity = destinationCity;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.busCode = busCode;
        this.totalSeats = totalSeats;
        this.seatPrice = seatPrice;
        this.status = status;
        this.operatorName = operatorName;
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

    public String getBusCode() {
        return busCode;
    }

    public void setBusCode(String busCode) {
        this.busCode = busCode;
    }

    public Integer getTotalSeats() {
        return totalSeats;
    }

    public void setTotalSeats(Integer totalSeats) {
        this.totalSeats = totalSeats;
    }

    public BigDecimal getSeatPrice() {
        return seatPrice;
    }

    public void setSeatPrice(BigDecimal seatPrice) {
        this.seatPrice = seatPrice;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getOperatorName() {
        return operatorName;
    }

    public void setOperatorName(String operatorName) {
        this.operatorName = operatorName;
    }
}