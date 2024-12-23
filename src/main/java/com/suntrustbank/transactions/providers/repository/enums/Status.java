package com.suntrustbank.transactions.providers.repository.enums;


import lombok.Getter;

@Getter
public enum Status {
    SUCCESS,
    FAILED,
    PENDING,
    REFUNDED
}
