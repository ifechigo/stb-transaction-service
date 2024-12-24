package com.suntrustbank.transactions.providers.repository.enums;


import lombok.Getter;

@Getter
public enum TransactionStatus {
    SUCCESS,
    FAILED,
    PENDING,
    REFUNDED
}
