package com.maryam.masar.repository;

import com.maryam.masar.entity.WalletTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
public interface WalletTransactionRepository extends JpaRepository<WalletTransaction, Long> {
    Page<WalletTransaction> findByPassenger_IdOrderByCreatedAtDesc(Long passengerId, Pageable pageable);
}
