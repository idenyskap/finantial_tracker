package com.example.financial_tracker.controller;

import com.example.financial_tracker.dto.AuthRequest;
import com.example.financial_tracker.dto.AuthResponse;
import com.example.financial_tracker.service.AuthService;
import com.example.financial_tracker.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Validated
public class AuthController {

  private final AuthService authService;
  private final UserService userService;

  @PostMapping("/register")
  public ResponseEntity<AuthResponse> register(@Valid @RequestBody AuthRequest request) {
    return ResponseEntity.ok(authService.register(request));
  }

  @PostMapping("/login")
  public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest request) {
    return ResponseEntity.ok(authService.login(request));
  }

  @PostMapping("/confirm-email-change")
  public ResponseEntity<?> confirmEmailChange(@RequestParam String token) {
    try {
      authService.confirmEmailChange(token);
      return ResponseEntity.ok(Map.of(
        "message", "Email changed successfully"
      ));
    } catch (IllegalArgumentException e) {
      return ResponseEntity.badRequest().body(Map.of(
        "error", e.getMessage()
      ));
    }
  }

  @PostMapping("/request-password-reset")
  public ResponseEntity<?> requestPasswordReset(@RequestBody Map<String, String> request) {
    String email = request.get("email");

    if (email == null || email.trim().isEmpty()) {
      return ResponseEntity.badRequest().body(Map.of(
        "error", "Email is required"
      ));
    }

    try {
      authService.requestPasswordReset(email);
      return ResponseEntity.ok(Map.of(
        "message", "If an account exists with this email, a password reset link has been sent."
      ));
    } catch (Exception e) {
      return ResponseEntity.ok(Map.of(
        "message", "If an account exists with this email, a password reset link has been sent."
      ));
    }
  }

  @PostMapping("/reset-password")
  public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> request) {
    String token = request.get("token");
    String newPassword = request.get("newPassword");

    if (token == null || token.trim().isEmpty()) {
      return ResponseEntity.badRequest().body(Map.of(
        "error", "Reset token is required"
      ));
    }

    if (newPassword == null || newPassword.trim().isEmpty()) {
      return ResponseEntity.badRequest().body(Map.of(
        "error", "New password is required"
      ));
    }

    if (newPassword.length() < 6) {
      return ResponseEntity.badRequest().body(Map.of(
        "error", "Password must be at least 6 characters long"
      ));
    }

    try {
      authService.resetPassword(token, newPassword);
      return ResponseEntity.ok(Map.of(
        "message", "Password has been reset successfully"
      ));
    } catch (IllegalArgumentException e) {
      return ResponseEntity.badRequest().body(Map.of(
        "error", e.getMessage()
      ));
    }
  }

  @PostMapping("/verify-email")
  public ResponseEntity<?> verifyEmail(@RequestParam String token) {
    try {
      authService.verifyEmail(token);
      return ResponseEntity.ok(Map.of(
        "message", "Email verified successfully"
      ));
    } catch (IllegalArgumentException e) {
      return ResponseEntity.badRequest().body(Map.of(
        "error", e.getMessage()
      ));
    }
  }

  @PostMapping("/resend-verification")
  public ResponseEntity<?> resendVerificationEmail(@RequestBody Map<String, String> request) {
    String email = request.get("email");

    if (email == null || email.trim().isEmpty()) {
      return ResponseEntity.badRequest().body(Map.of(
        "error", "Email is required"
      ));
    }

    try {
      authService.resendVerificationEmail(email);
      return ResponseEntity.ok(Map.of(
        "message", "Verification email sent"
      ));
    } catch (IllegalArgumentException e) {
      return ResponseEntity.badRequest().body(Map.of(
        "error", e.getMessage()
      ));
    }
  }

}
