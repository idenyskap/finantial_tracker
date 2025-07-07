package com.example.financial_tracker.service;

import com.example.financial_tracker.dto.*;
import com.example.financial_tracker.entity.Transaction;
import com.example.financial_tracker.entity.User;
import com.example.financial_tracker.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DashboardService {

  private final TransactionRepository transactionRepository;
  private final TransactionService transactionService;
  private final AnalyticsService analyticsService;

  public DashboardDTO getDashboard(User user) {
    log.info("Generating dashboard for user: {}", user.getEmail());

    LocalDate now = LocalDate.now();
    LocalDate monthStart = now.withDayOfMonth(1);
    LocalDate monthEnd = now.withDayOfMonth(now.lengthOfMonth());
    LocalDate lastMonthStart = monthStart.minusMonths(1);
    LocalDate lastMonthEnd = lastMonthStart.withDayOfMonth(lastMonthStart.lengthOfMonth());

    BigDecimal currentBalance = transactionService.getBalanceByUser(user);
    BigDecimal totalIncome = transactionService.getTotalIncomeByUser(user);
    BigDecimal totalExpense = transactionService.getTotalExpenseByUser(user);

    List<Object[]> currentMonthStats = transactionRepository.getPeriodStats(user, monthStart, monthEnd);
    BigDecimal monthlyIncome = BigDecimal.ZERO;
    BigDecimal monthlyExpense = BigDecimal.ZERO;

    if (!currentMonthStats.isEmpty()) {
      Object[] stats = currentMonthStats.get(0);
      monthlyIncome = convertToBigDecimal(stats[0]);
      monthlyExpense = convertToBigDecimal(stats[1]);
    }

    List<Object[]> lastMonthStats = transactionRepository.getPeriodStats(user, lastMonthStart, lastMonthEnd);
    BigDecimal lastMonthIncome = BigDecimal.ZERO;
    BigDecimal lastMonthExpense = BigDecimal.ZERO;

    if (!lastMonthStats.isEmpty()) {
      Object[] stats = lastMonthStats.get(0);
      lastMonthIncome = convertToBigDecimal(stats[0]);
      lastMonthExpense = convertToBigDecimal(stats[1]);
    }

    BigDecimal incomeChangePercent = calculatePercentageChange(lastMonthIncome, monthlyIncome);
    BigDecimal expenseChangePercent = calculatePercentageChange(lastMonthExpense, monthlyExpense);

    List<Transaction> recentTransactions = transactionRepository.getRecentTransactions(
      user, PageRequest.of(0, 10)
    );

    List<TransactionDTO> recentTransactionDtos = new ArrayList<>();
    for (Transaction t : recentTransactions) {
      TransactionDTO dto = new TransactionDTO();
      dto.setId(t.getId());
      dto.setAmount(t.getAmount());
      dto.setType(t.getType().toString());
      dto.setCategoryId(t.getCategory().getId());
      dto.setCategoryName(t.getCategory().getName());
      dto.setCategoryColor(t.getCategory().getColor());
      dto.setDate(t.getDate());
      dto.setDescription(t.getDescription());
      dto.setUserId(t.getUser().getId());
      recentTransactionDtos.add(dto);
    }

    List<CategoryStatsDTO> topExpenseCategories = analyticsService.getTopExpenseCategories(
      user, monthStart, monthEnd, 5
    );

    LocalDate thirtyDaysAgo = now.minusDays(29);
    List<DailyStatsDTO> dailyStats = getDailyStats(user, thirtyDaysAgo, now);

    return DashboardDTO.builder()
      .currentBalance(currentBalance)
      .totalIncome(totalIncome)
      .totalExpense(totalExpense)
      .monthlyIncome(monthlyIncome)
      .monthlyExpense(monthlyExpense)
      .monthlyBalance(monthlyIncome.subtract(monthlyExpense))
      .recentTransactions(recentTransactionDtos)
      .topExpenseCategories(topExpenseCategories)
      .dailyStats(dailyStats)
      .incomeChangePercent(incomeChangePercent)
      .expenseChangePercent(expenseChangePercent)
      .build();
  }

  private List<DailyStatsDTO> getDailyStats(User user, LocalDate startDate, LocalDate endDate) {
    List<Object[]> results = transactionRepository.getDailyStats(user, startDate, endDate);
    List<DailyStatsDTO> dailyStats = new ArrayList<>();

    LocalDate currentDate = startDate;
    while (!currentDate.isAfter(endDate)) {
      LocalDate date = currentDate;

      Object[] dayData = results.stream()
        .filter(r -> r[0].equals(date))
        .findFirst()
        .orElse(null);

      BigDecimal income = BigDecimal.ZERO;
      BigDecimal expense = BigDecimal.ZERO;

      if (dayData != null) {
        income = convertToBigDecimal(dayData[1]);
        expense = convertToBigDecimal(dayData[2]);
      }

      dailyStats.add(DailyStatsDTO.builder()
        .date(date)
        .income(income)
        .expense(expense)
        .balance(income.subtract(expense))
        .build());

      currentDate = currentDate.plusDays(1);
    }

    return dailyStats;
  }

  private BigDecimal calculatePercentageChange(BigDecimal oldValue, BigDecimal newValue) {
    if (oldValue.compareTo(BigDecimal.ZERO) == 0) {
      return newValue.compareTo(BigDecimal.ZERO) == 0 ? BigDecimal.ZERO : BigDecimal.valueOf(100);
    }

    return newValue.subtract(oldValue)
      .divide(oldValue, 2, RoundingMode.HALF_UP)
      .multiply(BigDecimal.valueOf(100));
  }

  private BigDecimal convertToBigDecimal(Object value) {
    if (value == null) return BigDecimal.ZERO;
    if (value instanceof BigDecimal) return (BigDecimal) value;
    if (value instanceof Number) return BigDecimal.valueOf(((Number) value).doubleValue());

    try {
      return new BigDecimal(value.toString());
    } catch (NumberFormatException e) {
      return BigDecimal.ZERO;
    }
  }
}
