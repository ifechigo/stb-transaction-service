package com.suntrustbank.transactions.providers.repository.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.suntrustbank.transactions.providers.repository.enums.Status;
import com.suntrustbank.transactions.providers.repository.enums.TransactionType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "transactions")
public class Transaction {
    @Id
    private String id;

    @Column(unique = true)
    private String reference;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;

    private BigDecimal amount;

    private double fee;

    private Status status;

    private  String statusDescription;

    private String currency = "NGN";

    private String initiatorId; //userId

    private String description;

    @Column(nullable = false, updatable = false)
    private long createdAt;

    @Column(nullable = false)
    private long updatedAt;

    @PrePersist
    public void setUp() {
        this.createdAt = Instant.now().toEpochMilli();
        this.updatedAt = Instant.now().toEpochMilli();
    }
}
