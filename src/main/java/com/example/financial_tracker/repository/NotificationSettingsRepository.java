package com.example.financial_tracker.repository;

import com.example.financial_tracker.entity.NotificationSettings;
import com.example.financial_tracker.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface NotificationSettingsRepository extends JpaRepository<NotificationSettings, Long> {
  Optional<NotificationSettings> findByUser(User user);
}
