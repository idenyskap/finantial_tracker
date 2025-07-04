package com.example.financial_tracker.dto;

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
public class PeriodStatsDTO {
  private LocalDate startDate;
  private LocalDate endDate;
  private String periodName;
  private BigDecimal income;
  private BigDecimal expense;
  private BigDecimal netChange;
  private Integer transactionCount;
}
