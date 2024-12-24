package com.suntrustbank.transactions.providers.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class PinRequest {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String initiatorId;

    @NotBlank(message = "pin field cannot be empty")
    private String pin;
}
