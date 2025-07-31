package com.example.financial_tracker.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationSettingsDTO {
  private Long id;

  private Boolean weeklyReport;
  private Boolean monthlyReport;

  private Boolean paymentReminders;

  @Min(0)
  @Max(7)
  private Integer paymentReminderDays;

  private Boolean dailyReminder;
  private String dailyReminderTime;

  private Boolean emailEnabled;
}
