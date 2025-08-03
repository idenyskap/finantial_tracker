package com.example.financial_tracker.dto;

import com.example.financial_tracker.entity.Currency;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

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

  private Currency primaryCurrency;
  private Map<Currency, CurrencyBalanceDTO> currencyBalances;

  private BigDecimal currentBalanceSecondary;
  private BigDecimal totalIncomeSecondary;
  private BigDecimal totalExpenseSecondary;
  private Currency secondaryCurrency;
}
