package com.example.financial_tracker.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserDTO {
  private Long id;
  private String name;
  private String email;
  private boolean emailVerified;
  private String newEmail;
  private LocalDateTime createdAt;
}
