package com.example.financial_tracker.dto;

import com.example.financial_tracker.entity.Currency;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserCurrencyPreferenceDTO {
  private Currency defaultCurrency;
  private List<Currency> activeCurrencies;
  private boolean displaySecondary;
  private Currency secondaryCurrency;
}
