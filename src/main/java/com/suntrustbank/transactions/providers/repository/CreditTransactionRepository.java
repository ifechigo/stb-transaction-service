package com.suntrustbank.transactions.providers.repository;

import com.suntrustbank.transactions.providers.repository.models.CreditTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CreditTransactionRepository extends JpaRepository<CreditTransaction, String> {
}
