package com.example.financial_tracker.dto;

import com.example.financial_tracker.entity.Currency;
import com.example.financial_tracker.validation.ValidTransactionAmount;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

@ValidTransactionAmount
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionDTO {
  private Long id;

  private BudgetWarningDTO budgetWarning;

  @NotNull(message = "Amount is required")
  @DecimalMin(value = "0.01", message = "Amount must be positive")
  @DecimalMax(value = "999999999.99", message = "Amount is too large")
  @Digits(integer = 9, fraction = 2, message = "Amount must have maximum 9 digits and 2 decimal places")
  private BigDecimal amount;

  private Currency currency = Currency.USD;
  private BigDecimal originalAmount;
  private BigDecimal exchangeRate;
  private BigDecimal convertedAmount;

  @NotBlank(message = "Transaction type is required")
  @Pattern(regexp = "INCOME|EXPENSE", message = "Type must be INCOME or EXPENSE")
  private String type;

  @NotNull(message = "Category is required")
  @Positive(message = "Category ID must be positive")
  private Long categoryId;

  private String categoryName;
  private String categoryColor;

  @NotNull(message = "Date is required")
  @PastOrPresent(message = "Date cannot be in the future")
  private LocalDate date;

  @Size(max = 500, message = "Description cannot exceed 500 characters")
  private String description;

  private Long userId;

  private Set<String> tags;
}
