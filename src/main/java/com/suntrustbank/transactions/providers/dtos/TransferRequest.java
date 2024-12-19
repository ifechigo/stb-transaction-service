package com.suntrustbank.transactions.providers.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.NumberFormat;

@Getter
@Setter
public class TransferRequest {
    @NotBlank(message = "initiatorId field cannot be empty")
    private String initiatorId;

    @NotBlank(message = "destination account number cannot be empty")
    @Pattern(regexp = "\\d{10}", message = "destination account number must be exactly 10 digits")
    private String destinationAccountNo;

    private String destinationAccountName;

    private String reference;

    @NotBlank(message = "bank code cannot be empty")
    @Pattern(regexp = "\\d{3}", message = "bank code must be exactly 3 digits")
    private String bankCode;

    @NotBlank(message = "amount cannot be empty")
    private String amount;

    private String description;

    @NotBlank(message = "pin cannot be empty")
    @Pattern(regexp = "\\d{4}", message = "pin must be exactly 4 digits")
    private String pin;
}
