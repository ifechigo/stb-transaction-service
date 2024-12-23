package com.suntrustbank.transactions.providers.repository;

import com.suntrustbank.transactions.providers.repository.models.POSTransaction;
import com.suntrustbank.transactions.providers.repository.models.Transaction;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.swing.*;
import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, String> {
    Optional<POSTransaction> findByReference(String reference);

    List<Transaction> findAllByInitiatorId(@Param("initiatorId") String initiatorId, Pageable pageable);
}
