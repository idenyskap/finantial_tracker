package com.example.financial_tracker.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PasswordConfirmRequest {
  @NotBlank(message = "Password is required")
  private String password;
}
