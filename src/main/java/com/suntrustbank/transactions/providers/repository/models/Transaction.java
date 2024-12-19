package com.suntrustbank.transactions.providers.repository.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.suntrustbank.transactions.providers.repository.enums.Status;
import com.suntrustbank.transactions.providers.repository.enums.TransactionType;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.util.Date;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Entity(name = "transactions")
public class Transaction {
    @Id
    private String id;

    private String reference;

    private TransactionType transactionType;

    private BigDecimal amount;

    private double fee;

    private Status status;

    private  String statusDescription;

    private String currency = "NGN";

    private String initiatorId; //businessId

    private String description;

    private Date transactionDate;

    @CreationTimestamp
    private Date createdAt;

    @UpdateTimestamp
    private Date updatedAt;
}
