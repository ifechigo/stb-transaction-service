package com.suntrustbank.transactions.providers.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
public class TransferRequest implements Serializable {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String initiatorId;

    @NotBlank(message = "destination account number cannot be empty")
    @Pattern(regexp = "\\d{10}", message = "destination account number must be exactly 10 digits")
    private String destinationAccountNo;

    @NotBlank(message = "destination account name cannot be empty")
    private String destinationAccountName;

    private String reference;

    @NotBlank(message = "bank code cannot be empty")
    @Pattern(regexp = "\\d{3}", message = "bank code must be exactly 3 digits")
    private String bankCode;

    @DecimalMin(value = "0.01", message = "amount must be greater than zero")
    private BigDecimal amount;

    private String description;

    @NotBlank(message = "pin cannot be empty")
    private String pin;
}
