package com.example.financial_tracker.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenService {

  private static final int TOKEN_LENGTH = 32;
  private static final int TOKEN_EXPIRY_HOURS = 24;

  public String generateToken() {
    SecureRandom random = new SecureRandom();
    byte[] bytes = new byte[TOKEN_LENGTH];
    random.nextBytes(bytes);
    return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
  }

  public LocalDateTime getExpiryDate() {
    return LocalDateTime.now().plusHours(TOKEN_EXPIRY_HOURS);
  }

  public boolean isTokenExpired(LocalDateTime expiryDate) {
    return expiryDate != null && LocalDateTime.now().isAfter(expiryDate);
  }
}
