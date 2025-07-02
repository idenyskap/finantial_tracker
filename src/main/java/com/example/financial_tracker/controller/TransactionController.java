package com.example.financial_tracker.controller;

import com.example.financial_tracker.dto.TransactionDTO;
import com.example.financial_tracker.entity.TransactionType;
import com.example.financial_tracker.entity.User;
import com.example.financial_tracker.service.TransactionService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/transactions")
@Validated // Enable method-level validation
public class TransactionController {

  private final TransactionService transactionService;

  @GetMapping
  public ResponseEntity<List<TransactionDTO>> getAllTransactions(@AuthenticationPrincipal User user) {
    List<TransactionDTO> transactions = transactionService.getTransactionsByUser(user);
    return ResponseEntity.ok(transactions);
  }

  @GetMapping("/paginated")
  public ResponseEntity<Page<TransactionDTO>> getTransactionsPaginated(
    @AuthenticationPrincipal User user,
    Pageable pageable) {
    Page<TransactionDTO> transactions = transactionService.getTransactionsByUser(user, pageable);
    return ResponseEntity.ok(transactions);
  }

  @GetMapping("/{id}")
  public ResponseEntity<TransactionDTO> getTransactionById(
    @PathVariable @Positive(message = "Transaction ID must be positive") Long id,
    @AuthenticationPrincipal User user) {
    TransactionDTO transaction = transactionService.getTransactionById(id, user);
    if (transaction == null) {
      return ResponseEntity.notFound().build();
    }
    return ResponseEntity.ok(transaction);
  }

  @PostMapping
  public ResponseEntity<TransactionDTO> createTransaction(
    @Valid @RequestBody TransactionDTO dto,
    @AuthenticationPrincipal User user) {
    TransactionDTO created = transactionService.createTransaction(dto, user);
    return ResponseEntity.status(HttpStatus.CREATED).body(created);
  }

  @PutMapping("/{id}")
  public ResponseEntity<TransactionDTO> updateTransaction(
    @PathVariable @Positive(message = "Transaction ID must be positive") Long id,
    @Valid @RequestBody TransactionDTO dto,
    @AuthenticationPrincipal User user) {
    TransactionDTO updated = transactionService.updateTransaction(id, dto, user);
    return ResponseEntity.ok(updated);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteTransaction(
    @PathVariable @Positive(message = "Transaction ID must be positive") Long id,
    @AuthenticationPrincipal User user) {
    transactionService.deleteTransaction(id, user);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/balance")
  public ResponseEntity<BigDecimal> getBalance(@AuthenticationPrincipal User user) {
    BigDecimal balance = transactionService.getBalanceByUser(user);
    return ResponseEntity.ok(balance);
  }

  @GetMapping("/stats")
  public ResponseEntity<Map<String, BigDecimal>> getStats(@AuthenticationPrincipal User user) {
    BigDecimal income = transactionService.getTotalIncomeByUser(user);
    BigDecimal expense = transactionService.getTotalExpenseByUser(user);
    BigDecimal balance = transactionService.getBalanceByUser(user);

    Map<String, BigDecimal> stats = Map.of(
      "income", income,
      "expense", expense,
      "balance", balance
    );

    return ResponseEntity.ok(stats);
  }

  @GetMapping("/filter")
  public ResponseEntity<List<TransactionDTO>> getTransactionsWithFilters(
    @AuthenticationPrincipal User user,
    @RequestParam(required = false) TransactionType type,
    @RequestParam(required = false) @Positive(message = "Category ID must be positive") Long categoryId,
    @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
    @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

    List<TransactionDTO> transactions = transactionService.getTransactionsWithFilters(
      user, type, categoryId, startDate, endDate);
    return ResponseEntity.ok(transactions);
  }

  @GetMapping("/income")
  public ResponseEntity<List<TransactionDTO>> getIncomeTransactions(@AuthenticationPrincipal User user) {
    List<TransactionDTO> transactions = transactionService.getTransactionsByType(user, TransactionType.INCOME);
    return ResponseEntity.ok(transactions);
  }

  @GetMapping("/expense")
  public ResponseEntity<List<TransactionDTO>> getExpenseTransactions(@AuthenticationPrincipal User user) {
    List<TransactionDTO> transactions = transactionService.getTransactionsByType(user, TransactionType.EXPENSE);
    return ResponseEntity.ok(transactions);
  }
}
