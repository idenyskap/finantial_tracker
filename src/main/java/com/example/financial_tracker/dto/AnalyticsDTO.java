package com.example.financial_tracker.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnalyticsDTO {
  private BigDecimal totalIncome;
  private BigDecimal totalExpense;
  private BigDecimal currentBalance;
  private BigDecimal netChange;
  private List<MonthlyStatsDTO> monthlyStats;
  private List<CategoryStatsDTO> topExpenseCategories;
  private List<CategoryStatsDTO> topIncomeCategories;
  private ComparisonStatsDTO comparison;
  private List<MonthlyStatsDTO> monthlyStatsList;
}
