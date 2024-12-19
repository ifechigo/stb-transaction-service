package com.suntrustbank.transactions.providers.repository;

import com.suntrustbank.transactions.providers.repository.models.DebitTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DebitTransactionRepository extends JpaRepository<DebitTransaction, String> {
}
