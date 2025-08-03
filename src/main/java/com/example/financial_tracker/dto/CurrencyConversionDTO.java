package com.example.financial_tracker.dto;

import com.example.financial_tracker.entity.Currency;
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
public class CurrencyConversionDTO {

  @NotNull(message = "Amount is required")
  @Positive(message = "Amount must be positive")
  private BigDecimal amount;

  @NotNull(message = "From currency is required")
  private Currency fromCurrency;

  @NotNull(message = "To currency is required")
  private Currency toCurrency;

  private BigDecimal convertedAmount;
  private BigDecimal exchangeRate;
  private LocalDateTime conversionDate;
}
