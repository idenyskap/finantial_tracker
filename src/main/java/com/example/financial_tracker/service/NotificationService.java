package com.example.financial_tracker.service;

import com.example.financial_tracker.dto.NotificationSettingsDTO;
import com.example.financial_tracker.entity.NotificationSettings;
import com.example.financial_tracker.entity.User;
import com.example.financial_tracker.mapper.NotificationSettingsMapper;
import com.example.financial_tracker.repository.NotificationSettingsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class NotificationService {

  private final NotificationSettingsRepository notificationSettingsRepository;
  private final NotificationSettingsMapper notificationSettingsMapper;

  @Transactional(readOnly = true)
  public NotificationSettingsDTO getSettings(User user) {
    log.info("Fetching notification settings for user: {}", user.getEmail());

    NotificationSettings settings = notificationSettingsRepository
      .findByUser(user)
      .orElseGet(() -> createDefaultSettings(user));

    return notificationSettingsMapper.toDto(settings);
  }

  public NotificationSettingsDTO updateSettings(User user, NotificationSettingsDTO dto) {
    log.info("Updating notification settings for user: {}", user.getEmail());

    NotificationSettings settings = notificationSettingsRepository
      .findByUser(user)
      .orElseGet(() -> createDefaultSettings(user));

    settings.setWeeklyReport(dto.getWeeklyReport());
    settings.setMonthlyReport(dto.getMonthlyReport());
    settings.setPaymentReminders(dto.getPaymentReminders());
    settings.setPaymentReminderDays(dto.getPaymentReminderDays());
    settings.setDailyReminder(dto.getDailyReminder());

    if (dto.getDailyReminderTime() != null) {
      settings.setDailyReminderTime(LocalTime.parse(dto.getDailyReminderTime()));
    }

    settings.setEmailEnabled(dto.getEmailEnabled());

    NotificationSettings saved = notificationSettingsRepository.save(settings);

    log.info("Updated notification settings for user: {}", user.getEmail());
    return notificationSettingsMapper.toDto(saved);
  }

  private NotificationSettings createDefaultSettings(User user) {
    log.info("Creating default notification settings for user: {}", user.getEmail());

    NotificationSettings settings = new NotificationSettings();
    settings.setUser(user);

    return notificationSettingsRepository.save(settings);
  }

  public NotificationSettingsDTO initializeSettings(User user) {
    log.info("Initializing notification settings for user: {}", user.getEmail());

    return notificationSettingsRepository.findByUser(user)
      .map(notificationSettingsMapper::toDto)
      .orElseGet(() -> {
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

        return notificationSettingsMapper.toDto(saved);
      });
  }
}
