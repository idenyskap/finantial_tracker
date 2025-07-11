package com.example.financial_tracker.entity;

public enum RecurrenceFrequency {
  DAILY("Daily"),
  WEEKLY("Weekly"),
  BIWEEKLY("Bi-weekly"),
  MONTHLY("Monthly"),
  QUARTERLY("Quarterly"),
  YEARLY("Yearly");

  private final String displayName;

  RecurrenceFrequency(String displayName) {
    this.displayName = displayName;
  }

  public String getDisplayName() {
    return displayName;
  }
}
