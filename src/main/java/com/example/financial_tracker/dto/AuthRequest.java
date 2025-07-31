package com.example.financial_tracker.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class AuthRequest {
  @NotBlank(message = "Email is required")
  @Email(message = "Email must be valid")
  @Size(max = 100, message = "Email cannot exceed 100 characters")
  private String email;

  @NotBlank(message = "Password is required")
  @Size(min = 6, max = 100, message = "Password must be between 6 and 100 characters")
  @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).*$",
    message = "Password must contain at least one lowercase letter, one uppercase letter, and one digit")
  private String password;

  // For registration only
  @Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters")
  @Pattern(regexp = "^[a-zA-Z\\s]+$", message = "Name can only contain letters and spaces")
  private String name;
}
