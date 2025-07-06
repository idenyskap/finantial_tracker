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
public class TransactionSearchStatsDTO {
  private Integer totalCount;
  private BigDecimal totalIncome;
  private BigDecimal totalExpense;
  private BigDecimal netAmount;
}
