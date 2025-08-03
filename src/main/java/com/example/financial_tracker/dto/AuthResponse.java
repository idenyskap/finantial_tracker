package com.example.financial_tracker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
  private String token;
  private boolean requiresTwoFactor;
  private String status;

  // Constructor for successful login
  public AuthResponse(String token) {
    this.token = token;
    this.requiresTwoFactor = false;
    this.status = "SUCCESS";
  }
}
