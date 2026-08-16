package com.maryam.masar.dto;

public class AuthResponse {
    private String token;
    private String tokenType = "Bearer";
    private Long passengerId;
    private String role;

    public AuthResponse() {}

    public AuthResponse(String token, Long passengerId, String role) {
        this.token = token;
        this.passengerId = passengerId;
        this.role = role;
    }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public String getTokenType() { return tokenType; }
    public void setTokenType(String tokenType) { this.tokenType = tokenType; }
    public Long getPassengerId() { return passengerId; }
    public void setPassengerId(Long passengerId) { this.passengerId = passengerId; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}