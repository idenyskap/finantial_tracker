//package com.example.financial_tracker.controller;
//
//import com.example.financial_tracker.dto.MonthlyReportDTO;
//import com.example.financial_tracker.dto.WeeklyReportDTO;
//import com.example.financial_tracker.entity.*;
//import com.example.financial_tracker.repository.*;
//import com.example.financial_tracker.service.EmailService;
//import com.example.financial_tracker.service.ReportService;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.context.annotation.Profile;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.core.annotation.AuthenticationPrincipal;
//import org.springframework.web.bind.annotation.*;
//
//import java.time.LocalDate;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.stream.Collectors;
//
//@RestController
//@RequestMapping("/api/test/email")
//@RequiredArgsConstructor
//@Slf4j
//@Profile("dev")
//public class TestEmailController {
//
//  private final EmailService emailService;
//  private final NotificationSettingsRepository notificationSettingsRepository;
//  private final TransactionRepository transactionRepository;
//  private final RecurringTransactionRepository recurringTransactionRepository;
//  private final EmailHistoryRepository emailHistoryRepository;
//  private final ReportService reportService;
//
//  @GetMapping("/status")
//  public ResponseEntity<Map<String, Object>> checkEmailStatus(
//    @AuthenticationPrincipal User user) {
//
//    NotificationSettings settings = notificationSettingsRepository
//      .findByUser(user).orElse(null);
//
//    return ResponseEntity.ok(Map.of(
//      "userEmail", user.getEmail(),
//      "hasSettings", settings != null,
//      "settingsDetails", settings != null ? Map.of(
//        "emailEnabled", settings.getEmailEnabled(),
//        "weeklyReport", settings.getWeeklyReport(),
//        "monthlyReport", settings.getMonthlyReport(),
//        "paymentReminders", settings.getPaymentReminders(),
//        "dailyReminder", settings.getDailyReminder()
//      ) : "No settings found",
//      "mailhogUrl", "http://localhost:8025"
//    ));
//  }
//
//  @PostMapping("/send-daily-reminder")
//  public ResponseEntity<Map<String, Object>> sendDailyReminder(
//    @AuthenticationPrincipal User user) {
//    try {
//      ensureNotificationSettings(user);
//
//      log.info("Sending test daily reminder to user: {}", user.getEmail());
//      emailService.sendDailyReminder(user);
//
//      return ResponseEntity.ok(Map.of(
//        "status", "success",
//        "message", "Daily reminder sent to " + user.getEmail(),
//        "type", "DAILY_REMINDER",
//        "checkMailhog", "http://localhost:8025"
//      ));
//    } catch (Exception e) {
//      log.error("Error sending daily reminder", e);
//      return ResponseEntity.status(500).body(Map.of(
//        "status", "error",
//        "message", e.getMessage()
//      ));
//    }
//  }
//
//  @PostMapping("/send-weekly-report")
//  public ResponseEntity<Map<String, Object>> sendWeeklyReport(
//    @AuthenticationPrincipal User user) {
//    try {
//      ensureNotificationSettings(user);
//
//      WeeklyReportDTO report = reportService.generateWeeklyReport(user);
//
//      log.info("Sending test weekly report to user: {}", user.getEmail());
//      emailService.sendWeeklyReport(user, report);
//
//      return ResponseEntity.ok(Map.of(
//        "status", "success",
//        "message", "Weekly report sent to " + user.getEmail(),
//        "type", "WEEKLY_REPORT",
//        "reportData", Map.of(
//          "startDate", report.getStartDate().toString(),
//          "endDate", report.getEndDate().toString(),
//          "totalIncome", report.getTotalIncome(),
//          "totalExpenses", report.getTotalExpenses(),
//          "netAmount", report.getNetAmount(),
//          "transactions", report.getTotalTransactions()
//        ),
//        "checkMailhog", "http://localhost:8025"
//      ));
//    } catch (Exception e) {
//      log.error("Error sending weekly report", e);
//      return ResponseEntity.status(500).body(Map.of(
//        "status", "error",
//        "message", e.getMessage()
//      ));
//    }
//  }
//
//  @PostMapping("/send-monthly-report")
//  public ResponseEntity<Map<String, Object>> sendMonthlyReport(
//    @AuthenticationPrincipal User user,
//    @RequestParam(required = false) Integer month,
//    @RequestParam(required = false) Integer year) {
//    try {
//      ensureNotificationSettings(user);
//
//      if (month == null || year == null) {
//        LocalDate lastMonth = LocalDate.now().minusMonths(1);
//        month = lastMonth.getMonthValue();
//        year = lastMonth.getYear();
//      }
//
//      LocalDate requestedDate = LocalDate.of(year, month, 1);
//      if (requestedDate.isAfter(LocalDate.now())) {
//        return ResponseEntity.badRequest().body(Map.of(
//          "status", "error",
//          "message", "Cannot generate report for future periods",
//          "hint", "Please select a past month"
//        ));
//      }
//
//      MonthlyReportDTO report = reportService.generateMonthlyReport(user, month, year);
//
//      log.info("Sending test monthly report for {}/{} to user: {}",
//        month, year, user.getEmail());
//      emailService.sendMonthlyReport(user, report);
//
//      return ResponseEntity.ok(Map.of(
//        "status", "success",
//        "message", String.format("Monthly report for %d/%d sent to %s",
//          month, year, user.getEmail()),
//        "type", "MONTHLY_REPORT",
//        "reportData", Map.of(
//          "month", report.getMonth(),
//          "year", report.getYear(),
//          "totalIncome", report.getTotalIncome(),
//          "totalExpenses", report.getTotalExpenses(),
//          "netAmount", report.getNetAmount(),
//          "savingsRate", report.getSavingsRate(),
//          "transactions", report.getTotalTransactions()
//        ),
//        "checkMailhog", "http://localhost:8025"
//      ));
//    } catch (Exception e) {
//      log.error("Error sending monthly report", e);
//      return ResponseEntity.status(500).body(Map.of(
//        "status", "error",
//        "message", e.getMessage()
//      ));
//    }
//  }
//
//  @PostMapping("/send-payment-reminder")
//  public ResponseEntity<Map<String, Object>> sendPaymentReminder(
//    @AuthenticationPrincipal User user) {
//    try {
//      ensureNotificationSettings(user);
//
//      List<RecurringTransaction> recurringTransactions =
//        recurringTransactionRepository.findByUserAndActiveTrue(user);
//
//      if (recurringTransactions.isEmpty()) {
//        return ResponseEntity.ok(Map.of(
//          "status", "warning",
//          "message", "No active recurring transactions found. Create one first.",
//          "hint", "Go to Recurring Transactions and create a recurring payment"
//        ));
//      }
//
//      RecurringTransaction transaction = recurringTransactions.get(0);
//
//      log.info("Sending test payment reminder to user: {}", user.getEmail());
//      emailService.sendPaymentReminder(user, transaction);
//
//      return ResponseEntity.ok(Map.of(
//        "status", "success",
//        "message", "Payment reminder sent to " + user.getEmail(),
//        "type", "PAYMENT_REMINDER",
//        "paymentData", Map.of(
//          "name", transaction.getName(),
//          "amount", transaction.getAmount(),
//          "nextDate", transaction.getNextExecutionDate().toString(),
//          "category", transaction.getCategory().getName()
//        ),
//        "checkMailhog", "http://localhost:8025"
//      ));
//    } catch (Exception e) {
//      log.error("Error sending payment reminder", e);
//      return ResponseEntity.status(500).body(Map.of(
//        "status", "error",
//        "message", e.getMessage()
//      ));
//    }
//  }
//
//  @PostMapping("/send-all-test-emails")
//  public ResponseEntity<Map<String, Object>> sendAllTestEmails(
//    @AuthenticationPrincipal User user) {
//    Map<String, String> results = new HashMap<>();
//
//    try {
//      emailService.sendDailyReminder(user);
//      results.put("dailyReminder", "✅ Sent");
//    } catch (Exception e) {
//      results.put("dailyReminder", "❌ Failed: " + e.getMessage());
//    }
//
//    try {
//      WeeklyReportDTO weeklyReport = reportService.generateWeeklyReport(user);
//      emailService.sendWeeklyReport(user, weeklyReport);
//      results.put("weeklyReport", "✅ Sent");
//    } catch (Exception e) {
//      results.put("weeklyReport", "❌ Failed: " + e.getMessage());
//    }
//
//    try {
//      LocalDate lastMonth = LocalDate.now().minusMonths(1);
//      MonthlyReportDTO monthlyReport = reportService.generateMonthlyReport(
//        user, lastMonth.getMonthValue(), lastMonth.getYear()
//      );
//      emailService.sendMonthlyReport(user, monthlyReport);
//      results.put("monthlyReport", "✅ Sent");
//    } catch (Exception e) {
//      results.put("monthlyReport", "❌ Failed: " + e.getMessage());
//    }
//
//    try {
//      List<RecurringTransaction> transactions =
//        recurringTransactionRepository.findByUserAndActiveTrue(user);
//      if (!transactions.isEmpty()) {
//        emailService.sendPaymentReminder(user, transactions.get(0));
//        results.put("paymentReminder", "✅ Sent");
//      } else {
//        results.put("paymentReminder", "⚠️ No recurring transactions");
//      }
//    } catch (Exception e) {
//      results.put("paymentReminder", "❌ Failed: " + e.getMessage());
//    }
//
//    return ResponseEntity.ok(Map.of(
//      "status", "completed",
//      "results", results,
//      "checkMailhog", "http://localhost:8025"
//    ));
//  }
//
//  @GetMapping("/history")
//  public ResponseEntity<List<Map<String, Object>>> getEmailHistory(
//    @AuthenticationPrincipal User user) {
//
//    List<EmailHistory> history = emailHistoryRepository
//      .findByUserOrderByCreatedAtDesc(user);
//
//    List<Map<String, Object>> result = history.stream()
//      .limit(20)
//      .map(email -> {
//        Map<String, Object> map = new HashMap<>();
//        map.put("id", email.getId());
//        map.put("recipient", email.getRecipient());
//        map.put("subject", email.getSubject());
//        map.put("type", email.getType().toString());
//        map.put("status", email.getStatus().toString());
//        map.put("error", email.getErrorMessage() != null ? email.getErrorMessage() : "");
//        map.put("sentAt", email.getSentAt() != null ? email.getSentAt().toString() : "");
//        map.put("createdAt", email.getCreatedAt().toString());
//        return map;
//      })
//      .collect(Collectors.toList());
//
//    return ResponseEntity.ok(result);
//  }
//
//  @GetMapping("/check-schedulers")
//  public ResponseEntity<Map<String, Object>> checkSchedulers() {
//    LocalDate today = LocalDate.now();
//
//    return ResponseEntity.ok(Map.of(
//      "currentDate", today.toString(),
//      "currentDayOfWeek", today.getDayOfWeek().toString(),
//      "currentDayOfMonth", today.getDayOfMonth(),
//      "isFirstDayOfMonth", today.getDayOfMonth() == 1,
//      "isMonday", today.getDayOfWeek().toString().equals("MONDAY"),
//      "scheduledTasks", Map.of(
//        "dailyReminder", "Every hour (checks for users with matching time)",
//        "paymentReminders", "Daily at 10:00",
//        "weeklyReports", "Every Monday at 8:00",
//        "monthlyReports", "First day of month at 8:00"
//      )
//    ));
//  }
//
//  private void ensureNotificationSettings(User user) {
//    if (notificationSettingsRepository.findByUser(user).isEmpty()) {
//      NotificationSettings settings = new NotificationSettings();
//      settings.setUser(user);
//      settings.setEmailEnabled(true);
//      settings.setWeeklyReport(true);
//      settings.setMonthlyReport(true);
//      settings.setPaymentReminders(true);
//      settings.setDailyReminder(true);
//      notificationSettingsRepository.save(settings);
//      log.info("Created notification settings for user in test");
//    }
//  }
//}
