package com.suntrustbank.transactions.providers.repository;

import com.suntrustbank.transactions.providers.repository.models.POSTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface POSTransactionRepository extends JpaRepository<POSTransaction, String> {
}
