package com.maryam.masar.service;

import com.maryam.masar.dto.TopUpRequest;
import com.maryam.masar.dto.WalletTransactionResponse;
import com.maryam.masar.entity.Passenger;
import com.maryam.masar.entity.TransactionType;
import com.maryam.masar.entity.WalletTransaction;
import com.maryam.masar.exception.NotFoundException;
import com.maryam.masar.repository.PassengerRepository;
import com.maryam.masar.repository.WalletTransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Service
public class WalletService {

    private final PassengerRepository passengerRepository;
    private final WalletTransactionRepository walletTransactionRepository;

    public WalletService(PassengerRepository passengerRepository,
                         WalletTransactionRepository walletTransactionRepository) {
        this.passengerRepository = passengerRepository;
        this.walletTransactionRepository = walletTransactionRepository;
    }

    @Transactional
    public WalletTransactionResponse topUp(TopUpRequest request) {
        Passenger passenger = passengerRepository.findById(request.getPassengerId())
                .orElseThrow(() -> new NotFoundException("Passenger not found"));

        BigDecimal newBalance = passenger.getWalletBalance().add(request.getAmount());
        passenger.setWalletBalance(newBalance);
        passengerRepository.save(passenger);

        WalletTransaction transaction = new WalletTransaction();
        transaction.setType(TransactionType.TOP_UP);
        transaction.setAmount(request.getAmount());
        transaction.setBalanceAfter(newBalance);
        transaction.setPassenger(passenger);
        transaction.setBooking(null); // TOP_UP has no related booking
        transaction.setCreatedAt(OffsetDateTime.now());
        WalletTransaction saved = walletTransactionRepository.save(transaction);

        return new WalletTransactionResponse(
                saved.getId(),
                saved.getType().name(),
                saved.getAmount(),
                saved.getBalanceAfter(),
                null,
                saved.getCreatedAt()
        );
    }
}