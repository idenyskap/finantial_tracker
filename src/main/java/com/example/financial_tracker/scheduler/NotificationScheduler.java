package com.example.financial_tracker.scheduler;

import com.example.financial_tracker.dto.MonthlyReportDTO;
import com.example.financial_tracker.dto.WeeklyReportDTO;
import com.example.financial_tracker.entity.RecurringTransaction;
import com.example.financial_tracker.entity.User;
import com.example.financial_tracker.repository.RecurringTransactionRepository;
import com.example.financial_tracker.repository.UserRepository;
import com.example.financial_tracker.service.EmailService;
import com.example.financial_tracker.service.ReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationScheduler {

  private final RecurringTransactionRepository recurringTransactionRepository;
  private final UserRepository userRepository;
  private final EmailService emailService;
  private final ReportService reportService;

  @Scheduled(cron = "0 0 10 * * *")
  public void sendPaymentReminders() {
    log.info("Starting payment reminders job");

    List<User> users = userRepository.findUsersWithPaymentReminders();

    for (User user : users) {
      int daysAhead = user.getNotificationSettings().getPaymentReminderDays();
      LocalDate checkDate = LocalDate.now().plusDays(daysAhead);

      List<RecurringTransaction> upcomingPayments =
        recurringTransactionRepository.findUpcomingPayments(user, checkDate);

      for (RecurringTransaction payment : upcomingPayments) {
        emailService.sendPaymentReminder(user, payment);
      }
    }

    log.info("Payment reminders job completed");
  }

  @Scheduled(cron = "0 0 * * * *")
  public void sendDailyReminders() {
    LocalTime currentHour = LocalTime.now().withMinute(0).withSecond(0);

    List<User> users = userRepository.findUsersWithDailyReminderAt(currentHour);

    for (User user : users) {
      emailService.sendDailyReminder(user);
    }

    log.info("Sent {} daily reminders", users.size());
  }

  @Scheduled(cron = "0 0 8 * * MON")
  public void sendWeeklyReports() {
    log.info("Starting weekly reports job");

    List<User> users = userRepository.findUsersWithWeeklyReports();

    for (User user : users) {
      try {
        WeeklyReportDTO report = reportService.generateWeeklyReport(user);
        emailService.sendWeeklyReport(user, report);
      } catch (Exception e) {
        log.error("Failed to send weekly report to user: {}", user.getEmail(), e);
      }
    }

    log.info("Weekly reports job completed");
  }

  @Scheduled(cron = "0 0 8 1 * *")
  public void sendMonthlyReports() {
    log.info("Starting monthly reports job");

    List<User> users = userRepository.findUsersWithMonthlyReports();
    LocalDate lastMonth = LocalDate.now().minusMonths(1);

    for (User user : users) {
      try {
        MonthlyReportDTO report = reportService.generateMonthlyReport(
          user,
          lastMonth.getMonthValue(),
          lastMonth.getYear()
        );
        emailService.sendMonthlyReport(user, report);
      } catch (Exception e) {
        log.error("Failed to send monthly report to user: {}", user.getEmail(), e);
      }
    }

    log.info("Monthly reports job completed");
  }
}
