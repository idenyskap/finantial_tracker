package com.example.financial_tracker.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SavedSearchDTO {
  private Long id;

  @NotBlank(message = "Name is required")
  @Size(min = 1, max = 100, message = "Name must be between 1 and 100 characters")
  private String name;

  private TransactionSearchDTO searchCriteria;

  private LocalDateTime createdAt;
}
