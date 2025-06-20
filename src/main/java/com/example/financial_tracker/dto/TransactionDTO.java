package com.example.financial_tracker.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;


@Data
public class TransactionDTO {
  private Long id;
  private BigDecimal amount;
  private String type;
  private Long categoryId;
  private String categoryName;
  private LocalDate date;
  private String description;
}
