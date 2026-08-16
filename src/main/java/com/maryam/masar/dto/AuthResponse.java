package com.maryam.masar.dto;

public class AuthResponse {
    private String token;
    private String tokenType = "Bearer";
    private Long accountId;
    private String role;

    public AuthResponse() {}

    public AuthResponse(String token, Long accountId, String role) {
        this.token = token;
        this.accountId = accountId;
        this.role = role;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}