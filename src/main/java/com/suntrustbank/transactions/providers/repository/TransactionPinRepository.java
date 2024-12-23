package com.suntrustbank.transactions.providers.repository;

import com.suntrustbank.transactions.providers.repository.models.TransactionPin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TransactionPinRepository extends JpaRepository<TransactionPin, String> {
    Optional<TransactionPin> findByUserId(String userId);
}
