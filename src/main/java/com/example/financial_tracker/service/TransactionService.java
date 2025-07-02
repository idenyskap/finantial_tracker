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
    List<Transaction> transactions = transactionRepository.findByUserOrderByDateDesc(user);
    return transactionMapper.toDtoList(transactions);
  }

  public Page<TransactionDTO> getTransactionsByUser(User user, Pageable pageable) {
    Page<Transaction> transactions = transactionRepository.findByUserOrderByDateDesc(user, pageable);
    return transactions.map(transactionMapper::toDto);
  }

  public TransactionDTO getTransactionById(Long id, User user) {
    return transactionRepository.findByIdAndUser(id, user)
      .map(transactionMapper::toDto)
      .orElse(null);
  }

  public TransactionDTO createTransaction(TransactionDTO dto, User user) {
    log.info("Creating transaction with data: {}", dto);

    // Validate transaction type manually if mapper returned null
    if (dto.getType() != null) {
      try {
        TransactionType.valueOf(dto.getType().toUpperCase());
      } catch (IllegalArgumentException e) {
        throw new BusinessLogicException("Invalid transaction type: " + dto.getType() +
          ". Must be 'INCOME' or 'EXPENSE'");
      }
    }

    // Check that category belongs to user
    Category category = categoryRepository.findById(dto.getCategoryId())
      .orElseThrow(() -> new ResourceNotFoundException("Category", "id", dto.getCategoryId()));

    if (!category.getUser().getId().equals(user.getId())) {
      throw new AccessDeniedException("Category does not belong to user");
    }

    Transaction transaction = transactionMapper.toEntity(dto);
    transaction.setUser(user);
    transaction.setCategory(category);

    Transaction saved = transactionRepository.save(transaction);
    return transactionMapper.toDto(saved);
  }

  public TransactionDTO updateTransaction(Long id, TransactionDTO dto, User user) {
    Transaction existing = transactionRepository.findByIdAndUser(id, user)
      .orElseThrow(() -> new RuntimeException("Transaction not found"));

    // Check the category if it has changed
    if (dto.getCategoryId() != null && !dto.getCategoryId().equals(existing.getCategory().getId())) {
      Category category = categoryRepository.findById(dto.getCategoryId())
        .orElseThrow(() -> new RuntimeException("Category not found"));

      if (!category.getUser().getId().equals(user.getId())) {
        throw new RuntimeException("Category does not belong to user");
      }
      existing.setCategory(category);
    }

    existing.setAmount(dto.getAmount());
    existing.setType(TransactionType.valueOf(dto.getType()));
    existing.setDescription(dto.getDescription());
    existing.setDate(dto.getDate());

    Transaction saved = transactionRepository.save(existing);
    return transactionMapper.toDto(saved);
  }

  public void deleteTransaction(Long id, User user) {
    Transaction transaction = transactionRepository.findByIdAndUser(id, user)
      .orElseThrow(() -> new RuntimeException("Transaction not found"));
    transactionRepository.delete(transaction);
  }

  public BigDecimal getBalanceByUser(User user) {
    BigDecimal balance = transactionRepository.calculateBalanceByUser(user);
    return balance != null ? balance : BigDecimal.ZERO;
  }

  public BigDecimal getTotalIncomeByUser(User user) {
    return transactionRepository.getTotalIncomeByUser(user);
  }

  public BigDecimal getTotalExpenseByUser(User user) {
    return transactionRepository.getTotalExpenseByUser(user);
  }

  public List<TransactionDTO> getTransactionsWithFilters(User user,
                                                         TransactionType type,
                                                         Long categoryId,
                                                         LocalDate startDate,
                                                         LocalDate endDate) {
    List<Transaction> transactions = transactionRepository.findByUserWithFilters(
      user, type, categoryId, startDate, endDate);
    return transactionMapper.toDtoList(transactions);
  }

  public List<TransactionDTO> getTransactionsByType(User user, TransactionType type) {
    List<Transaction> transactions = transactionRepository.findByUserAndTypeOrderByDateDesc(user, type);
    return transactionMapper.toDtoList(transactions);
  }
}
