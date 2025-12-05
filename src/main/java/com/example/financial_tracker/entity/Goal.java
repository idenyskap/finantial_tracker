package com.example.financial_tracker.entity;

import com.example.financial_tracker.enumerations.GoalPriority;
import com.example.financial_tracker.enumerations.GoalStatus;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@EqualsAndHashCode(of = "id")
@Entity
@Table(name = "goals")
public class Goal {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, length = 100)
  private String name;

  @Column(columnDefinition = "TEXT")
  private String description;

  @Column(name = "target_amount", nullable = false, precision = 10, scale = 2)
  private BigDecimal targetAmount;

  @Column(name = "current_amount", nullable = false, precision = 10, scale = 2)
  private BigDecimal currentAmount = BigDecimal.ZERO;

  @Column(name = "target_date", nullable = false)
  private LocalDate targetDate;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "category_id")
  private Category category;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private GoalStatus status = GoalStatus.ACTIVE;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private GoalPriority priority = GoalPriority.MEDIUM;

  @Column(length = 50)
  private String color;

  @Column(length = 50)
  private String icon;

  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @Column(name = "updated_at", nullable = false)
  private LocalDateTime updatedAt;

  @Column(name = "completed_at")
  private LocalDateTime completedAt;

  @PrePersist
  protected void onCreate() {
    createdAt = LocalDateTime.now();
    updatedAt = LocalDateTime.now();
  }

  @PreUpdate
  protected void onUpdate() {
    updatedAt = LocalDateTime.now();
  }
}
