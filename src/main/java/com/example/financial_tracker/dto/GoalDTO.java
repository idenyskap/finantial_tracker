package com.example.financial_tracker.dto;

import com.example.financial_tracker.entity.GoalPriority;
import com.example.financial_tracker.entity.GoalStatus;
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
public class GoalDTO {

  private Long id;

  @NotBlank(message = "Name is required")
  @Size(max = 100, message = "Name must not exceed 100 characters")
  private String name;

  @Size(max = 500, message = "Description must not exceed 500 characters")
  private String description;

  @NotNull(message = "Target amount is required")
  @DecimalMin(value = "0.01", message = "Target amount must be greater than 0")
  @Digits(integer = 8, fraction = 2, message = "Amount must have at most 8 digits and 2 decimal places")
  private BigDecimal targetAmount;

  private BigDecimal currentAmount;

  @NotNull(message = "Target date is required")
  @Future(message = "Target date must be in the future")
  private LocalDate targetDate;

  private Long categoryId;
  private String categoryName;
  private String categoryColor;

  private GoalStatus status;
  private GoalPriority priority;

  @Pattern(regexp = "^#[0-9A-Fa-f]{6}$", message = "Color must be a valid hex color")
  private String color;

  private String icon;

  private BigDecimal progressPercentage;
  private BigDecimal remainingAmount;
  private Integer daysRemaining;
  private BigDecimal requiredMonthlySaving;
  private BigDecimal requiredWeeklySaving;
  private BigDecimal requiredDailySaving;
  private Boolean isOverdue;
  private Boolean isCompleted;

  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
  private LocalDateTime completedAt;
}
