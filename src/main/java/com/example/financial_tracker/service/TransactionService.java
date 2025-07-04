package com.example.financial_tracker.service;

import com.example.financial_tracker.dto.TransactionDTO;
import com.example.financial_tracker.entity.Category;
import com.example.financial_tracker.entity.Transaction;
import com.example.financial_tracker.entity.TransactionType;
import com.example.financial_tracker.entity.User;
import com.example.financial_tracker.exception.AccessDeniedException;
import com.example.financial_tracker.exception.BusinessLogicException;
import com.example.financial_tracker.exception.ResourceNotFoundException;
import com.example.financial_tracker.mapper.TransactionMapper;
import com.example.financial_tracker.repository.CategoryRepository;
import com.example.financial_tracker.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class TransactionService {

  private final TransactionRepository transactionRepository;
  private final TransactionMapper transactionMapper;
  private final CategoryRepository categoryRepository;

  public List<TransactionDTO> getTransactionsByUser(User user) {
    log.info("Fetching all transactions for user: {} (ID: {})", user.getEmail(), user.getId());

    List<Transaction> transactions = transactionRepository.findByUserOrderByDateDesc(user);

    log.info("Found {} transactions for user: {}", transactions.size(), user.getEmail());
    return transactionMapper.toDtoList(transactions);
  }

  public Page<TransactionDTO> getTransactionsByUser(User user, Pageable pageable) {
    log.info("Fetching paginated transactions for user: {} (page: {}, size: {})",
      user.getEmail(), pageable.getPageNumber(), pageable.getPageSize());

    Page<Transaction> transactions = transactionRepository.findByUserOrderByDateDesc(user, pageable);

    log.info("Retrieved page {} of {} with {} transactions for user: {}",
      transactions.getNumber(), transactions.getTotalPages(),
      transactions.getNumberOfElements(), user.getEmail());

    return transactions.map(transactionMapper::toDto);
  }

  public TransactionDTO getTransactionById(Long id, User user) {
    log.info("Fetching transaction ID: {} for user: {}", id, user.getEmail());

    Transaction transaction = transactionRepository.findByIdAndUser(id, user)
      .orElse(null);

    if (transaction == null) {
      log.warn("Transaction ID: {} not found for user: {}", id, user.getEmail());
      return null;
    }

    log.info("Successfully retrieved transaction ID: {} for user: {}", id, user.getEmail());
    return transactionMapper.toDto(transaction);
  }

  public TransactionDTO createTransaction(TransactionDTO dto, User user) {
    log.info("Creating new transaction for user: {} with data: {}", user.getEmail(),
      maskSensitiveData(dto));

    // Validate transaction type manually if mapper returned null
    if (dto.getType() != null) {
      try {
        TransactionType.valueOf(dto.getType().toUpperCase());
      } catch (IllegalArgumentException e) {
        log.error("Invalid transaction type '{}' provided by user: {}",
          dto.getType(), user.getEmail());
        throw new BusinessLogicException("Invalid transaction type: " + dto.getType() +
          ". Must be 'INCOME' or 'EXPENSE'");
      }
    }

    // Check that category belongs to user
    log.debug("Validating category ID: {} for user: {}", dto.getCategoryId(), user.getEmail());
    Category category = categoryRepository.findById(dto.getCategoryId())
      .orElseThrow(() -> {
        log.error("Category ID: {} not found when creating transaction for user: {}",
          dto.getCategoryId(), user.getEmail());
        return new ResourceNotFoundException("Category", "id", dto.getCategoryId());
      });

    if (!category.getUser().getId().equals(user.getId())) {
      log.warn("User: {} attempted to use category ID: {} belonging to another user",
        user.getEmail(), dto.getCategoryId());
      throw new AccessDeniedException("Category does not belong to user");
    }

    Transaction transaction = transactionMapper.toEntity(dto);
    transaction.setUser(user);
    transaction.setCategory(category);

    Transaction saved = transactionRepository.save(transaction);

    log.info("Successfully created transaction ID: {} for user: {} - Type: {}, Amount: {}, Category: '{}'",
      saved.getId(), user.getEmail(), saved.getType(),
      saved.getAmount(), category.getName());

    return transactionMapper.toDto(saved);
  }

  public TransactionDTO updateTransaction(Long id, TransactionDTO dto, User user) {
    log.info("Updating transaction ID: {} for user: {} with new data: {}",
      id, user.getEmail(), maskSensitiveData(dto));

    Transaction existing = transactionRepository.findByIdAndUser(id, user)
      .orElseThrow(() -> {
        log.error("Transaction ID: {} not found for update by user: {}", id, user.getEmail());
        return new RuntimeException("Transaction not found");
      });

    // Log what's being changed
    log.debug("Original transaction - Amount: {}, Type: {}, Description: '{}'",
      existing.getAmount(), existing.getType(), existing.getDescription());

    // Check category if it's being changed
    if (dto.getCategoryId() != null && !dto.getCategoryId().equals(existing.getCategory().getId())) {
      log.debug("Category being changed from ID: {} to ID: {}",
        existing.getCategory().getId(), dto.getCategoryId());

      Category category = categoryRepository.findById(dto.getCategoryId())
        .orElseThrow(() -> {
          log.error("New category ID: {} not found during update for user: {}",
            dto.getCategoryId(), user.getEmail());
          return new RuntimeException("Category not found");
        });

      if (!category.getUser().getId().equals(user.getId())) {
        log.warn("User: {} attempted to change to category ID: {} belonging to another user",
          user.getEmail(), dto.getCategoryId());
        throw new RuntimeException("Category does not belong to user");
      }
      existing.setCategory(category);
    }

    existing.setAmount(dto.getAmount());
    existing.setType(TransactionType.valueOf(dto.getType()));
    existing.setDescription(dto.getDescription());
    existing.setDate(dto.getDate());

    Transaction saved = transactionRepository.save(existing);

    log.info("Successfully updated transaction ID: {} for user: {} - New values: Type: {}, Amount: {}",
      id, user.getEmail(), saved.getType(), saved.getAmount());

    return transactionMapper.toDto(saved);
  }

  public void deleteTransaction(Long id, User user) {
    log.info("Deleting transaction ID: {} for user: {}", id, user.getEmail());

    Transaction transaction = transactionRepository.findByIdAndUser(id, user)
      .orElseThrow(() -> {
        log.error("Transaction ID: {} not found for deletion by user: {}", id, user.getEmail());
        return new RuntimeException("Transaction not found");
      });

    // Log transaction details before deletion
    log.info("Deleting transaction - Type: {}, Amount: {}, Category: '{}', Date: {}",
      transaction.getType(), transaction.getAmount(),
      transaction.getCategory().getName(), transaction.getDate());

    transactionRepository.delete(transaction);

    log.info("Successfully deleted transaction ID: {} for user: {}", id, user.getEmail());
  }

  public BigDecimal getBalanceByUser(User user) {
    log.debug("Calculating balance for user: {}", user.getEmail());

    BigDecimal balance = transactionRepository.calculateBalanceByUser(user);
    BigDecimal result = balance != null ? balance : BigDecimal.ZERO;

    log.info("Current balance for user: {} is: {}", user.getEmail(), result);
    return result;
  }

  public BigDecimal getTotalIncomeByUser(User user) {
    log.debug("Calculating total income for user: {}", user.getEmail());

    BigDecimal income = transactionRepository.getTotalIncomeByUser(user);

    log.info("Total income for user: {} is: {}", user.getEmail(), income);
    return income;
  }

  public BigDecimal getTotalExpenseByUser(User user) {
    log.debug("Calculating total expenses for user: {}", user.getEmail());

    BigDecimal expense = transactionRepository.getTotalExpenseByUser(user);

    log.info("Total expenses for user: {} is: {}", user.getEmail(), expense);
    return expense;
  }

  public List<TransactionDTO> getTransactionsWithFilters(User user,
                                                         TransactionType type,
                                                         Long categoryId,
                                                         LocalDate startDate,
                                                         LocalDate endDate) {
    log.info("Fetching filtered transactions for user: {} - Type: {}, CategoryID: {}, Date range: {} to {}",
      user.getEmail(), type, categoryId, startDate, endDate);

    List<Transaction> transactions = transactionRepository.findByUserWithFilters(
      user, type, categoryId, startDate, endDate);

    log.info("Found {} transactions matching filters for user: {}",
      transactions.size(), user.getEmail());

    return transactionMapper.toDtoList(transactions);
  }

  public List<TransactionDTO> getTransactionsByType(User user, TransactionType type) {
    log.info("Fetching {} transactions for user: {}", type, user.getEmail());

    List<Transaction> transactions = transactionRepository.findByUserAndTypeOrderByDateDesc(user, type);

    log.info("Found {} {} transactions for user: {}",
      transactions.size(), type, user.getEmail());

    return transactionMapper.toDtoList(transactions);
  }

  // Helper method to mask sensitive data in logs
  private String maskSensitiveData(TransactionDTO dto) {
    if (dto == null) return "null";

    return String.format("TransactionDTO{type='%s', amount=%s, categoryId=%s, date=%s, description='%s'}",
      dto.getType(),
      dto.getAmount(),
      dto.getCategoryId(),
      dto.getDate(),
      dto.getDescription() != null && dto.getDescription().length() > 50 ?
        dto.getDescription().substring(0, 50) + "..." : dto.getDescription());
  }
}
