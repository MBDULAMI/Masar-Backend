package com.maryam.masar.repository;

import com.maryam.masar.entity.Operator;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface OperatorRepository extends JpaRepository<Operator, Long> {
    Optional<Operator> findByCode(String code);
    Optional<Operator> findByOwner_Id(Long ownerId);
}