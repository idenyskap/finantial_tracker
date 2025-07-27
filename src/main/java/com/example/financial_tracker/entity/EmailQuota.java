package com.example.financial_tracker.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "t_email_quota")
public class EmailQuota {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true)
  private LocalDate date;

  @Column(name = "daily_count", nullable = false)
  private Integer dailyCount = 0;

  @Column(name = "monthly_count", nullable = false)
  private Integer monthlyCount = 0;

  @Column(name = "created_at", nullable = false)
  private LocalDateTime createdAt = LocalDateTime.now();
}
