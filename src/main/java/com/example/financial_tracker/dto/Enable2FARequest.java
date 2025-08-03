package com.example.financial_tracker.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Set;

@Data
public class Enable2FARequest {
  @NotBlank(message = "Secret is required")
  private String secret;

  @NotBlank(message = "Verification code is required")
  private String verificationCode;

  @NotNull(message = "Recovery codes are required")
  private Set<String> recoveryCodes;
}
