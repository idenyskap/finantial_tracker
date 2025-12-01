package com.example.financial_tracker.controller;

import com.example.financial_tracker.dto.*;
import com.example.financial_tracker.entity.User;
import com.example.financial_tracker.service.TwoFactorAuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Set;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth/2fa")
@RequiredArgsConstructor
public class TwoFactorAuthController {

  private final TwoFactorAuthService twoFactorAuthService;

  @GetMapping("/status")
  public ResponseEntity<TwoFactorAuthDTO> getTwoFactorStatus(@AuthenticationPrincipal User user) {
    return ResponseEntity.ok(twoFactorAuthService.getTwoFactorStatus(user));
  }

  @PostMapping("/setup")
  public ResponseEntity<TwoFactorSetupDTO> setupTwoFactorAuth(@AuthenticationPrincipal User user) {
    log.info("User {} initiating 2FA setup", user.getEmail());
    TwoFactorSetupDTO setup = twoFactorAuthService.setupTwoFactorAuth(user);
    return ResponseEntity.ok(setup);
  }

  @PostMapping("/enable")
  public ResponseEntity<Map<String, String>> enableTwoFactorAuth(
    @AuthenticationPrincipal User user,
    @Valid @RequestBody Enable2FARequest request) {

    log.info("User {} enabling 2FA", user.getEmail());

    twoFactorAuthService.enableTwoFactorAuth(
      user,
      request.getSecret(),
      request.getVerificationCode(),
      request.getRecoveryCodes()
    );

    return ResponseEntity.ok(Map.of(
      "message", "Two-factor authentication enabled successfully"
    ));
  }

  @PostMapping("/disable")
  public ResponseEntity<Map<String, String>> disableTwoFactorAuth(
    @AuthenticationPrincipal User user,
    @Valid @RequestBody PasswordConfirmRequest request) {

    log.info("User {} disabling 2FA", user.getEmail());

    twoFactorAuthService.disableTwoFactorAuth(user, request.getPassword());

    return ResponseEntity.ok(Map.of(
      "message", "Two-factor authentication disabled successfully"
    ));
  }

  @PostMapping("/regenerate-codes")
  public ResponseEntity<Map<String, Object>> regenerateRecoveryCodes(
    @AuthenticationPrincipal User user,
    @Valid @RequestBody PasswordConfirmRequest request) {

    log.info("User {} regenerating recovery codes", user.getEmail());

    Set<String> newCodes = twoFactorAuthService.regenerateRecoveryCodes(user, request.getPassword());

    return ResponseEntity.ok(Map.of(
      "recoveryCodes", newCodes,
      "message", "Recovery codes regenerated successfully"
    ));
  }
}
