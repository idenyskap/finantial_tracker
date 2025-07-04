package com.example.financial_tracker.service;

import com.example.financial_tracker.dto.*;
import com.example.financial_tracker.entity.User;
import com.example.financial_tracker.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AnalyticsService {

  private final TransactionRepository transactionRepository;

  public AnalyticsDTO getFullAnalytics(User user, LocalDate startDate, LocalDate endDate) {
    log.info("Generating full analytics for user: {} from {} to {}",
      user.getEmail(), startDate, endDate);

    BigDecimal totalIncome = getTotalIncomeOrZero(user);
    BigDecimal totalExpense = getTotalExpenseOrZero(user);
    BigDecimal currentBalance = getBalanceOrZero(user);

    return AnalyticsDTO.builder()
      .totalIncome(totalIncome)
      .totalExpense(totalExpense)
      .currentBalance(currentBalance)
      .netChange(totalIncome.subtract(totalExpense))
      .monthlyStats(getMonthlyStats(user, startDate, endDate))
      .topExpenseCategories(getTopExpenseCategories(user, startDate, endDate, 10))
      .topIncomeCategories(getTopIncomeCategories(user, startDate, endDate, 10))
      .comparison(getComparisonStats(user))
      .build();
  }

  public List<MonthlyStatsDTO> getMonthlyStats(User user, LocalDate startDate, LocalDate endDate) {
    log.info("Calculating monthly stats for user: {} from {} to {}",
      user.getEmail(), startDate, endDate);

    List<Object[]> results = transactionRepository.getMonthlyStatsByUser(user, startDate, endDate);
    List<MonthlyStatsDTO> monthlyStats = new ArrayList<>();

    for (Object[] result : results) {
      String month = (String) result[0];
      BigDecimal income = convertToBigDecimal(result[1]);
      BigDecimal expense = convertToBigDecimal(result[2]);
      Long transactionCount = (Long) result[3];

      // Parse month and create readable format
      YearMonth yearMonth = YearMonth.parse(month);
      String monthName = yearMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy"));

      monthlyStats.add(MonthlyStatsDTO.builder()
        .month(month)
        .monthName(monthName)
        .income(income)
        .expense(expense)
        .netChange(income.subtract(expense))
        .transactionCount(transactionCount != null ? transactionCount.intValue() : 0)
        .build());
    }

    log.info("Generated {} monthly stats entries for user: {}", monthlyStats.size(), user.getEmail());
    return monthlyStats;
  }

  public List<CategoryStatsDTO> getTopExpenseCategories(User user, LocalDate startDate, LocalDate endDate, int limit) {
    log.info("Getting top {} expense categories for user: {} from {} to {}",
      limit, user.getEmail(), startDate, endDate);

    // Use default date range if null values provided
    LocalDate effectiveStartDate = startDate != null ? startDate : LocalDate.now().minusYears(10);
    LocalDate effectiveEndDate = endDate != null ? endDate : LocalDate.now();

    List<Object[]> results = transactionRepository.getExpenseCategoryStats(user, effectiveStartDate, effectiveEndDate);
    BigDecimal totalExpenses = getTotalExpenseOrZero(user);

    return buildCategoryStats(results, totalExpenses, limit);
  }

  public List<CategoryStatsDTO> getTopIncomeCategories(User user, LocalDate startDate, LocalDate endDate, int limit) {
    log.info("Getting top {} income categories for user: {} from {} to {}",
      limit, user.getEmail(), startDate, endDate);

    // Use default date range if null values provided
    LocalDate effectiveStartDate = startDate != null ? startDate : LocalDate.now().minusYears(10);
    LocalDate effectiveEndDate = endDate != null ? endDate : LocalDate.now();

    List<Object[]> results = transactionRepository.getIncomeCategoryStats(user, effectiveStartDate, effectiveEndDate);
    BigDecimal totalIncome = getTotalIncomeOrZero(user);

    return buildCategoryStats(results, totalIncome, limit);
  }

  private List<CategoryStatsDTO> buildCategoryStats(List<Object[]> results, BigDecimal total, int limit) {
    List<CategoryStatsDTO> categoryStats = new ArrayList<>();

    for (int i = 0; i < Math.min(results.size(), limit); i++) {
      Object[] result = results.get(i);

      Long categoryId = (Long) result[0];
      String categoryName = (String) result[1];
      String categoryColor = (String) result[2];
      BigDecimal totalAmount = convertToBigDecimal(result[3]);
      Long transactionCount = (Long) result[4];
      BigDecimal averageAmount = convertToBigDecimal(result[5]);

      // Calculate percentage
      BigDecimal percentage = BigDecimal.ZERO;
      if (total.compareTo(BigDecimal.ZERO) > 0) {
        percentage = totalAmount.divide(total, 4, RoundingMode.HALF_UP)
          .multiply(BigDecimal.valueOf(100));
      }

      categoryStats.add(CategoryStatsDTO.builder()
        .categoryId(categoryId)
        .categoryName(categoryName)
        .categoryColor(categoryColor)
        .totalAmount(totalAmount)
        .transactionCount(transactionCount != null ? transactionCount.intValue() : 0)
        .averageAmount(averageAmount)
        .percentage(percentage)
        .build());
    }

    return categoryStats;
  }

  public ComparisonStatsDTO getComparisonStats(User user) {
    log.info("Calculating comparison stats for user: {}", user.getEmail());

    LocalDate now = LocalDate.now();
    LocalDate currentMonthStart = now.withDayOfMonth(1);
    LocalDate currentMonthEnd = now.withDayOfMonth(now.lengthOfMonth());

    LocalDate previousMonthStart = currentMonthStart.minusMonths(1);
    LocalDate previousMonthEnd = previousMonthStart.withDayOfMonth(previousMonthStart.lengthOfMonth());

    PeriodStatsDTO currentPeriod = getPeriodStats(user, currentMonthStart, currentMonthEnd, "Current Month");
    PeriodStatsDTO previousPeriod = getPeriodStats(user, previousMonthStart, previousMonthEnd, "Previous Month");

    // Calculate changes
    BigDecimal incomeChange = currentPeriod.getIncome().subtract(previousPeriod.getIncome());
    BigDecimal expenseChange = currentPeriod.getExpense().subtract(previousPeriod.getExpense());

    // Calculate percentage changes
    BigDecimal incomeChangePercent = calculatePercentageChange(previousPeriod.getIncome(), currentPeriod.getIncome());
    BigDecimal expenseChangePercent = calculatePercentageChange(previousPeriod.getExpense(), currentPeriod.getExpense());

    return ComparisonStatsDTO.builder()
      .currentPeriod(currentPeriod)
      .previousPeriod(previousPeriod)
      .incomeChange(incomeChange)
      .expenseChange(expenseChange)
      .incomeChangePercent(incomeChangePercent)
      .expenseChangePercent(expenseChangePercent)
      .build();
  }

  private PeriodStatsDTO getPeriodStats(User user, LocalDate startDate, LocalDate endDate, String periodName) {
    List<Object[]> results = transactionRepository.getPeriodStats(user, startDate, endDate);

    if (results.isEmpty()) {
      return PeriodStatsDTO.builder()
        .startDate(startDate)
        .endDate(endDate)
        .periodName(periodName)
        .income(BigDecimal.ZERO)
        .expense(BigDecimal.ZERO)
        .netChange(BigDecimal.ZERO)
        .transactionCount(0)
        .build();
    }

    Object[] result = results.get(0);
    BigDecimal income = convertToBigDecimal(result[0]);
    BigDecimal expense = convertToBigDecimal(result[1]);
    Long transactionCount = (Long) result[2];

    return PeriodStatsDTO.builder()
      .startDate(startDate)
      .endDate(endDate)
      .periodName(periodName)
      .income(income)
      .expense(expense)
      .netChange(income.subtract(expense))
      .transactionCount(transactionCount != null ? transactionCount.intValue() : 0)
      .build();
  }

  private BigDecimal calculatePercentageChange(BigDecimal oldValue, BigDecimal newValue) {
    if (oldValue.compareTo(BigDecimal.ZERO) == 0) {
      return newValue.compareTo(BigDecimal.ZERO) == 0 ? BigDecimal.ZERO : BigDecimal.valueOf(100);
    }

    return newValue.subtract(oldValue)
      .divide(oldValue, 4, RoundingMode.HALF_UP)
      .multiply(BigDecimal.valueOf(100));
  }

  // Helper methods to safely handle null values and type conversion
  private BigDecimal getTotalIncomeOrZero(User user) {
    BigDecimal income = transactionRepository.getTotalIncomeByUser(user);
    return income != null ? income : BigDecimal.ZERO;
  }

  private BigDecimal getTotalExpenseOrZero(User user) {
    BigDecimal expense = transactionRepository.getTotalExpenseByUser(user);
    return expense != null ? expense : BigDecimal.ZERO;
  }

  private BigDecimal getBalanceOrZero(User user) {
    BigDecimal balance = transactionRepository.calculateBalanceByUser(user);
    return balance != null ? balance : BigDecimal.ZERO;
  }

  // Convert various numeric types to BigDecimal
  private BigDecimal convertToBigDecimal(Object value) {
    if (value == null) {
      return BigDecimal.ZERO;
    }

    if (value instanceof BigDecimal) {
      return (BigDecimal) value;
    }

    if (value instanceof Double) {
      return BigDecimal.valueOf((Double) value);
    }

    if (value instanceof Float) {
      return BigDecimal.valueOf((Float) value);
    }

    if (value instanceof Integer) {
      return BigDecimal.valueOf((Integer) value);
    }

    if (value instanceof Long) {
      return BigDecimal.valueOf((Long) value);
    }

    // Try to parse as string
    try {
      return new BigDecimal(value.toString());
    } catch (NumberFormatException e) {
      log.warn("Could not convert value to BigDecimal: {}", value);
      return BigDecimal.ZERO;
    }
  }
}
