package com.example.financial_tracker.service;

import com.example.financial_tracker.dto.MonthlyReportDTO;
import com.example.financial_tracker.dto.WeeklyReportDTO;
import com.example.financial_tracker.entity.*;
import com.example.financial_tracker.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReportService {

  private final TransactionRepository transactionRepository;
  private final BudgetRepository budgetRepository;

  public WeeklyReportDTO generateWeeklyReport(User user) {
    log.info("Generating weekly report for user: {}", user.getEmail());

    LocalDate endDate = LocalDate.now();
    LocalDate startDate = endDate.minusDays(7);

    List<Transaction> weekTransactions = transactionRepository
      .findByUserAndDateBetweenOrderByDateDesc(user, startDate, endDate);

    BigDecimal totalIncome = BigDecimal.ZERO;
    BigDecimal totalExpenses = BigDecimal.ZERO;
    Map<String, BigDecimal> categoryExpenses = new HashMap<>();

    for (Transaction t : weekTransactions) {
      if (t.getType() == TransactionType.INCOME) {
        totalIncome = totalIncome.add(t.getAmount());
      } else {
        totalExpenses = totalExpenses.add(t.getAmount());

        if (t.getCategory() != null) {
          String categoryName = t.getCategory().getName();
          categoryExpenses.merge(categoryName, t.getAmount(), BigDecimal::add);
        }
      }
    }

    BigDecimal netAmount = totalIncome.subtract(totalExpenses);

    final BigDecimal finalTotalExpenses = totalExpenses;
    List<WeeklyReportDTO.CategoryExpenseDTO> topCategories = categoryExpenses.entrySet().stream()
      .sorted(Map.Entry.<String, BigDecimal>comparingByValue().reversed())
      .limit(5)
      .map(entry -> WeeklyReportDTO.CategoryExpenseDTO.builder()
        .categoryName(entry.getKey())
        .amount(entry.getValue())
        .percentOfTotal(finalTotalExpenses.compareTo(BigDecimal.ZERO) > 0
          ? entry.getValue().multiply(new BigDecimal("100"))
          .divide(finalTotalExpenses, 2, RoundingMode.HALF_UP)
          : BigDecimal.ZERO)
        .build())
      .collect(Collectors.toList());

    return WeeklyReportDTO.builder()
      .startDate(startDate)
      .endDate(endDate)
      .totalIncome(totalIncome)
      .totalExpenses(totalExpenses)
      .netAmount(netAmount)
      .totalTransactions(weekTransactions.size())
      .topExpenseCategories(topCategories)
      .build();
  }

  public MonthlyReportDTO generateMonthlyReport(User user, int month, int year) {
    log.info("Generating monthly report for user: {} for {}/{}", user.getEmail(), month, year);

    LocalDate startDate = LocalDate.of(year, month, 1);
    LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());

    if (startDate.isAfter(LocalDate.now())) {
      log.warn("Cannot generate report for future month: {}/{}", month, year);
      return MonthlyReportDTO.builder()
        .month(month)
        .year(year)
        .startDate(startDate)
        .endDate(endDate)
        .totalIncome(BigDecimal.ZERO)
        .totalExpenses(BigDecimal.ZERO)
        .netAmount(BigDecimal.ZERO)
        .savingsRate(BigDecimal.ZERO)
        .expensesByCategory(new ArrayList<>())
        .totalTransactions(0)
        .incomeTransactions(0)
        .expenseTransactions(0)
        .budgetSummary(new ArrayList<>())
        .budgetsExceeded(0)
        .build();
    }

    List<Transaction> monthTransactions = transactionRepository
      .findByUserAndDateBetweenOrderByDateDesc(user, startDate, endDate);

    BigDecimal totalIncome = BigDecimal.ZERO;
    BigDecimal totalExpenses = BigDecimal.ZERO;
    Map<String, List<Transaction>> expensesByCategory = new HashMap<>();
    int incomeCount = 0;
    int expenseCount = 0;

    for (Transaction t : monthTransactions) {
      if (t.getType() == TransactionType.INCOME) {
        totalIncome = totalIncome.add(t.getAmount());
        incomeCount++;
      } else {
        totalExpenses = totalExpenses.add(t.getAmount());
        expenseCount++;

        if (t.getCategory() != null) {
          String categoryName = t.getCategory().getName();
          expensesByCategory.computeIfAbsent(categoryName, k -> new ArrayList<>()).add(t);
        }
      }
    }

    BigDecimal netAmount = totalIncome.subtract(totalExpenses);
    BigDecimal savingsRate = totalIncome.compareTo(BigDecimal.ZERO) > 0
      ? netAmount.divide(totalIncome, 4, RoundingMode.HALF_UP).multiply(new BigDecimal("100"))
      : BigDecimal.ZERO;

    final BigDecimal finalTotalExpenses = totalExpenses;
    List<MonthlyReportDTO.CategorySummaryDTO> categorySummaries = expensesByCategory.entrySet().stream()
      .map(entry -> {
        String categoryName = entry.getKey();
        List<Transaction> transactions = entry.getValue();
        BigDecimal categoryTotal = transactions.stream()
          .map(Transaction::getAmount)
          .reduce(BigDecimal.ZERO, BigDecimal::add);

        return MonthlyReportDTO.CategorySummaryDTO.builder()
          .categoryName(categoryName)
          .amount(categoryTotal)
          .transactionCount(transactions.size())
          .percentOfTotal(finalTotalExpenses.compareTo(BigDecimal.ZERO) > 0
            ? categoryTotal.multiply(new BigDecimal("100"))
            .divide(finalTotalExpenses, 2, RoundingMode.HALF_UP)
            : BigDecimal.ZERO)
          .averageAmount(categoryTotal.divide(
            BigDecimal.valueOf(transactions.size()), 2, RoundingMode.HALF_UP))
          .build();
      })
      .sorted((a, b) -> b.getAmount().compareTo(a.getAmount()))
      .collect(Collectors.toList());

    List<MonthlyReportDTO.BudgetSummaryDTO> budgetSummary = new ArrayList<>();
    try {
      List<Budget> budgets = budgetRepository.findActiveByUser(user);
      for (Budget budget : budgets) {
        BigDecimal spent = BigDecimal.ZERO;

        if (budget.getCategory() != null) {
          spent = monthTransactions.stream()
            .filter(t -> t.getType() == TransactionType.EXPENSE)
            .filter(t -> t.getCategory() != null &&
              t.getCategory().getId().equals(budget.getCategory().getId()))
            .map(Transaction::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        } else {
          spent = totalExpenses;
        }

        BigDecimal percentUsed = budget.getAmount().compareTo(BigDecimal.ZERO) > 0
          ? spent.multiply(new BigDecimal("100"))
          .divide(budget.getAmount(), 2, RoundingMode.HALF_UP)
          : BigDecimal.ZERO;

        budgetSummary.add(MonthlyReportDTO.BudgetSummaryDTO.builder()
          .budgetName(budget.getName())
          .categoryName(budget.getCategory() != null ?
            budget.getCategory().getName() : "All Categories")
          .limit(budget.getAmount())
          .spent(spent)
          .percentUsed(percentUsed)
          .isExceeded(spent.compareTo(budget.getAmount()) > 0)
          .build());
      }
    } catch (Exception e) {
      log.error("Error calculating budget summary", e);
    }

    return MonthlyReportDTO.builder()
      .month(month)
      .year(year)
      .startDate(startDate)
      .endDate(endDate)
      .totalIncome(totalIncome)
      .totalExpenses(totalExpenses)
      .netAmount(netAmount)
      .savingsRate(savingsRate)
      .expensesByCategory(categorySummaries)
      .totalTransactions(monthTransactions.size())
      .incomeTransactions(incomeCount)
      .expenseTransactions(expenseCount)
      .budgetSummary(budgetSummary)
      .budgetsExceeded((int) budgetSummary.stream()
        .filter(MonthlyReportDTO.BudgetSummaryDTO::getIsExceeded).count())
      .build();
  }
}
