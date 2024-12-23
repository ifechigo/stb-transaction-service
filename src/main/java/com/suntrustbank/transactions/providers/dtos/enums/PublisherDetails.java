package com.suntrustbank.transactions.providers.dtos.enums;

import lombok.Getter;

@Getter
public enum PublisherDetails {
    PAYMENT_EXCHANGE_NAME("payment.exchange"),
    PAYMENT_ROUTING_KEY("pay");

    private final String value;

    PublisherDetails(String value) {
        this.value = value;
    }

}
