package com.suntrustbank.transactions.providers.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
public class PinRequest {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String initiatorId;

    @NotBlank(message = "new pin field cannot be empty")
    @Pattern(regexp = "\\d{4}", message = "pin must be exactly 4 digits")
    private String newPin;

    @NotBlank(message = "confirm pin field cannot be empty")
    @Pattern(regexp = "\\d{4}", message = "confirm pin must be exactly 4 digits")
    private String confirmPin;

    @AssertTrue(message = "pins provided must match")
    public boolean isEqual() {
        return Objects.equals(newPin, confirmPin);
    }
}
