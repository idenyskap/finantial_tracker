package com.example.financial_tracker.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "t_email_history")
public class EmailHistory {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @Column(nullable = false)
  private String recipient;

  @Column(nullable = false)
  private String subject;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private EmailType type;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private EmailStatus status;

  @Column(name = "error_message")
  private String errorMessage;

  @Column(name = "sent_at")
  private LocalDateTime sentAt;

  @Column(name = "created_at", nullable = false)
  private LocalDateTime createdAt = LocalDateTime.now();

  public enum EmailType {
    DAILY_REMINDER,
    WEEKLY_REPORT,
    MONTHLY_REPORT,
    PAYMENT_REMINDER,
    EMAIL_VERIFICATION,
    PASSWORD_RESET,
    EMAIL_CHANGE
  }

  public enum EmailStatus {
    PENDING,
    SENT,
    FAILED,
    QUOTA_EXCEEDED
  }
}
