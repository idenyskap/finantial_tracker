package com.example.financial_tracker.dto;

import com.example.financial_tracker.entity.BudgetPeriod;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BudgetDTO {
  private Long id;

  @NotBlank(message = "Budget name is required")
  @Size(min = 1, max = 100, message = "Name must be between 1 and 100 characters")
  private String name;

  @NotNull(message = "Amount is required")
  @DecimalMin(value = "0.01", message = "Amount must be positive")
  @DecimalMax(value = "999999999.99", message = "Amount is too large")
  private BigDecimal amount;

  @NotNull(message = "Period is required")
  private BudgetPeriod period;

  private Long categoryId;
  private String categoryName;
  private String categoryColor;

  private boolean active = true;

  @Min(1)
  @Max(100)
  private Integer notifyThreshold = 80;

  private BigDecimal spent;
  private BigDecimal remaining;
  private BigDecimal percentUsed;
  private boolean overBudget;

  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
}
