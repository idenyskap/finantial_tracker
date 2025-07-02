package com.example.financial_tracker.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class TransactionDTO {
  private Long id;

  @NotNull(message = "Amount is required")
  @DecimalMin(value = "0.01", message = "Amount must be positive")
  private BigDecimal amount;

  @NotBlank(message = "Transaction type is required")
  @Pattern(regexp = "INCOME|EXPENSE", message = "Type must be INCOME or EXPENSE")
  private String type;

  @NotNull(message = "Category is required")
  private Long categoryId;

  private String categoryName;
  private String categoryColor;

  @NotNull(message = "Date is required")
  private LocalDate date;

  @Size(max = 500, message = "Description cannot exceed 500 characters")
  private String description;

  private Long userId;
}
