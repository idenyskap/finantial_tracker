package com.example.financial_tracker.controller;

import com.example.financial_tracker.dto.AuthRequest;
import com.example.financial_tracker.dto.AuthResponse;
import com.example.financial_tracker.dto.EmailRequest;
import com.example.financial_tracker.dto.PasswordResetRequest;
import com.example.financial_tracker.service.AuthService;
import com.example.financial_tracker.service.CookieService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Validated
public class AuthController {

  private final AuthService authService;
  private final CookieService cookieService;

  @PostMapping("/register")
  public ResponseEntity<AuthResponse> register(
          @Valid @RequestBody AuthRequest request,
          HttpServletResponse response) {
      AuthResponse authResponse = authService.register(request);

      if (authResponse.getToken() != null) {
          cookieService.setAuthCookie(response, authResponse.getToken());
      }

      return ResponseEntity.ok(authResponse);
  }

  @PostMapping("/login")
  public ResponseEntity<AuthResponse> login(
          @Valid @RequestBody AuthRequest request,
          HttpServletResponse response) {

      AuthResponse authResponse = authService.login(request);

      if (authResponse.getToken() != null && !authResponse.isRequiresTwoFactor()) {
          cookieService.setAuthCookie(response, authResponse.getToken());
      }

    return ResponseEntity.ok(authResponse);
  }

  @PostMapping("/logout")
  public ResponseEntity<Map<String, String>> logout(HttpServletResponse response) {
      cookieService.clearAuthCookie(response);
      return ResponseEntity.ok(Map.of("message", "Logged out successfully"));
  }

  @PostMapping("/confirm-email-change")
  public ResponseEntity<Map<String, String>> confirmEmailChange(@RequestParam String token) {
    authService.confirmEmailChange(token);
    return ResponseEntity.ok(Map.of("message", "Email changed successfully"));
  }

  @PostMapping("/request-password-reset")
  public ResponseEntity<Map<String, String>> requestPasswordReset(
          @Valid @RequestBody EmailRequest request) {
    authService.requestPasswordReset(request.getEmail());
    return ResponseEntity.ok(Map.of(
      "message", "If an account exists with this email, a password reset link has been sent."
    ));
  }

  @PostMapping("/reset-password")
  public ResponseEntity<Map<String, String>> resetPassword(
          @Valid @RequestBody PasswordResetRequest request) {
    authService.resetPassword(request.getToken(), request.getNewPassword());
    return ResponseEntity.ok(Map.of("message", "Password has been reset successfully"));
  }

  @PostMapping("/verify-email")
  public ResponseEntity<Map<String, String>> verifyEmail(@RequestParam String token) {
    authService.verifyEmail(token);
    return ResponseEntity.ok(Map.of("message", "Email verified successfully"));
  }

  @PostMapping("/resend-verification")
  public ResponseEntity<Map<String, String>> resendVerificationEmail(
          @Valid @RequestBody EmailRequest request) {
    authService.resendVerificationEmail(request.getEmail());
    return ResponseEntity.ok(Map.of("message", "Verification email sent"));
  }
}
