package com.example.financial_tracker.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BudgetWarningDTO {
  private Long budgetId;
  private String budgetName;
  private BigDecimal limit;
  private BigDecimal spent;
  private BigDecimal remaining;
  private BigDecimal percentUsed;
  private boolean overBudget;
  private String message;
  private WarningLevel level;

  public enum WarningLevel {
    INFO,      // < 50%
    WARNING,   // 50-80%
    ALERT,     // 80-100%
    EXCEEDED   // > 100%
  }
}
