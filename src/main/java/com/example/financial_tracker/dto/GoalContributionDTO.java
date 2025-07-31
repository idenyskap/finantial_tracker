package com.example.financial_tracker.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoalContributionDTO {

  @NotNull(message = "Goal ID is required")
  private Long goalId;

  @NotNull(message = "Amount is required")
  @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
  private BigDecimal amount;

  @NotNull(message = "Contribution type is required")
  private ContributionType type;

  @Size(max = 200, message = "Note must not exceed 200 characters")
  private String note;

  private LocalDate date;

  public enum ContributionType {
    ADD, WITHDRAW
  }
}
