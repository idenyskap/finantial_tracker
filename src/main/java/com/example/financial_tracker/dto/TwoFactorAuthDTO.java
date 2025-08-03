package com.example.financial_tracker.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TwoFactorAuthDTO {
  private boolean enabled;
  private int recoveryCodesRemaining;
}
