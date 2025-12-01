package com.example.financial_tracker.dto;

import com.example.financial_tracker.enumerations.Currency;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CurrencyBalanceDTO {
  private Currency currency;
  private BigDecimal balance;
  private BigDecimal income;
  private BigDecimal expense;
  private BigDecimal convertedBalance;
  private int transactionCount;
}
