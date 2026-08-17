package com.maryam.masar.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(name = "trips")
public class Trip {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "operator_id", nullable = false)
    private Operator operator;

    @Column(nullable = false)
    private String originCity;

    @Column(nullable = false)
    private String destinationCity;

    @Column(nullable = false)
    private OffsetDateTime departureTime;

    @Column(nullable = false)
    private OffsetDateTime arrivalTime;

    @Column(nullable = false)
    private String busCode;

    @Column(nullable = false)
    private Integer totalSeats;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal seatPrice;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TripStatus status;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Operator getOperator() {
        return operator;
    }

    public void setOperator(Operator operator) {
        this.operator = operator;
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

    public TripStatus getStatus() {
        return status;
    }

    public void setStatus(TripStatus status) {
        this.status = status;
    }
}