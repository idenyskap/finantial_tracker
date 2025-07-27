package com.example.financial_tracker.controller;

import com.example.financial_tracker.dto.AuthRequest;
import com.example.financial_tracker.dto.AuthResponse;
import com.example.financial_tracker.entity.User;
import com.example.financial_tracker.service.AuthService;
import com.example.financial_tracker.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
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

}
