package com.example.financial_tracker.service;

import com.example.financial_tracker.entity.EmailQuota;
import com.example.financial_tracker.repository.EmailQuotaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class EmailQuotaService {

  private final EmailQuotaRepository emailQuotaRepository;

  @Value("${app.mail.quota.enabled}")
  private boolean quotaEnabled;

  @Value("${app.mail.quota.daily-limit}")
  private int dailyLimit;

  @Value("${app.mail.quota.monthly-limit}")
  private int monthlyLimit;

  @Value("${app.mail.quota.hourly-limit}")
  private int hourlyLimit;

  public synchronized boolean canSendEmail() {
    if (!quotaEnabled) {
      return true;
    }

    LocalDate today = LocalDate.now();
    EmailQuota quota = getCurrentQuota(today);

    if (quota.getDailyCount() >= dailyLimit) {
      log.warn("Daily email limit reached: {}/{}", quota.getDailyCount(), dailyLimit);
      return false;
    }

    YearMonth currentMonth = YearMonth.from(today);
    LocalDate monthStart = currentMonth.atDay(1);
    LocalDate monthEnd = currentMonth.atEndOfMonth();

    Integer monthlyCount = emailQuotaRepository.getMonthlyCount(monthStart, monthEnd);
    if (monthlyCount >= monthlyLimit) {
      log.warn("Monthly email limit reached: {}/{}", monthlyCount, monthlyLimit);
      return false;
    }

    return true;
  }

  public synchronized void incrementQuota() {
    if (!quotaEnabled) {
      return;
    }

    LocalDate today = LocalDate.now();
    EmailQuota quota = getCurrentQuota(today);

    quota.setDailyCount(quota.getDailyCount() + 1);
    quota.setMonthlyCount(quota.getMonthlyCount() + 1);
    emailQuotaRepository.save(quota);

    log.debug("Email quota updated - Daily: {}/{}, Monthly: {}/{}",
      quota.getDailyCount(), dailyLimit,
      quota.getMonthlyCount(), monthlyLimit);
  }

  private EmailQuota getCurrentQuota(LocalDate date) {
    return emailQuotaRepository.findByDate(date)
      .orElseGet(() -> createNewQuota(date));
  }

  private EmailQuota createNewQuota(LocalDate date) {
    EmailQuota quota = new EmailQuota();
    quota.setDate(date);

    if (date.getDayOfMonth() != 1) {
      YearMonth currentMonth = YearMonth.from(date);
      LocalDate monthStart = currentMonth.atDay(1);
      Integer monthlyCount = emailQuotaRepository.getMonthlyCount(monthStart, date.minusDays(1));
      quota.setMonthlyCount(monthlyCount != null ? monthlyCount : 0);
    }

    return emailQuotaRepository.save(quota);
  }

  public EmailQuotaStatus getQuotaStatus() {
    LocalDate today = LocalDate.now();
    EmailQuota quota = getCurrentQuota(today);

    YearMonth currentMonth = YearMonth.from(today);
    LocalDate monthStart = currentMonth.atDay(1);
    LocalDate monthEnd = currentMonth.atEndOfMonth();
    Integer monthlyCount = emailQuotaRepository.getMonthlyCount(monthStart, monthEnd);

    return EmailQuotaStatus.builder()
      .dailySent(quota.getDailyCount())
      .dailyLimit(dailyLimit)
      .dailyRemaining(Math.max(0, dailyLimit - quota.getDailyCount()))
      .monthlySent(monthlyCount)
      .monthlyLimit(monthlyLimit)
      .monthlyRemaining(Math.max(0, monthlyLimit - monthlyCount))
      .build();
  }

  @lombok.Builder
  @lombok.Data
  public static class EmailQuotaStatus {
    private Integer dailySent;
    private Integer dailyLimit;
    private Integer dailyRemaining;
    private Integer monthlySent;
    private Integer monthlyLimit;
    private Integer monthlyRemaining;
  }
}
