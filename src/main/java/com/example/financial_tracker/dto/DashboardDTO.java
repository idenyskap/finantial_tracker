package com.example.financial_tracker.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardDTO {

  private BigDecimal currentBalance;
  private BigDecimal totalIncome;
  private BigDecimal totalExpense;

  private BigDecimal monthlyIncome;
  private BigDecimal monthlyExpense;
  private BigDecimal monthlyBalance;

  private List<TransactionDTO> recentTransactions;

  private List<CategoryStatsDTO> topExpenseCategories;

  private List<DailyStatsDTO> dailyStats;

  private BigDecimal incomeChangePercent;
  private BigDecimal expenseChangePercent;
}
