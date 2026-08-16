package com.maryam.masar.dto;

public class TicketResponse {
    private Long id;
    private int seatNumber;
    private String serial;

    public TicketResponse() {}

    public TicketResponse(Long id, int seatNumber, String serial) {
        this.id = id;
        this.seatNumber = seatNumber;
        this.serial = serial;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public int getSeatNumber() { return seatNumber; }
    public void setSeatNumber(int seatNumber) { this.seatNumber = seatNumber; }
    public String getSerial() { return serial; }
    public void setSerial(String serial) { this.serial = serial; }
}