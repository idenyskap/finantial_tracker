package com.example.financial_tracker.service;

import com.example.financial_tracker.dto.TransactionDTO;
import com.example.financial_tracker.entity.Category;
import com.example.financial_tracker.entity.Transaction;
import com.example.financial_tracker.entity.TransactionType;
import com.example.financial_tracker.mapper.TransactionMapper;
import com.example.financial_tracker.repository.CategoryRepository;
import com.example.financial_tracker.repository.TransactionRepository;
import com.example.financial_tracker.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class TransactionService {
  private final TransactionRepository transactionRepository;
  private final TransactionMapper transactionMapper;
  private final CategoryRepository categoryRepository;

  public TransactionService(TransactionRepository transactionRepository, TransactionMapper transactionMapper,
                            CategoryRepository categoryRepository) {
    this.transactionRepository = transactionRepository;
    this.transactionMapper = transactionMapper;
    this.categoryRepository = categoryRepository;
  }

  public List<TransactionDTO> getAllTransactions() {
    return transactionRepository.findAll().stream().map(transactionMapper::toDto).toList();
  }

  public TransactionDTO getTransactionById(Long id) {
    return transactionRepository.findById(id)
      .map(transactionMapper::toDto)
      .orElse(null);
  }

  public TransactionDTO createTransaction(TransactionDTO dto) {
    Transaction entity = transactionMapper.toEntity(dto);

    Category category = categoryRepository.findById(dto.getCategoryId())
      .orElseThrow(() -> new RuntimeException("Category not found"));

    entity.setCategory(category);

    Transaction saved = transactionRepository.save(entity);

    return transactionMapper.toDto(saved);
  }

  public void deleteTransaction(Long id) {
    transactionRepository.deleteById(id);
  }

  public TransactionDTO updateTransaction(Long id, TransactionDTO dto) {
    Transaction existing = transactionRepository.findById(id)
      .orElseThrow(() -> new RuntimeException("Transaction not found"));

    existing.setAmount(dto.getAmount());
    existing.setType(TransactionType.valueOf(dto.getType()));
    existing.setDescription(dto.getDescription());
    existing.setDate(dto.getDate());

    if (dto.getCategoryId() != null) {
      Category category = categoryRepository.findById(dto.getCategoryId())
        .orElseThrow(() -> new RuntimeException("Category not found"));
      existing.setCategory(category);
    }

    Transaction saved = transactionRepository.save(existing);

    return transactionMapper.toDto(saved);
  }

  public BigDecimal getBalance() {
    return transactionRepository.calculateBalance();
  }
}
