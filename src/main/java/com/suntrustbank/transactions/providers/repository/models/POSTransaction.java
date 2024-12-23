package com.suntrustbank.transactions.providers.repository.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.Date;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Entity(name = "pos_transactions")
public class POSTransaction {
    @Id
    private String id;

    @OneToOne
    @JoinColumn(name = "transaction_id", nullable = false)
    private Transaction transaction;

    private long transactionDate;
    private String accountAgentNumber;
    private String pan;
    private String rrn;
    private String stan;
    private String cardExpiry;
    private String statusCode;
    private String customerName;
    private String terminalId;
    private String serialNumber;
    private String invoiceId;

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
