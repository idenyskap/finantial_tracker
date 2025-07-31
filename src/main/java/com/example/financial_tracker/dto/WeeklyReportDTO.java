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
public class WeeklyReportDTO {

  private LocalDate startDate;
  private LocalDate endDate;
  private BigDecimal totalIncome;
  private BigDecimal totalExpenses;
  private BigDecimal netAmount;
  private Integer totalTransactions;
  private List<CategoryExpenseDTO> topExpenseCategories;

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class CategoryExpenseDTO {
    private String categoryName;
    private BigDecimal amount;
    private BigDecimal percentOfTotal;
  }
}
