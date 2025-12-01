package com.example.financial_tracker.entity;

import com.example.financial_tracker.enumerations.Currency;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@EqualsAndHashCode(of = "id")
@Entity
@Table(name = "t_exchange_rate")
public class ExchangeRate {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Enumerated(EnumType.STRING)
  @Column(name = "from_currency", nullable = false)
  private Currency fromCurrency;

  @Enumerated(EnumType.STRING)
  @Column(name = "to_currency", nullable = false)
  private Currency toCurrency;

  @Column(nullable = false, precision = 10, scale = 6)
  private BigDecimal rate;

  @Column(name = "valid_from", nullable = false)
  private LocalDateTime validFrom;

  @Column(name = "valid_to")
  private LocalDateTime validTo;

  @Column(name = "source")
  private String source;

  @Column(name = "created_at", nullable = false)
  private LocalDateTime createdAt = LocalDateTime.now();
}
