package com.maryam.masar.controller;

import com.maryam.masar.dto.TopUpRequest;
import com.maryam.masar.dto.WalletTransactionResponse;
import com.maryam.masar.service.WalletService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/wallet")
public class WalletController {

    private final WalletService walletService;

    public WalletController(WalletService walletService) {
        this.walletService = walletService;
    }

    @PostMapping("/topup")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<WalletTransactionResponse> topUp(@Valid @RequestBody TopUpRequest request) {
        WalletTransactionResponse response = walletService.topUp(request);
        return ResponseEntity.status(201).body(response);
    }
}