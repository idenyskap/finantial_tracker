package com.example.financial_tracker.dto;

import com.example.financial_tracker.entity.RecurrenceFrequency;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecurringTransactionDTO {

  private Long id;

  @NotBlank(message = "Name is required")
  @Size(max = 100, message = "Name must not exceed 100 characters")
  private String name;

  @NotNull(message = "Amount is required")
  @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
  @Digits(integer = 8, fraction = 2, message = "Amount must have at most 8 digits and 2 decimal places")
  private BigDecimal amount;

  @NotNull(message = "Type is required")
  private String type;

  @NotNull(message = "Category is required")
  private Long categoryId;
  private String categoryName;
  private String categoryColor;

  @Size(max = 500, message = "Description must not exceed 500 characters")
  private String description;

  @NotNull(message = "Frequency is required")
  private RecurrenceFrequency frequency;

  @NotNull(message = "Start date is required")
  @FutureOrPresent(message = "Start date cannot be in the past")
  private LocalDate startDate;

  @Future(message = "End date must be in the future")
  private LocalDate endDate;

  private LocalDate nextExecutionDate;
  private LocalDate lastExecutionDate;

  private Boolean active;

  @Min(value = 1, message = "Day of month must be between 1 and 31")
  @Max(value = 31, message = "Day of month must be between 1 and 31")
  private Integer dayOfMonth;

  @Min(value = 1, message = "Day of week must be between 1 and 7")
  @Max(value = 7, message = "Day of week must be between 1 and 7")
  private Integer dayOfWeek;

  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
}
