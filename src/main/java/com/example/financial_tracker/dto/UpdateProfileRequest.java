package com.example.financial_tracker.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateProfileRequest {

  @NotBlank(message = "Name is required")
  private String name;
}
