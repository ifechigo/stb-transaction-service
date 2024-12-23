package com.suntrustbank.transactions.providers.repository;

import com.suntrustbank.transactions.providers.repository.models.POSTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface POSTransactionRepository extends JpaRepository<POSTransaction, String> {
}
