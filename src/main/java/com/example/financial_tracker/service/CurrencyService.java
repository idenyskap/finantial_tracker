package com.example.financial_tracker.service;

import com.example.financial_tracker.dto.CurrencyConversionDTO;
import com.example.financial_tracker.dto.ExchangeRateDTO;
import com.example.financial_tracker.entity.Currency;
import com.example.financial_tracker.entity.ExchangeRate;
import com.example.financial_tracker.exception.BusinessLogicException;
import com.example.financial_tracker.repository.ExchangeRateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CurrencyService {

  private final ExchangeRateRepository exchangeRateRepository;
  private final RestTemplate restTemplate = new RestTemplate();

  // Free API for exchange rates (you can replace with your preferred provider)
  private static final String EXCHANGE_API_URL = "https://api.exchangerate-api.com/v4/latest/";

  public void initializeExchangeRatesIfNeeded() {
    log.info("Checking for existing exchange rates...");
    // Check if rates exist, if not fetch them
    List<ExchangeRate> existingRates = exchangeRateRepository
      .findCurrentRatesForCurrency(Currency.USD, LocalDateTime.now());

    if (existingRates.isEmpty()) {
      log.info("No exchange rates found, initializing from external API...");
      updateExchangeRates();
    } else {
      log.info("Exchange rates already exist ({} rates), skipping initialization", existingRates.size());
    }
  }

  public BigDecimal convert(BigDecimal amount, Currency from, Currency to) {
    if (from == to) {
      return amount;
    }

    BigDecimal rate = getExchangeRate(from, to);
    return amount.multiply(rate).setScale(to.getDecimalPlaces(), RoundingMode.HALF_UP);
  }

  @Cacheable(value = "exchangeRates", key = "#from.name() + '-' + #to.name()")
  public BigDecimal getExchangeRate(Currency from, Currency to) {
    log.debug("Getting exchange rate from {} to {}", from, to);

    // Try to get from database first
    ExchangeRate rate = exchangeRateRepository
      .findLatestRate(from, to, LocalDateTime.now())
      .orElseGet(() -> {
        // If not found, try reverse rate
        return exchangeRateRepository
          .findLatestRate(to, from, LocalDateTime.now())
          .map(reverseRate -> {
            // Calculate reverse rate
            BigDecimal reversed = BigDecimal.ONE.divide(
              reverseRate.getRate(), 6, RoundingMode.HALF_UP
            );
            return createReverseRate(reverseRate, reversed);
          })
          .orElseThrow(() -> new BusinessLogicException(
            "Exchange rate not found for " + from + " to " + to
          ));
      });

    return rate.getRate();
  }

  private ExchangeRate createReverseRate(ExchangeRate original, BigDecimal reversedRate) {
    ExchangeRate reversed = new ExchangeRate();
    reversed.setFromCurrency(original.getToCurrency());
    reversed.setToCurrency(original.getFromCurrency());
    reversed.setRate(reversedRate);
    reversed.setValidFrom(original.getValidFrom());
    reversed.setValidTo(original.getValidTo());
    reversed.setSource(original.getSource() + "_REVERSED");
    return reversed;
  }

  @Scheduled(cron = "0 0 */6 * * *") // Every 6 hours
  public void updateExchangeRates() {
    log.info("Updating exchange rates from external API");

    try {
      // Update rates for major currencies
      updateRatesForCurrency(Currency.USD);
      updateRatesForCurrency(Currency.EUR);
      updateRatesForCurrency(Currency.GBP);

    } catch (Exception e) {
      log.error("Failed to update exchange rates", e);
    }
  }

  @SuppressWarnings("unchecked")
  private void updateRatesForCurrency(Currency baseCurrency) {
    try {
      String url = EXCHANGE_API_URL + baseCurrency.name();
      Map<String, Object> response = restTemplate.getForObject(url, Map.class);

      if (response != null && response.containsKey("rates")) {
        Map<String, Number> rates = (Map<String, Number>) response.get("rates");
        LocalDateTime now = LocalDateTime.now();

        rates.forEach((currencyCode, rate) -> {
          try {
            Currency toCurrency = Currency.valueOf(currencyCode);

            // Close existing rates
            exchangeRateRepository.closeExistingRates(baseCurrency, toCurrency, now);

            // Create new rate
            ExchangeRate exchangeRate = new ExchangeRate();
            exchangeRate.setFromCurrency(baseCurrency);
            exchangeRate.setToCurrency(toCurrency);
            exchangeRate.setRate(new BigDecimal(rate.toString()));
            exchangeRate.setValidFrom(now);
            exchangeRate.setSource("ExchangeRate-API");

            exchangeRateRepository.save(exchangeRate);

          } catch (IllegalArgumentException e) {
            // Currency not supported in our enum
            log.trace("Skipping unsupported currency: {}", currencyCode);
          }
        });

        log.info("Updated {} exchange rates for base currency {}",
          rates.size(), baseCurrency);
      }
    } catch (Exception e) {
      log.error("Failed to update rates for currency {}", baseCurrency, e);
    }
  }

  public List<ExchangeRateDTO> getCurrentRates(Currency baseCurrency) {
    List<ExchangeRate> rates = exchangeRateRepository
      .findCurrentRatesForCurrency(baseCurrency, LocalDateTime.now());

    return rates.stream()
      .map(this::mapToDTO)
      .collect(Collectors.toList());
  }

  public CurrencyConversionDTO convertAmount(CurrencyConversionDTO request) {
    BigDecimal convertedAmount = convert(
      request.getAmount(),
      request.getFromCurrency(),
      request.getToCurrency()
    );

    BigDecimal rate = getExchangeRate(
      request.getFromCurrency(),
      request.getToCurrency()
    );

    request.setConvertedAmount(convertedAmount);
    request.setExchangeRate(rate);
    request.setConversionDate(LocalDateTime.now());

    return request;
  }

  private ExchangeRateDTO mapToDTO(ExchangeRate rate) {
    return ExchangeRateDTO.builder()
      .id(rate.getId())
      .fromCurrency(rate.getFromCurrency())
      .toCurrency(rate.getToCurrency())
      .rate(rate.getRate())
      .validFrom(rate.getValidFrom())
      .validTo(rate.getValidTo())
      .source(rate.getSource())
      .build();
  }

  public void createManualRate(ExchangeRateDTO dto) {
    log.info("Creating manual exchange rate from {} to {} with rate {}",
      dto.getFromCurrency(), dto.getToCurrency(), dto.getRate());

    // Close any existing rates
    exchangeRateRepository.closeExistingRates(
      dto.getFromCurrency(),
      dto.getToCurrency(),
      LocalDateTime.now()
    );

    ExchangeRate rate = new ExchangeRate();
    rate.setFromCurrency(dto.getFromCurrency());
    rate.setToCurrency(dto.getToCurrency());
    rate.setRate(dto.getRate());
    rate.setValidFrom(LocalDateTime.now());
    rate.setSource("MANUAL");

    exchangeRateRepository.save(rate);
  }
}
