package com.example.financial_tracker.service;

import com.example.financial_tracker.dto.BudgetDTO;
import com.example.financial_tracker.entity.Budget;
import com.example.financial_tracker.entity.Category;
import com.example.financial_tracker.entity.User;
import com.example.financial_tracker.exception.ResourceNotFoundException;
import com.example.financial_tracker.mapper.BudgetMapper;
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
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class BudgetService {

  private final BudgetRepository budgetRepository;
  private final CategoryRepository categoryRepository;
  private final TransactionRepository transactionRepository;
  private final BudgetMapper budgetMapper;

  public BudgetDTO createBudget(User user, BudgetDTO dto) {
    log.info("Creating budget '{}' for user: {}", dto.getName(), user.getEmail());

    Budget budget = budgetMapper.toEntity(dto);
    budget.setUser(user);

    if (dto.getCategoryId() != null) {
      Category category = categoryRepository.findByIdAndUser(dto.getCategoryId(), user)
        .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
      budget.setCategory(category);
    }

    Budget saved = budgetRepository.save(budget);
    return mapBudgetWithSpent(saved);
  }

  public List<BudgetDTO> getUserBudgets(User user) {
    log.info("Fetching budgets for user: {}", user.getEmail());

    List<Budget> budgets = budgetRepository.findByUserAndActiveOrderByCreatedAtDesc(user, true);

    return budgets.stream()
      .map(this::mapBudgetWithSpent)
      .collect(Collectors.toList());
  }

  public BudgetDTO getBudgetById(User user, Long id) {
    log.info("Fetching budget {} for user: {}", id, user.getEmail());

    Budget budget = budgetRepository.findByIdAndUser(id, user)
      .orElseThrow(() -> new ResourceNotFoundException("Budget not found"));

    return mapBudgetWithSpent(budget);
  }

  public BudgetDTO updateBudget(User user, Long id, BudgetDTO dto) {
    log.info("Updating budget {} for user: {}", id, user.getEmail());

    Budget budget = budgetRepository.findByIdAndUser(id, user)
      .orElseThrow(() -> new ResourceNotFoundException("Budget not found"));

    budget.setName(dto.getName());
    budget.setAmount(dto.getAmount());
    budget.setPeriod(dto.getPeriod());
    budget.setNotifyThreshold(dto.getNotifyThreshold());
    budget.setActive(dto.isActive());

    if (dto.getCategoryId() != null) {
      Category category = categoryRepository.findByIdAndUser(dto.getCategoryId(), user)
        .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
      budget.setCategory(category);
    } else {
      budget.setCategory(null);
    }

    Budget saved = budgetRepository.save(budget);
    return mapBudgetWithSpent(saved);
  }

  public void deleteBudget(User user, Long id) {
    log.info("Deleting budget {} for user: {}", id, user.getEmail());

    Budget budget = budgetRepository.findByIdAndUser(id, user)
      .orElseThrow(() -> new ResourceNotFoundException("Budget not found"));

    budgetRepository.delete(budget);
  }

  public Optional<BudgetDTO> findBudgetByCategory(User user, Long categoryId) {
    log.info("Finding budget for category {} for user: {}", categoryId, user.getEmail());

    List<Budget> budgets = budgetRepository.findByUserAndActiveOrderByCreatedAtDesc(user, true);

    return budgets.stream()
      .filter(b -> b.getCategory() != null && b.getCategory().getId().equals(categoryId))
      .findFirst()
      .or(() -> budgets.stream()
        .filter(b -> b.getCategory() == null)
        .findFirst())
      .map(this::mapBudgetWithSpent);
  }

  private BudgetDTO mapBudgetWithSpent(Budget budget) {
    if (budget.getStartDate() == null || budget.getEndDate() == null) {
      budget.calculateDates();
    }

    BudgetDTO dto = budgetMapper.toDto(budget);

    BigDecimal spent = BigDecimal.ZERO;
    if (budget.getCategory() != null) {
      spent = transactionRepository.getTotalExpenseByUserAndCategoryAndDateRange(
        budget.getUser(),
        budget.getCategory(),
        budget.getStartDate(),
        budget.getEndDate()
      );
    } else {
      spent = transactionRepository.getTotalExpenseByUserAndDateRange(
        budget.getUser(),
        budget.getStartDate(),
        budget.getEndDate()
      );
    }

    dto.setSpent(spent);
    dto.setRemaining(budget.getAmount().subtract(spent));

    BigDecimal percentUsed = budget.getAmount().compareTo(BigDecimal.ZERO) > 0
      ? spent.multiply(new BigDecimal("100")).divide(budget.getAmount(), 2, RoundingMode.HALF_UP)
      : BigDecimal.ZERO;

    dto.setPercentUsed(percentUsed);
    dto.setOverBudget(spent.compareTo(budget.getAmount()) > 0);

    return dto;
  }
}
