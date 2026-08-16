package com.maryam.masar.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class TopUpRequest {

    @NotNull
    private Long passengerId;

    @NotNull
    @DecimalMin(value = "0.01", message = "Top-up amount must be positive")
    private BigDecimal amount;

    public TopUpRequest() {}

    public Long getPassengerId() { return passengerId; }
    public void setPassengerId(Long passengerId) { this.passengerId = passengerId; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
}