package com.example.financial_tracker.service;

import com.example.financial_tracker.dto.GoalContributionDTO;
import com.example.financial_tracker.dto.GoalDTO;
import com.example.financial_tracker.dto.TransactionDTO;
import com.example.financial_tracker.entity.*;
import com.example.financial_tracker.exception.BusinessLogicException;
import com.example.financial_tracker.exception.ResourceNotFoundException;
import com.example.financial_tracker.mapper.GoalMapper;
import com.example.financial_tracker.repository.CategoryRepository;
import com.example.financial_tracker.repository.GoalRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class GoalService {

  private final GoalRepository goalRepository;
  private final GoalMapper goalMapper;
  private final CategoryRepository categoryRepository;
  private final TransactionService transactionService;

  public List<GoalDTO> getUserGoals(User user) {
    log.info("Fetching all goals for user: {}", user.getEmail());
    List<Goal> goals = goalRepository.findByUserOrderByPriorityDescTargetDateAsc(user);

    return goals.stream()
      .map(this::enrichGoalDto)
      .collect(Collectors.toList());
  }

  public List<GoalDTO> getActiveGoals(User user) {
    log.info("Fetching active goals for user: {}", user.getEmail());
    List<Goal> goals = goalRepository.findActiveGoalsSortedByUrgency(user);

    return goals.stream()
      .map(this::enrichGoalDto)
      .collect(Collectors.toList());
  }

  public GoalDTO getGoalById(User user, Long id) {
    log.info("Fetching goal ID: {} for user: {}", id, user.getEmail());

    Goal goal = goalRepository.findByIdAndUser(id, user)
      .orElseThrow(() -> new ResourceNotFoundException("Goal not found"));

    return enrichGoalDto(goal);
  }

  public GoalDTO createGoal(User user, GoalDTO dto) {
    log.info("Creating goal '{}' for user: {}", dto.getName(), user.getEmail());

    Goal goal = goalMapper.toEntity(dto);
    goal.setUser(user);
    goal.setStatus(GoalStatus.ACTIVE);
    goal.setCurrentAmount(BigDecimal.ZERO);

    if (dto.getCategoryId() != null) {
      Category category = categoryRepository.findById(dto.getCategoryId())
        .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
      goal.setCategory(category);
    }

    Goal saved = goalRepository.save(goal);
    log.info("Created goal ID: {} for user: {}", saved.getId(), user.getEmail());

    return enrichGoalDto(saved);
  }

  public GoalDTO updateGoal(User user, Long id, GoalDTO dto) {
    log.info("Updating goal ID: {} for user: {}", id, user.getEmail());

    Goal existing = goalRepository.findByIdAndUser(id, user)
      .orElseThrow(() -> new ResourceNotFoundException("Goal not found"));

    existing.setName(dto.getName());
    existing.setDescription(dto.getDescription());
    existing.setTargetAmount(dto.getTargetAmount());
    existing.setTargetDate(dto.getTargetDate());
    existing.setPriority(dto.getPriority());
    existing.setColor(dto.getColor());
    existing.setIcon(dto.getIcon());

    if (dto.getCategoryId() != null && !dto.getCategoryId().equals(
      existing.getCategory() != null ? existing.getCategory().getId() : null)) {
      Category category = categoryRepository.findById(dto.getCategoryId())
        .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
      existing.setCategory(category);
    }

    Goal saved = goalRepository.save(existing);
    return enrichGoalDto(saved);
  }

  public void deleteGoal(User user, Long id) {
    log.info("Deleting goal ID: {} for user: {}", id, user.getEmail());

    Goal goal = goalRepository.findByIdAndUser(id, user)
      .orElseThrow(() -> new ResourceNotFoundException("Goal not found"));

    goalRepository.delete(goal);
    log.info("Deleted goal ID: {} for user: {}", id, user.getEmail());
  }

  @Transactional
  public GoalDTO contributeToGoal(User user, GoalContributionDTO contribution) {
    log.info("Processing contribution to goal ID: {} for user: {}",
      contribution.getGoalId(), user.getEmail());

    Goal goal = goalRepository.findByIdAndUser(contribution.getGoalId(), user)
      .orElseThrow(() -> new ResourceNotFoundException("Goal not found"));

    if (goal.getStatus() != GoalStatus.ACTIVE) {
      throw new BusinessLogicException("Cannot contribute to inactive goal");
    }

    BigDecimal newAmount;
    String transactionDescription;

    if (contribution.getType() == GoalContributionDTO.ContributionType.ADD) {
      newAmount = goal.getCurrentAmount().add(contribution.getAmount());
      transactionDescription = "Contribution to goal: " + goal.getName();

      if (goal.getCategory() != null) {
        TransactionDTO transaction = TransactionDTO.builder()
          .amount(contribution.getAmount())
          .type(TransactionType.EXPENSE.name())
          .categoryId(goal.getCategory().getId())
          .date(contribution.getDate() != null ? contribution.getDate() : LocalDate.now())
          .description(transactionDescription +
            (contribution.getNote() != null ? " - " + contribution.getNote() : ""))
          .build();

        transactionService.createTransaction(transaction, user);
      }
    } else {
      newAmount = goal.getCurrentAmount().subtract(contribution.getAmount());
      if (newAmount.compareTo(BigDecimal.ZERO) < 0) {
        throw new BusinessLogicException("Cannot withdraw more than current amount");
      }
      transactionDescription = "Withdrawal from goal: " + goal.getName();
    }

    goal.setCurrentAmount(newAmount);

    if (newAmount.compareTo(goal.getTargetAmount()) >= 0) {
      goal.setStatus(GoalStatus.COMPLETED);
      goal.setCompletedAt(LocalDateTime.now());
      log.info("Goal ID: {} completed for user: {}", goal.getId(), user.getEmail());
    }

    Goal saved = goalRepository.save(goal);
    return enrichGoalDto(saved);
  }

  public void pauseGoal(User user, Long id) {
    log.info("Pausing goal ID: {} for user: {}", id, user.getEmail());
    updateGoalStatus(user, id, GoalStatus.PAUSED);
  }

  public void resumeGoal(User user, Long id) {
    log.info("Resuming goal ID: {} for user: {}", id, user.getEmail());
    updateGoalStatus(user, id, GoalStatus.ACTIVE);
  }

  public void cancelGoal(User user, Long id) {
    log.info("Cancelling goal ID: {} for user: {}", id, user.getEmail());
    updateGoalStatus(user, id, GoalStatus.CANCELLED);
  }

  private void updateGoalStatus(User user, Long id, GoalStatus status) {
    Goal goal = goalRepository.findByIdAndUser(id, user)
      .orElseThrow(() -> new ResourceNotFoundException("Goal not found"));

    goal.setStatus(status);
    goalRepository.save(goal);
  }

  private GoalDTO enrichGoalDto(Goal goal) {
    GoalDTO dto = goalMapper.toDto(goal);

    if (goal.getTargetAmount().compareTo(BigDecimal.ZERO) > 0) {
      BigDecimal progress = goal.getCurrentAmount()
        .divide(goal.getTargetAmount(), 4, RoundingMode.HALF_UP)
        .multiply(BigDecimal.valueOf(100));
      dto.setProgressPercentage(progress);
    } else {
      dto.setProgressPercentage(BigDecimal.ZERO);
    }

    BigDecimal remaining = goal.getTargetAmount().subtract(goal.getCurrentAmount());
    dto.setRemainingAmount(remaining.compareTo(BigDecimal.ZERO) > 0 ? remaining : BigDecimal.ZERO);

    LocalDate today = LocalDate.now();
    long daysRemaining = ChronoUnit.DAYS.between(today, goal.getTargetDate());
    dto.setDaysRemaining((int) daysRemaining);

    if (daysRemaining > 0 && dto.getRemainingAmount().compareTo(BigDecimal.ZERO) > 0) {
      BigDecimal dailyRequired = dto.getRemainingAmount()
        .divide(BigDecimal.valueOf(daysRemaining), 2, RoundingMode.HALF_UP);
      dto.setRequiredDailySaving(dailyRequired);

      BigDecimal weeklyRequired = dailyRequired.multiply(BigDecimal.valueOf(7));
      dto.setRequiredWeeklySaving(weeklyRequired);

      BigDecimal monthlyRequired = dailyRequired.multiply(BigDecimal.valueOf(30));
      dto.setRequiredMonthlySaving(monthlyRequired);
    } else {
      dto.setRequiredDailySaving(BigDecimal.ZERO);
      dto.setRequiredWeeklySaving(BigDecimal.ZERO);
      dto.setRequiredMonthlySaving(BigDecimal.ZERO);
    }

    dto.setIsOverdue(daysRemaining < 0 && goal.getStatus() == GoalStatus.ACTIVE);
    dto.setIsCompleted(goal.getStatus() == GoalStatus.COMPLETED);

    return dto;
  }
}
