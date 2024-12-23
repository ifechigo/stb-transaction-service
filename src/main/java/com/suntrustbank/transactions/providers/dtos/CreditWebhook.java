package com.suntrustbank.transactions.providers.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreditWebhook {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String initiatorId;

    private String reference;
    private String amount;
    private String fees;
    private String transactionDate;
    private String description;
}
