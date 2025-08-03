package com.example.financial_tracker.repository;

import com.example.financial_tracker.entity.Currency;
import com.example.financial_tracker.entity.ExchangeRate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ExchangeRateRepository extends JpaRepository<ExchangeRate, Long> {

  @Query("SELECT er FROM ExchangeRate er WHERE " +
    "er.fromCurrency = :from AND er.toCurrency = :to AND " +
    "er.validFrom <= :date AND (er.validTo IS NULL OR er.validTo > :date) " +
    "ORDER BY er.validFrom DESC")
  Optional<ExchangeRate> findLatestRate(@Param("from") Currency from,
                                        @Param("to") Currency to,
                                        @Param("date") LocalDateTime date);

  @Query("SELECT er FROM ExchangeRate er WHERE " +
    "er.fromCurrency = :currency AND " +
    "er.validFrom <= :date AND (er.validTo IS NULL OR er.validTo > :date)")
  List<ExchangeRate> findCurrentRatesForCurrency(@Param("currency") Currency currency,
                                                 @Param("date") LocalDateTime date);

  @Modifying
  @Query("UPDATE ExchangeRate er SET er.validTo = :date WHERE " +
    "er.fromCurrency = :from AND er.toCurrency = :to AND er.validTo IS NULL")
  void closeExistingRates(@Param("from") Currency from,
                          @Param("to") Currency to,
                          @Param("date") LocalDateTime date);
}
