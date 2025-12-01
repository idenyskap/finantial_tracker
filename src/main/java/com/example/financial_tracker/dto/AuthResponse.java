package com.example.financial_tracker.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuthResponse {
  private String token;
  private boolean requiresTwoFactor;
  private String status;
  private String message;

  public AuthResponse(String token) {
    this.token = token;
    this.requiresTwoFactor = false;
    this.status = "SUCCESS";
    this.message = "Login successful";
  }

  public AuthResponse(String token, boolean requiresTwoFactor, String status) {
      this.token = token;
      this.requiresTwoFactor = requiresTwoFactor;
      this.status = status;
      this.message = requiresTwoFactor ? "Two-factor authentication required" : null;
  }
}
