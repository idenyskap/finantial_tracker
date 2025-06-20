package com.example.financial_tracker.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "t_transaction")
public class Transaction {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private BigDecimal amount;

  @Column(name = "transaction_type")
  @Enumerated(EnumType.STRING)
  private TransactionType type; // INCOME or EXPENSE

  @ManyToOne
  @JoinColumn(name = "category_id")
  private Category category;

  private LocalDate date;

  private String description;
}
