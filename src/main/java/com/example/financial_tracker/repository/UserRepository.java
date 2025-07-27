package com.example.financial_tracker.repository;

import com.example.financial_tracker.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

  Optional<User> findByEmail(String email);

  Optional<User> findByVerificationToken(String token);
  Optional<User> findByResetPasswordToken(String token);
  Optional<User> findByNewEmailToken(String token);

  @Query("SELECT u FROM User u LEFT JOIN FETCH u.notificationSettings WHERE u.id = :id")
  Optional<User> findByIdWithNotificationSettings(@Param("id") Long id);

  @Query("SELECT u FROM User u JOIN u.notificationSettings ns WHERE ns.paymentReminders = true AND ns.emailEnabled = true")
  List<User> findUsersWithPaymentReminders();

  @Query("SELECT u FROM User u JOIN u.notificationSettings ns WHERE ns.dailyReminder = true AND ns.emailEnabled = true AND ns.dailyReminderTime = :time")
  List<User> findUsersWithDailyReminderAt(@Param("time") LocalTime time);

  @Query("SELECT u FROM User u JOIN u.notificationSettings ns WHERE ns.weeklyReport = true AND ns.emailEnabled = true")
  List<User> findUsersWithWeeklyReports();

  @Query("SELECT u FROM User u JOIN u.notificationSettings ns WHERE ns.monthlyReport = true AND ns.emailEnabled = true")
  List<User> findUsersWithMonthlyReports();
}
