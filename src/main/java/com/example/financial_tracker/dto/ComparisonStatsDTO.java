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
public class ComparisonStatsDTO {
  private PeriodStatsDTO currentPeriod;
  private PeriodStatsDTO previousPeriod;
  private BigDecimal incomeChange;
  private BigDecimal expenseChange;
  private BigDecimal incomeChangePercent;
  private BigDecimal expenseChangePercent;
}
