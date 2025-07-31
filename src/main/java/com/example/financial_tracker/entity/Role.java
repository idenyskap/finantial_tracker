package com.example.financial_tracker.entity;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

@RequiredArgsConstructor
public enum Role implements GrantedAuthority {
  USER,
  ADMIN;

  @Override
  public String getAuthority() {
    return name();
  }
}
