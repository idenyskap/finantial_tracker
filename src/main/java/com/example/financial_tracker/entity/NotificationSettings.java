package com.example.financial_tracker.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@Entity
@Table(name = "t_notification_settings")
public class NotificationSettings {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false, unique = true)
  private User user;

  @Column(name = "weekly_report", nullable = false)
  private Boolean weeklyReport = false;

  @Column(name = "monthly_report", nullable = false)
  private Boolean monthlyReport = true;

  @Column(name = "payment_reminders", nullable = false)
  private Boolean paymentReminders = true;

  @Column(name = "payment_reminder_days", nullable = false)
  private Integer paymentReminderDays = 1;

  @Column(name = "daily_reminder", nullable = false)
  private Boolean dailyReminder = false;

  @Column(name = "daily_reminder_time")
  private LocalTime dailyReminderTime = LocalTime.of(21, 0);

  @Column(name = "email_enabled", nullable = false)
  private Boolean emailEnabled = true;

  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @Column(name = "updated_at", nullable = false)
  private LocalDateTime updatedAt;

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
