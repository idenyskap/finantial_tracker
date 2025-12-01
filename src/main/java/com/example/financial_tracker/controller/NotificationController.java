package com.example.financial_tracker.controller;

import com.example.financial_tracker.dto.NotificationSettingsDTO;
import com.example.financial_tracker.entity.User;
import com.example.financial_tracker.service.NotificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
@Slf4j
public class NotificationController {

  private final NotificationService notificationService;

  @GetMapping("/settings")
  public ResponseEntity<NotificationSettingsDTO> getSettings(
    @AuthenticationPrincipal User user) {
    NotificationSettingsDTO settings = notificationService.getSettings(user);
    return ResponseEntity.ok(settings);
  }

  @PutMapping("/settings")
  public ResponseEntity<NotificationSettingsDTO> updateSettings(
    @AuthenticationPrincipal User user,
    @Valid @RequestBody NotificationSettingsDTO dto) {
    NotificationSettingsDTO updated = notificationService.updateSettings(user, dto);
    return ResponseEntity.ok(updated);
  }

  @PostMapping("/settings/init")
  public ResponseEntity<NotificationSettingsDTO> initializeSettings(
    @AuthenticationPrincipal User user) {
    NotificationSettingsDTO settings = notificationService.initializeSettings(user);
    return ResponseEntity.ok(settings);
  }
}
