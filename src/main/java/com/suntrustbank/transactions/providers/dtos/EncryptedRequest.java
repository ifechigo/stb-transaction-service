package com.suntrustbank.transactions.providers.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EncryptedRequest {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String initiatorId;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String userAgent;

    @NotBlank(message = "data field must be present and cannot be empty")
    private String data;
}
