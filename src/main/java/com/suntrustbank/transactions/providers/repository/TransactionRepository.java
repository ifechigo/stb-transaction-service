package com.suntrustbank.transactions.providers.repository;

import com.suntrustbank.transactions.providers.repository.models.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, String> {
}
