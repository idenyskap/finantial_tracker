package com.example.financial_tracker.controller;

import com.example.financial_tracker.dto.NotificationSettingsDTO;
import com.example.financial_tracker.entity.NotificationSettings;
import com.example.financial_tracker.entity.User;
import com.example.financial_tracker.mapper.NotificationSettingsMapper;
import com.example.financial_tracker.repository.NotificationSettingsRepository;
import com.example.financial_tracker.service.EmailQuotaService;
import com.example.financial_tracker.service.EmailService;
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
  private final NotificationSettingsRepository notificationSettingsRepository;
  private final NotificationSettingsMapper notificationSettingsMapper;
  private final EmailService emailService;
  private final EmailQuotaService emailQuotaService;

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

    if (notificationSettingsRepository.findByUser(user).isPresent()) {
      return ResponseEntity.ok(notificationService.getSettings(user));
    }

    NotificationSettings settings = new NotificationSettings();
    settings.setUser(user);
    settings.setEmailEnabled(false);
    settings.setWeeklyReport(false);
    settings.setMonthlyReport(false);
    settings.setPaymentReminders(false);
    settings.setPaymentReminderDays(1);
    settings.setDailyReminder(false);

    NotificationSettings saved = notificationSettingsRepository.save(settings);
    log.info("Created notification settings for existing user: {}", user.getEmail());

    return ResponseEntity.ok(notificationSettingsMapper.toDto(saved));
  }
}
