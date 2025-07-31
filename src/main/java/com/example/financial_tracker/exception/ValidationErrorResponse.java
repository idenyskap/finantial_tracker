package com.example.financial_tracker.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ValidationErrorResponse {
  private String message;
  private String error;
  private int status;
  private LocalDateTime timestamp;
  private String path;
  private Map<String, String> fieldErrors;
}
