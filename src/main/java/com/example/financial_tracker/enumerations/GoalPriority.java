package com.example.financial_tracker.enumerations;

public enum GoalPriority {
  LOW("Low"),
  MEDIUM("Medium"),
  HIGH("High"),
  CRITICAL("Critical");

  private final String displayName;

  GoalPriority(String displayName) {
    this.displayName = displayName;
  }

  public String getDisplayName() {
    return displayName;
  }
}
