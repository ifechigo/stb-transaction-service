package com.suntrustbank.transactions.providers.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class POSRequest {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String initiatorId;

    private String agentAccountNumber;
    private String pan;
    private String rrn;
    private String stan;
    private int amount;
    private int serviceFee;
    private String reference;
    private String cardExpiry;
    private String status;
    private String statusCode;
    private String customerName;
    private String transactionType;
    private String terminalId;
    private String serialNumber;
    private String statusDescription;
    private long transactionDate;
    private String invoiceId;
    private String hash;
}
