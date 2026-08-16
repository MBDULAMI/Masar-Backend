package com.maryam.masar.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

public class BookingResponse {
    private Long id;
    private String reference;
    private Long tripId;
    private String originCity;
    private String destinationCity;
    private OffsetDateTime departureTime;
    private int seatCount;
    private BigDecimal totalAmount;
    private String status;
    private OffsetDateTime createdAt;
    private List<TicketResponse> tickets;

    public BookingResponse() {}

    public BookingResponse(Long id, String reference, Long tripId, String originCity,
                           String destinationCity, OffsetDateTime departureTime,
                           int seatCount, BigDecimal totalAmount, String status,
                           OffsetDateTime createdAt, List<TicketResponse> tickets) {
        this.id = id;
        this.reference = reference;
        this.tripId = tripId;
        this.originCity = originCity;
        this.destinationCity = destinationCity;
        this.departureTime = departureTime;
        this.seatCount = seatCount;
        this.totalAmount = totalAmount;
        this.status = status;
        this.createdAt = createdAt;
        this.tickets = tickets;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getReference() { return reference; }
    public void setReference(String reference) { this.reference = reference; }
    public Long getTripId() { return tripId; }
    public void setTripId(Long tripId) { this.tripId = tripId; }
    public String getOriginCity() { return originCity; }
    public void setOriginCity(String originCity) { this.originCity = originCity; }
    public String getDestinationCity() { return destinationCity; }
    public void setDestinationCity(String destinationCity) { this.destinationCity = destinationCity; }
    public OffsetDateTime getDepartureTime() { return departureTime; }
    public void setDepartureTime(OffsetDateTime departureTime) { this.departureTime = departureTime; }
    public int getSeatCount() { return seatCount; }
    public void setSeatCount(int seatCount) { this.seatCount = seatCount; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }
    public List<TicketResponse> getTickets() { return tickets; }
    public void setTickets(List<TicketResponse> tickets) { this.tickets = tickets; }
}