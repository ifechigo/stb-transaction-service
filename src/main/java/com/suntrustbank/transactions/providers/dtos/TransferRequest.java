package com.suntrustbank.transactions.providers.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.suntrustbank.transactions.providers.dtos.enums.Source;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class TransferRequest {
    @NotBlank(message = "destinationAccountNo is required and cannot be empty")
    @Pattern(regexp = "\\d{10}", message = "destination account number must be exactly 10 digits")
    private String destinationAccountNo;

    @NotBlank(message = "destinationAccountName is required and cannot be empty")
    private String destinationAccountName;

    private String reference;

    @NotBlank(message = "bankCode is required and cannot be empty")
    @Pattern(regexp = "\\d{3}", message = "bank code must be exactly 3 digits")
    private String bankCode;

    @DecimalMin(value = "0.01", message = "amount must be greater than zero")
    private BigDecimal amount;

    private String description;

    @NotBlank(message = "source is required and cannot be empty")
    private String source;

    @NotBlank(message = "pin is required and cannot be empty")
    private String pin;
}
