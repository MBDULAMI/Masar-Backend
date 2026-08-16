package com.maryam.masar.controller;

import com.maryam.masar.dto.WalletTransactionResponse;
import com.maryam.masar.entity.Passenger;
import com.maryam.masar.security.CustomUserDetails;
import com.maryam.masar.service.WalletService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/v1/wallet")
public class PassengerWalletController {

    private final WalletService walletService;

    public PassengerWalletController(WalletService walletService) {
        this.walletService = walletService;
    }

    @GetMapping("/balance")
    @PreAuthorize("hasRole('PASSENGER')")
    public ResponseEntity<BigDecimal> getMyBalance(@AuthenticationPrincipal CustomUserDetails userDetails) {
        Passenger currentUser = userDetails.getPassenger();
        return ResponseEntity.ok(walletService.getMyBalance(currentUser));
    }

    @GetMapping("/history")
    @PreAuthorize("hasRole('PASSENGER')")
    public ResponseEntity<Page<WalletTransactionResponse>> getMyLedger(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Passenger currentUser = userDetails.getPassenger();
        return ResponseEntity.ok(walletService.getMyLedger(currentUser, page, size));
    }
}