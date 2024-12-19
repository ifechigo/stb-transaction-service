package com.suntrustbank.transactions.providers.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreditWebhook {
    private String initiatorId;
    private String reference;
    private String amount;
    private String fees;
    private String transactionDate;
    private String description;
}
