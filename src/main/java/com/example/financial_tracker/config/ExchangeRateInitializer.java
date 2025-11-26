package com.example.financial_tracker.config;

import com.example.financial_tracker.service.CurrencyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ExchangeRateInitializer implements ApplicationRunner {

    private final CurrencyService currencyService;

    @Override
    public void run(ApplicationArguments args) {
        log.info("=== Initializing exchange rates on startup ===");
        try {
            currencyService.initializeExchangeRatesIfNeeded();
        } catch (Exception e) {
            log.error("Failed to initialize exchange rates on startup: {}", e.getMessage(), e);
        }
    }
}
