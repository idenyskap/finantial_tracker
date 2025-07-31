package com.example.financial_tracker.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {
  private String message;
  private String error;
  private int status;
  private LocalDateTime timestamp;
  private String path;
  private List<String> details;
}
