package com.example.financial_tracker.dto;

import com.example.financial_tracker.enumerations.Currency;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
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
public class ExchangeRateDTO {
  private Long id;

  @NotNull(message = "From currency is required")
  private Currency fromCurrency;

  @NotNull(message = "To currency is required")
  private Currency toCurrency;

  @NotNull(message = "Rate is required")
  @Positive(message = "Rate must be positive")
  private BigDecimal rate;

  private LocalDateTime validFrom;
  private LocalDateTime validTo;
  private String source;
}
