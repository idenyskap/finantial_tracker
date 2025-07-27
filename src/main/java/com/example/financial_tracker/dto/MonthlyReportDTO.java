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
public class MonthlyReportDTO {

  private Integer month;
  private Integer year;
  private LocalDate startDate;
  private LocalDate endDate;

  private BigDecimal totalIncome;
  private BigDecimal totalExpenses;
  private BigDecimal netAmount;
  private BigDecimal savingsRate;

  private List<CategorySummaryDTO> expensesByCategory;

  private Integer totalTransactions;
  private Integer incomeTransactions;
  private Integer expenseTransactions;

  private List<BudgetSummaryDTO> budgetSummary;
  private Integer budgetsExceeded;

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class CategorySummaryDTO {
    private String categoryName;
    private BigDecimal amount;
    private Integer transactionCount;
    private BigDecimal percentOfTotal;
    private BigDecimal averageAmount;
  }

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class BudgetSummaryDTO {
    private String budgetName;
    private String categoryName;
    private BigDecimal limit;
    private BigDecimal spent;
    private BigDecimal percentUsed;
    private Boolean isExceeded;
  }
}
