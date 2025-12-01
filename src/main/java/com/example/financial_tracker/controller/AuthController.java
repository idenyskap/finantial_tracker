package com.example.financial_tracker.controller;

import com.example.financial_tracker.dto.AuthRequest;
import com.example.financial_tracker.dto.AuthResponse;
import com.example.financial_tracker.service.AuthService;
import com.example.financial_tracker.service.UserService;
import jakarta.servlet.http.Cookie;
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
  private final UserService userService;

  private static final int COOKIE_MAX_AGE = 86400;
  private static final String COOKIE_PATH = "/";

  @PostMapping("/register")
  public ResponseEntity<AuthResponse> register(
          @Valid @RequestBody AuthRequest request,
          HttpServletResponse response) {
      AuthResponse authResponse = authService.register(request);

      if (authResponse.getToken() != null) {
          addTokenCookie(response, authResponse.getToken());
          authResponse.setToken(null);
      }

      return ResponseEntity.ok(authResponse);
  }

  @PostMapping("/login")
  public ResponseEntity<AuthResponse> login(
          @Valid @RequestBody AuthRequest request,
          HttpServletResponse response) {

      AuthResponse authResponse = authService.login(request);

      if (authResponse.getToken() != null && !authResponse.isRequiresTwoFactor()) {
          addTokenCookie(response, authResponse.getToken());
          authResponse.setToken(null);
      }

    return ResponseEntity.ok(authResponse);
  }

  @PostMapping("/logout")
  public ResponseEntity<?> logout(HttpServletResponse response) {
      removeTokenCookie(response);
      return ResponseEntity.ok(Map.of("message", "Logged out successfully"));
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

  private void addTokenCookie(HttpServletResponse response, String token) {
      Cookie cookie = new Cookie("token", token);
      cookie.setHttpOnly(true);
      cookie.setSecure(false);
      cookie.setPath(COOKIE_PATH);
      cookie.setMaxAge(COOKIE_MAX_AGE);
      response.addCookie(cookie);
  }

  private void removeTokenCookie(HttpServletResponse response) {
      Cookie cookie = new Cookie("token", null);
      cookie.setHttpOnly(true);
      cookie.setSecure(false);
      cookie.setPath(COOKIE_PATH);
      cookie.setMaxAge(0);
      response.addCookie(cookie);
  }

}
