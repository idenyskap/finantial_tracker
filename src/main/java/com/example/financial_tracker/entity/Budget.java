package com.example.financial_tracker.entity;

import com.example.financial_tracker.enumerations.BudgetPeriod;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
@Entity
@Table(name = "budgets")
public class Budget {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String name;

  @Column(nullable = false)
  private BigDecimal amount;

  @Column(name = "period_type", nullable = false)
  @Enumerated(EnumType.STRING)
  private BudgetPeriod period;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "category_id")
  private Category category;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @Column(name = "is_active")
  private boolean active = true;

  @Column(name = "notify_threshold")
  private Integer notifyThreshold = 80;

  @Column(name = "start_date")
  private LocalDate startDate;

  @Column(name = "end_date")
  private LocalDate endDate;

  @CreationTimestamp
  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at")
  private LocalDateTime updatedAt;

  @PostLoad
  @PostPersist
  @PostUpdate
  public void calculateDates() {
    if (this.period != null) {
      LocalDate now = LocalDate.now();
      switch (this.period) {
        case WEEKLY:
          this.startDate = now.with(java.time.DayOfWeek.MONDAY);
          this.endDate = this.startDate.plusDays(6);
          break;
        case MONTHLY:
          this.startDate = now.withDayOfMonth(1);
          this.endDate = now.withDayOfMonth(now.lengthOfMonth());
          break;
        case QUARTERLY:
          int currentQuarter = (now.getMonthValue() - 1) / 3;
          this.startDate = now.withMonth(currentQuarter * 3 + 1).withDayOfMonth(1);
          this.endDate = this.startDate.plusMonths(3).minusDays(1);
          break;
        case YEARLY:
          this.startDate = now.withDayOfYear(1);
          this.endDate = now.withDayOfYear(now.lengthOfYear());
          break;
      }
    }
  }
}
