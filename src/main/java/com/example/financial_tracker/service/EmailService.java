package com.example.financial_tracker.service;

import com.example.financial_tracker.dto.MonthlyReportDTO;
import com.example.financial_tracker.dto.WeeklyReportDTO;
import com.example.financial_tracker.entity.EmailHistory;
import com.example.financial_tracker.entity.RecurringTransaction;
import com.example.financial_tracker.entity.User;
import com.example.financial_tracker.repository.EmailHistoryRepository;
import com.example.financial_tracker.repository.TransactionRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

  private final JavaMailSender mailSender;
  private final TemplateEngine templateEngine;
  private final EmailHistoryRepository emailHistoryRepository;
  private final EmailQuotaService emailQuotaService;
  private final TransactionRepository transactionRepository;

  @Value("${app.mail.from}")
  private String fromEmail;

  @Value("${app.mail.from-name}")
  private String fromName;

  @Value("${app.mail.base-url}")
  private String baseUrl;

  @Value("${spring.mail.host}")
  private String mailHost;

  @Value("${spring.mail.port}")
  private int mailPort;

  @Async
  public void sendEmail(String to, String subject, String template,
                        Map<String, Object> variables, EmailHistory.EmailType type, User user) {

    log.info("=== STARTING EMAIL SEND ===");
    log.info("Mail server: {}:{}", mailHost, mailPort);
    log.info("To: {}", to);
    log.info("Subject: {}", subject);
    log.info("Type: {}", type);
    log.info("From: {} <{}>", fromName, fromEmail);

    EmailHistory history = createEmailHistory(to, subject, type, user);

    if (!emailQuotaService.canSendEmail()) {
      log.error("Email quota exceeded");
      history.setStatus(EmailHistory.EmailStatus.QUOTA_EXCEEDED);
      emailHistoryRepository.save(history);
      return;
    }

    try {
      log.info("Creating MimeMessage...");
      MimeMessage message = mailSender.createMimeMessage();
      MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

      helper.setFrom(fromEmail, fromName);
      helper.setTo(to);
      helper.setSubject(subject);

      log.info("Processing template: {}", template);
      Context context = new Context();
      context.setVariables(variables);
      context.setVariable("baseUrl", baseUrl);

      String htmlContent = templateEngine.process("emails/" + template, context);
      log.info("Template processed, content length: {}", htmlContent.length());

      helper.setText(htmlContent, true);

      log.info("Attempting to send email via JavaMailSender...");
      mailSender.send(message);

      log.info("✅ EMAIL SENT SUCCESSFULLY!");

      emailQuotaService.incrementQuota();

      history.setStatus(EmailHistory.EmailStatus.SENT);
      history.setSentAt(LocalDateTime.now());

    } catch (MailException e) {
      log.error("❌ MailException: {}", e.getMessage(), e);
      history.setStatus(EmailHistory.EmailStatus.FAILED);
      history.setErrorMessage("Mail error: " + e.getMessage());
    } catch (MessagingException e) {
      log.error("❌ MessagingException: {}", e.getMessage(), e);
      history.setStatus(EmailHistory.EmailStatus.FAILED);
      history.setErrorMessage("Messaging error: " + e.getMessage());
    } catch (UnsupportedEncodingException e) {
      log.error("❌ UnsupportedEncodingException: {}", e.getMessage(), e);
      history.setStatus(EmailHistory.EmailStatus.FAILED);
      history.setErrorMessage("Encoding error: " + e.getMessage());
    } catch (Exception e) {
      log.error("❌ Unexpected error: {}", e.getMessage(), e);
      history.setStatus(EmailHistory.EmailStatus.FAILED);
      history.setErrorMessage("Unexpected error: " + e.getMessage());
    }

    emailHistoryRepository.save(history);
    log.info("Email history saved with status: {}", history.getStatus());
    log.info("=== EMAIL SEND COMPLETE ===");
  }

  private EmailHistory createEmailHistory(String to, String subject,
                                          EmailHistory.EmailType type, User user) {
    EmailHistory history = new EmailHistory();
    history.setUser(user);
    history.setRecipient(to);
    history.setSubject(subject);
    history.setType(type);
    history.setStatus(EmailHistory.EmailStatus.PENDING);
    return history;
  }

  public void sendPaymentReminder(User user, RecurringTransaction transaction) {
    if (!user.getNotificationSettings().getPaymentReminders()) {
      return;
    }

    Map<String, Object> variables = Map.of(
      "userName", user.getName(),
      "transactionName", transaction.getName(),
      "amount", transaction.getAmount(),
      "dueDate", transaction.getNextExecutionDate(),
      "category", transaction.getCategory().getName()
    );

    sendEmail(
      user.getEmail(),
      "Payment Reminder: " + transaction.getName(),
      "payment-reminder",
      variables,
      EmailHistory.EmailType.PAYMENT_REMINDER,
      user
    );
  }

  public void sendDailyReminder(User user) {
    if (!user.getNotificationSettings().getDailyReminder()) {
      return;
    }

    long todayTransactions = transactionRepository.countByUserAndDate(user, LocalDate.now());

    Map<String, Object> variables = Map.of(
      "userName", user.getName(),
      "todayCount", todayTransactions,
      "currentTime", LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"))
    );

    sendEmail(
      user.getEmail(),
      "Don't forget to log your expenses!",
      "daily-reminder",
      variables,
      EmailHistory.EmailType.DAILY_REMINDER,
      user
    );
  }

  public void sendWeeklyReport(User user, WeeklyReportDTO report) {
    if (!user.getNotificationSettings().getWeeklyReport()) {
      return;
    }

    Map<String, Object> variables = Map.of(
      "userName", user.getName(),
      "report", report,
      "startDate", report.getStartDate(),
      "endDate", report.getEndDate()
    );

    sendEmail(
      user.getEmail(),
      "Your Weekly Financial Summary",
      "weekly-report",
      variables,
      EmailHistory.EmailType.WEEKLY_REPORT,
      user
    );
  }

  public void sendMonthlyReport(User user, MonthlyReportDTO report) {
    if (!user.getNotificationSettings().getMonthlyReport()) {
      return;
    }

    Map<String, Object> variables = Map.of(
      "userName", user.getName(),
      "report", report,
      "month", report.getMonth(),
      "year", report.getYear()
    );

    sendEmail(
      user.getEmail(),
      "Your Monthly Financial Report - " + getMonthName(report.getMonth()) + " " + report.getYear(),
      "monthly-report",
      variables,
      EmailHistory.EmailType.MONTHLY_REPORT,
      user
    );
  }

  private String getMonthName(int month) {
    String[] months = {"January", "February", "March", "April", "May", "June",
      "July", "August", "September", "October", "November", "December"};
    return months[month - 1];
  }
}
