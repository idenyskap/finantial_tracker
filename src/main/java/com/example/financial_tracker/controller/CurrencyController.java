package com.example.financial_tracker.controller;

import com.example.financial_tracker.dto.CurrencyConversionDTO;
import com.example.financial_tracker.dto.CurrencyInfoDTO;
import com.example.financial_tracker.dto.ExchangeRateDTO;
import com.example.financial_tracker.dto.UserCurrencyPreferenceDTO;
import com.example.financial_tracker.enumerations.Currency;
import com.example.financial_tracker.entity.User;
import com.example.financial_tracker.service.CurrencyService;
import com.example.financial_tracker.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/currency")
@RequiredArgsConstructor
public class CurrencyController {

  private final CurrencyService currencyService;
  private final UserService userService;

  @GetMapping("/available")
  public ResponseEntity<List<CurrencyInfoDTO>> getAvailableCurrencies() {
    return ResponseEntity.ok(currencyService.getAvailableCurrencies());
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
  public ResponseEntity<Map<String, String>> updateExchangeRates() {
    log.info("Manually triggering exchange rate update");
    currencyService.updateExchangeRates();
    return ResponseEntity.ok(Map.of("message", "Exchange rates updated successfully"));
  }

  @GetMapping("/preferences")
  public ResponseEntity<UserCurrencyPreferenceDTO> getUserCurrencyPreferences(
    @AuthenticationPrincipal User user) {
    return ResponseEntity.ok(currencyService.getUserPreferences(user));
  }

  @PutMapping("/preferences")
  public ResponseEntity<UserCurrencyPreferenceDTO> updateUserCurrencyPreferences(
    @AuthenticationPrincipal User user,
    @Valid @RequestBody UserCurrencyPreferenceDTO preferences) {

    log.info("Updating currency preferences for user: {}", user.getEmail());
    UserCurrencyPreferenceDTO updated = userService.updateCurrencyPreferences(user, preferences);
    return ResponseEntity.ok(updated);
  }

  @PostMapping("/rates/manual")
  public ResponseEntity<Map<String, String>> createManualExchangeRate(
    @Valid @RequestBody ExchangeRateDTO rateDto) {

    log.info("Creating manual exchange rate: {} to {} = {}",
      rateDto.getFromCurrency(), rateDto.getToCurrency(), rateDto.getRate());

    currencyService.createManualRate(rateDto);
    return ResponseEntity.ok(Map.of("message", "Exchange rate created successfully"));
  }
}
