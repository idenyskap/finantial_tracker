package com.example.financial_tracker.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class EmailChangeRequest {

  @NotBlank(message = "New email is required")
  @Email(message = "Invalid email format")
  private String newEmail;

  @NotBlank(message = "Password is required")
  private String password;
}
