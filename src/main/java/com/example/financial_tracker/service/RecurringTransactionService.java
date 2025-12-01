package com.example.financial_tracker.service;

import com.example.financial_tracker.dto.RecurringTransactionDTO;
import com.example.financial_tracker.dto.TransactionDTO;
import com.example.financial_tracker.entity.*;
import com.example.financial_tracker.enumerations.RecurrenceFrequency;
import com.example.financial_tracker.enumerations.TransactionType;
import com.example.financial_tracker.exception.ResourceNotFoundException;
import com.example.financial_tracker.mapper.RecurringTransactionMapper;
import com.example.financial_tracker.repository.CategoryRepository;
import com.example.financial_tracker.repository.RecurringTransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class RecurringTransactionService {

  private final RecurringTransactionRepository recurringTransactionRepository;
  private final RecurringTransactionMapper recurringTransactionMapper;
  private final CategoryRepository categoryRepository;
  private final TransactionService transactionService;

  @Transactional(readOnly = true)
  public List<RecurringTransactionDTO> getUserRecurringTransactions(User user) {
    log.info("Fetching recurring transactions for user: {}", user.getEmail());
    List<RecurringTransaction> transactions = recurringTransactionRepository.findByUserOrderByNextExecutionDateAsc(user);
    return recurringTransactionMapper.toDtoList(transactions);
  }

  public RecurringTransactionDTO createRecurringTransaction(User user, RecurringTransactionDTO dto) {
    log.info("Creating recurring transaction '{}' for user: {}", dto.getName(), user.getEmail());

    Category category = categoryRepository.findById(dto.getCategoryId())
      .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

    RecurringTransaction transaction = recurringTransactionMapper.toEntity(dto);
    transaction.setUser(user);
    transaction.setCategory(category);
    transaction.setActive(true);

    LocalDate nextDate = calculateNextExecutionDate(dto.getStartDate(), dto.getFrequency(), dto.getDayOfMonth(), dto.getDayOfWeek());
    transaction.setNextExecutionDate(nextDate);

    RecurringTransaction saved = recurringTransactionRepository.save(transaction);
    log.info("Created recurring transaction ID: {} for user: {}", saved.getId(), user.getEmail());

    return recurringTransactionMapper.toDto(saved);
  }

  public RecurringTransactionDTO updateRecurringTransaction(User user, Long id, RecurringTransactionDTO dto) {
    log.info("Updating recurring transaction ID: {} for user: {}", id, user.getEmail());

    RecurringTransaction existing = recurringTransactionRepository.findByIdAndUser(id, user)
      .orElseThrow(() -> new ResourceNotFoundException("Recurring transaction not found"));

    if (dto.getCategoryId() != null && !dto.getCategoryId().equals(existing.getCategory().getId())) {
      Category category = categoryRepository.findById(dto.getCategoryId())
        .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
      existing.setCategory(category);
    }

    existing.setName(dto.getName());
    existing.setAmount(dto.getAmount());
    existing.setType(TransactionType.valueOf(dto.getType()));
    existing.setDescription(dto.getDescription());
    existing.setFrequency(dto.getFrequency());
    existing.setStartDate(dto.getStartDate());
    existing.setEndDate(dto.getEndDate());
    existing.setDayOfMonth(dto.getDayOfMonth());
    existing.setDayOfWeek(dto.getDayOfWeek());
    existing.setActive(dto.getActive());

    LocalDate nextDate = calculateNextExecutionDate(
      existing.getLastExecutionDate() != null ? existing.getLastExecutionDate() : existing.getStartDate(),
      existing.getFrequency(),
      existing.getDayOfMonth(),
      existing.getDayOfWeek()
    );
    existing.setNextExecutionDate(nextDate);

    RecurringTransaction saved = recurringTransactionRepository.save(existing);
    return recurringTransactionMapper.toDto(saved);
  }

  public void deleteRecurringTransaction(User user, Long id) {
    log.info("Deleting recurring transaction ID: {} for user: {}", id, user.getEmail());

    RecurringTransaction transaction = recurringTransactionRepository.findByIdAndUser(id, user)
      .orElseThrow(() -> new ResourceNotFoundException("Recurring transaction not found"));

    recurringTransactionRepository.delete(transaction);
    log.info("Deleted recurring transaction ID: {} for user: {}", id, user.getEmail());
  }

  @Scheduled(cron = "0 0 1 * * *")
  @Transactional
  public void processRecurringTransactions() {
    log.info("Starting processing of recurring transactions");

    LocalDate today = LocalDate.now();
    List<RecurringTransaction> dueTransactions = recurringTransactionRepository.findDueRecurringTransactions(today);

    for (RecurringTransaction recurring : dueTransactions) {
      try {
        if (recurring.getEndDate() != null && recurring.getEndDate().isBefore(today)) {
          recurring.setActive(false);
          recurringTransactionRepository.save(recurring);
          continue;
        }

        TransactionDTO transactionDto = TransactionDTO.builder()
          .amount(recurring.getAmount())
          .type(recurring.getType().name())
          .categoryId(recurring.getCategory().getId())
          .date(today)
          .description(recurring.getDescription() != null ?
            recurring.getDescription() : "Recurring: " + recurring.getName())
          .build();

        transactionService.createTransaction(transactionDto, recurring.getUser());

        recurring.setLastExecutionDate(today);
        LocalDate nextDate = calculateNextExecutionDate(
          today,
          recurring.getFrequency(),
          recurring.getDayOfMonth(),
          recurring.getDayOfWeek()
        );
        recurring.setNextExecutionDate(nextDate);

        recurringTransactionRepository.save(recurring);
        log.info("Processed recurring transaction ID: {} for user: {}",
          recurring.getId(), recurring.getUser().getEmail());

      } catch (Exception e) {
        log.error("Error processing recurring transaction ID: {}", recurring.getId(), e);
      }
    }

    log.info("Completed processing of {} recurring transactions", dueTransactions.size());
  }

  private LocalDate calculateNextExecutionDate(LocalDate fromDate, RecurrenceFrequency frequency,
                                               Integer dayOfMonth, Integer dayOfWeek) {
    LocalDate nextDate = fromDate;

    switch (frequency) {
      case DAILY:
        nextDate = fromDate.plusDays(1);
        break;

      case WEEKLY:
        if (dayOfWeek != null) {
          nextDate = fromDate.with(TemporalAdjusters.next(DayOfWeek.of(dayOfWeek)));
        } else {
          nextDate = fromDate.plusWeeks(1);
        }
        break;

      case BIWEEKLY:
        nextDate = fromDate.plusWeeks(2);
        break;

      case MONTHLY:
        if (dayOfMonth != null) {
          nextDate = fromDate.plusMonths(1);
          int lastDayOfMonth = nextDate.lengthOfMonth();
          if (dayOfMonth > lastDayOfMonth) {
            nextDate = nextDate.withDayOfMonth(lastDayOfMonth);
          } else {
            nextDate = nextDate.withDayOfMonth(dayOfMonth);
          }
        } else {
          nextDate = fromDate.plusMonths(1);
        }
        break;

      case QUARTERLY:
        nextDate = fromDate.plusMonths(3);
        break;

      case YEARLY:
        nextDate = fromDate.plusYears(1);
        break;
    }

    return nextDate;
  }

  public void executeRecurringTransactionNow(User user, Long id) {
    log.info("Executing recurring transaction ID: {} for user: {} immediately", id, user.getEmail());

    RecurringTransaction recurring = recurringTransactionRepository.findByIdAndUser(id, user)
      .orElseThrow(() -> new ResourceNotFoundException("Recurring transaction not found"));

    TransactionDTO transactionDto = TransactionDTO.builder()
      .amount(recurring.getAmount())
      .type(recurring.getType().name())
      .categoryId(recurring.getCategory().getId())
      .date(LocalDate.now())
      .description(recurring.getDescription() != null ?
        recurring.getDescription() : "Recurring: " + recurring.getName())
      .build();

    transactionService.createTransaction(transactionDto, user);

    recurring.setLastExecutionDate(LocalDate.now());
    LocalDate nextDate = calculateNextExecutionDate(
      LocalDate.now(),
      recurring.getFrequency(),
      recurring.getDayOfMonth(),
      recurring.getDayOfWeek()
    );
    recurring.setNextExecutionDate(nextDate);

    recurringTransactionRepository.save(recurring);
    log.info("Successfully executed recurring transaction ID: {} for user: {}", id, user.getEmail());
  }

}
