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
public class CategoryMonthlyStatsDTO {
  private Long categoryId;
  private String categoryName;
  private String categoryColor;
  private List<MonthlyAmountDTO> monthlyData;
  
  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class MonthlyAmountDTO {
    private String month; // Format: YYYY-MM
    private String monthName; // Format: MMM YYYY
    private BigDecimal amount;
    private Integer transactionCount;
  }
}