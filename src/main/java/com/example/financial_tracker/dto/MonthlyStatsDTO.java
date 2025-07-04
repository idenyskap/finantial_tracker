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
public class MonthlyStatsDTO {
  private String month; // Format: "2025-07"
  private String monthName; // Format: "July 2025"
  private BigDecimal income;
  private BigDecimal expense;
  private BigDecimal netChange;
  private Integer transactionCount;
}
