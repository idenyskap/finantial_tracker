package com.example.financial_tracker.controller;

import com.example.financial_tracker.dto.*;
import com.example.financial_tracker.entity.User;
import com.example.financial_tracker.service.TwoFactorAuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Set;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth/2fa")
@RequiredArgsConstructor
public class TwoFactorAuthController {

  private final TwoFactorAuthService twoFactorAuthService;
  private final PasswordEncoder passwordEncoder;

  @GetMapping("/status")
  public ResponseEntity<TwoFactorAuthDTO> getTwoFactorStatus(@AuthenticationPrincipal User user) {
    return ResponseEntity.ok(twoFactorAuthService.getTwoFactorStatus(user));
  }

  @PostMapping("/setup")
  public ResponseEntity<TwoFactorSetupDTO> setupTwoFactorAuth(@AuthenticationPrincipal User user) {
    log.info("User {} initiating 2FA setup", user.getEmail());

    if (user.isTwoFactorEnabled()) {
      return ResponseEntity.badRequest().build();
    }

    TwoFactorSetupDTO setup = twoFactorAuthService.setupTwoFactorAuth(user);
    return ResponseEntity.ok(setup);
  }

  @PostMapping("/enable")
  public ResponseEntity<?> enableTwoFactorAuth(
    @AuthenticationPrincipal User user,
    @Valid @RequestBody Enable2FARequest request) {

    log.info("User {} enabling 2FA", user.getEmail());

    try {
      twoFactorAuthService.enableTwoFactorAuth(
        user,
        request.getSecret(),
        request.getVerificationCode(),
        request.getRecoveryCodes()
      );

      return ResponseEntity.ok(Map.of(
        "message", "Two-factor authentication enabled successfully"
      ));
    } catch (Exception e) {
      return ResponseEntity.badRequest().body(Map.of(
        "error", e.getMessage()
      ));
    }
  }

  @PostMapping("/disable")
  public ResponseEntity<?> disableTwoFactorAuth(
    @AuthenticationPrincipal User user,
    @RequestBody Map<String, String> request) {

    String password = request.get("password");

    if (!passwordEncoder.matches(password, user.getPassword())) {
      return ResponseEntity.badRequest().body(Map.of(
        "error", "Invalid password"
      ));
    }

    twoFactorAuthService.disableTwoFactorAuth(user, password);

    return ResponseEntity.ok(Map.of(
      "message", "Two-factor authentication disabled successfully"
    ));
  }

  @PostMapping("/regenerate-codes")
  public ResponseEntity<?> regenerateRecoveryCodes(
    @AuthenticationPrincipal User user,
    @RequestBody Map<String, String> request) {

    String password = request.get("password");

    if (!passwordEncoder.matches(password, user.getPassword())) {
      return ResponseEntity.badRequest().body(Map.of(
        "error", "Invalid password"
      ));
    }

    Set<String> newCodes = twoFactorAuthService.regenerateRecoveryCodes(user, password);

    return ResponseEntity.ok(Map.of(
      "recoveryCodes", newCodes,
      "message", "Recovery codes regenerated successfully"
    ));
  }
}
