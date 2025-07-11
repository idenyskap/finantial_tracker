package com.example.financial_tracker.service;

import com.example.financial_tracker.dto.BudgetDTO;
import com.example.financial_tracker.entity.*;
import com.example.financial_tracker.exception.BusinessLogicException;
import com.example.financial_tracker.exception.ResourceNotFoundException;
import com.example.financial_tracker.repository.BudgetRepository;
import com.example.financial_tracker.repository.CategoryRepository;
import com.example.financial_tracker.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class BudgetService {

  private final BudgetRepository budgetRepository;
  private final CategoryRepository categoryRepository;
  private final TransactionRepository transactionRepository;

  public BudgetDTO createBudget(User user, BudgetDTO dto) {
    log.info("Creating budget '{}' for user: {}", dto.getName(), user.getEmail());

    Category category = null;
    if (dto.getCategoryId() != null) {
      category = categoryRepository.findById(dto.getCategoryId())
        .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

      if (!category.getUser().getId().equals(user.getId())) {
        throw new BusinessLogicException("Category does not belong to user");
      }

      if (budgetRepository.existsByUserAndCategoryAndActiveTrue(user, category)) {
        throw new BusinessLogicException("Active budget already exists for this category");
      }
    }

    Budget budget = Budget.builder()
      .name(dto.getName())
      .amount(dto.getAmount())
      .period(dto.getPeriod())
      .category(category)
      .user(user)
      .active(dto.isActive())
      .notifyThreshold(dto.getNotifyThreshold())
      .build();

    Budget saved = budgetRepository.save(budget);
    log.info("Created budget ID: {} for user: {}", saved.getId(), user.getEmail());

    return mapToDto(saved);
  }

  public List<BudgetDTO> getUserBudgets(User user) {
    log.info("Fetching budgets for user: {}", user.getEmail());

    List<Budget> budgets = budgetRepository.findByUserAndActiveOrderByCreatedAtDesc(user, true);

    return budgets.stream()
      .map(this::mapToDto)
      .collect(Collectors.toList());
  }

  public BudgetDTO getBudgetById(User user, Long id) {
    Budget budget = budgetRepository.findByIdAndUser(id, user)
      .orElseThrow(() -> new ResourceNotFoundException("Budget not found"));

    return mapToDto(budget);
  }

  public BudgetDTO updateBudget(User user, Long id, BudgetDTO dto) {
    log.info("Updating budget ID: {} for user: {}", id, user.getEmail());

    Budget budget = budgetRepository.findByIdAndUser(id, user)
      .orElseThrow(() -> new ResourceNotFoundException("Budget not found"));

    budget.setName(dto.getName());
    budget.setAmount(dto.getAmount());
    budget.setPeriod(dto.getPeriod());
    budget.setActive(dto.isActive());
    budget.setNotifyThreshold(dto.getNotifyThreshold());

    Budget saved = budgetRepository.save(budget);
    return mapToDto(saved);
  }

  public void deleteBudget(User user, Long id) {
    log.info("Deleting budget ID: {} for user: {}", id, user.getEmail());

    Budget budget = budgetRepository.findByIdAndUser(id, user)
      .orElseThrow(() -> new ResourceNotFoundException("Budget not found"));

    budgetRepository.delete(budget);
  }

  private BudgetDTO mapToDto(Budget budget) {
    BudgetDTO dto = BudgetDTO.builder()
      .id(budget.getId())
      .name(budget.getName())
      .amount(budget.getAmount())
      .period(budget.getPeriod())
      .active(budget.isActive())
      .notifyThreshold(budget.getNotifyThreshold())
      .createdAt(budget.getCreatedAt())
      .updatedAt(budget.getUpdatedAt())
      .build();

    if (budget.getCategory() != null) {
      dto.setCategoryId(budget.getCategory().getId());
      dto.setCategoryName(budget.getCategory().getName());
      dto.setCategoryColor(budget.getCategory().getColor());
    }

    LocalDate[] period = getPeriodDates(budget.getPeriod());
    BigDecimal spent = calculateSpent(budget, period[0], period[1]);

    dto.setSpent(spent);
    dto.setRemaining(budget.getAmount().subtract(spent));

    BigDecimal percentUsed = BigDecimal.ZERO;
    if (budget.getAmount().compareTo(BigDecimal.ZERO) > 0) {
      percentUsed = spent.divide(budget.getAmount(), 2, RoundingMode.HALF_UP)
        .multiply(BigDecimal.valueOf(100));
    }
    dto.setPercentUsed(percentUsed);
    dto.setOverBudget(spent.compareTo(budget.getAmount()) > 0);

    return dto;
  }

  private LocalDate[] getPeriodDates(BudgetPeriod period) {
    LocalDate now = LocalDate.now();
    LocalDate start, end;

    switch (period) {
      case WEEKLY:
        start = now.with(TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY));
        end = start.plusDays(6);
        break;
      case MONTHLY:
        start = now.withDayOfMonth(1);
        end = now.withDayOfMonth(now.lengthOfMonth());
        break;
      case QUARTERLY:
        int quarter = (now.getMonthValue() - 1) / 3;
        start = now.withMonth(quarter * 3 + 1).withDayOfMonth(1);
        end = start.plusMonths(3).minusDays(1);
        break;
      case YEARLY:
        start = now.withDayOfYear(1);
        end = now.withDayOfYear(now.lengthOfYear());
        break;
      default:
        start = now.withDayOfMonth(1);
        end = now.withDayOfMonth(now.lengthOfMonth());
    }

    return new LocalDate[]{start, end};
  }

  private BigDecimal calculateSpent(Budget budget, LocalDate start, LocalDate end) {
    if (budget.getCategory() != null) {
      return transactionRepository.getTotalExpenseByUserAndCategoryAndDateRange(
        budget.getUser(), budget.getCategory(), start, end);
    } else {
      return transactionRepository.getTotalExpenseByUserAndDateRange(
        budget.getUser(), start, end);
    }
  }
}
