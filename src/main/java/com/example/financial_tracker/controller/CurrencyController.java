package com.example.financial_tracker.controller;

import com.example.financial_tracker.dto.CurrencyConversionDTO;
import com.example.financial_tracker.dto.ExchangeRateDTO;
import com.example.financial_tracker.dto.UserCurrencyPreferenceDTO;
import com.example.financial_tracker.entity.Currency;
import com.example.financial_tracker.entity.User;
import com.example.financial_tracker.service.CurrencyService;
import com.example.financial_tracker.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/v1/currency")
@RequiredArgsConstructor
public class CurrencyController {

  private final CurrencyService currencyService;
  private final UserService userService;

  @GetMapping("/available")
  public ResponseEntity<List<Map<String, String>>> getAvailableCurrencies() {
    List<Map<String, String>> currencies = Arrays.stream(Currency.values())
      .map(currency -> Map.of(
        "code", currency.name(),
        "name", currency.getDisplayName(),
        "symbol", currency.getSymbol()
      ))
      .collect(Collectors.toList());

    return ResponseEntity.ok(currencies);
  }

  @PostMapping("/convert")
  public ResponseEntity<CurrencyConversionDTO> convertCurrency(
    @Valid @RequestBody CurrencyConversionDTO request) {

    log.info("Converting {} {} to {}",
      request.getAmount(), request.getFromCurrency(), request.getToCurrency());

    CurrencyConversionDTO result = currencyService.convertAmount(request);
    return ResponseEntity.ok(result);
  }

  @GetMapping("/rates/{baseCurrency}")
  public ResponseEntity<List<ExchangeRateDTO>> getExchangeRates(
    @PathVariable Currency baseCurrency) {

    List<ExchangeRateDTO> rates = currencyService.getCurrentRates(baseCurrency);
    return ResponseEntity.ok(rates);
  }

  @PostMapping("/rates/update")
  public ResponseEntity<?> updateExchangeRates() {
    log.info("Manually triggering exchange rate update");

    try {
      currencyService.updateExchangeRates();
      return ResponseEntity.ok(Map.of(
        "message", "Exchange rates updated successfully"
      ));
    } catch (Exception e) {
      return ResponseEntity.internalServerError().body(Map.of(
        "error", "Failed to update exchange rates: " + e.getMessage()
      ));
    }
  }

  @GetMapping("/preferences")
  public ResponseEntity<UserCurrencyPreferenceDTO> getUserCurrencyPreferences(
    @AuthenticationPrincipal User user) {

    UserCurrencyPreferenceDTO preferences = UserCurrencyPreferenceDTO.builder()
      .defaultCurrency(user.getDefaultCurrency())
      .displaySecondary(user.isDisplaySecondaryCurrency())
      .secondaryCurrency(user.getSecondaryCurrency())
      .build();

    return ResponseEntity.ok(preferences);
  }

  @PutMapping("/preferences")
  public ResponseEntity<UserCurrencyPreferenceDTO> updateUserCurrencyPreferences(
    @AuthenticationPrincipal User user,
    @Valid @RequestBody UserCurrencyPreferenceDTO preferences) {

    log.info("Updating currency preferences for user: {}", user.getEmail());

    user.setDefaultCurrency(preferences.getDefaultCurrency());
    user.setDisplaySecondaryCurrency(preferences.isDisplaySecondary());
    user.setSecondaryCurrency(preferences.getSecondaryCurrency());

    userService.saveUser(user);

    return ResponseEntity.ok(preferences);
  }

  @PostMapping("/rates/manual")
  public ResponseEntity<?> createManualExchangeRate(
    @Valid @RequestBody ExchangeRateDTO rateDto) {

    log.info("Creating manual exchange rate: {} to {} = {}",
      rateDto.getFromCurrency(), rateDto.getToCurrency(), rateDto.getRate());

    try {
      currencyService.createManualRate(rateDto);
      return ResponseEntity.ok(Map.of(
        "message", "Exchange rate created successfully"
      ));
    } catch (Exception e) {
      return ResponseEntity.badRequest().body(Map.of(
        "error", e.getMessage()
      ));
    }
  }
}
