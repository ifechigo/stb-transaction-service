package com.suntrustbank.transactions.providers.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EncryptedRequest {
    @NotBlank(message = "data field must be present and cannot be empty")
    private String data;
}
