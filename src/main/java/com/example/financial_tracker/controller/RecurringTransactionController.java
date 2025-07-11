package com.example.financial_tracker.controller;

import com.example.financial_tracker.dto.RecurringTransactionDTO;
import com.example.financial_tracker.entity.User;
import com.example.financial_tracker.service.RecurringTransactionService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/recurring-transactions")
@RequiredArgsConstructor
public class RecurringTransactionController {

  private final RecurringTransactionService recurringTransactionService;

  @PostMapping
  public ResponseEntity<RecurringTransactionDTO> createRecurringTransaction(
    @AuthenticationPrincipal User user,
    @Valid @RequestBody RecurringTransactionDTO dto,
    HttpServletRequest request) {

    log.info("POST /api/recurring-transactions - User: {} creating recurring transaction: '{}'",
      user.getEmail(), dto.getName());

    RecurringTransactionDTO created = recurringTransactionService.createRecurringTransaction(user, dto);
    return ResponseEntity.status(HttpStatus.CREATED).body(created);
  }

  @GetMapping
  public ResponseEntity<List<RecurringTransactionDTO>> getUserRecurringTransactions(
    @AuthenticationPrincipal User user,
    HttpServletRequest request) {

    log.info("GET /api/recurring-transactions - User: {}", user.getEmail());

    List<RecurringTransactionDTO> transactions = recurringTransactionService.getUserRecurringTransactions(user);
    return ResponseEntity.ok(transactions);
  }

  @PutMapping("/{id}")
  public ResponseEntity<RecurringTransactionDTO> updateRecurringTransaction(
    @AuthenticationPrincipal User user,
    @PathVariable Long id,
    @Valid @RequestBody RecurringTransactionDTO dto,
    HttpServletRequest request) {

    log.info("PUT /api/recurring-transactions/{} - User: {}", id, user.getEmail());

    RecurringTransactionDTO updated = recurringTransactionService.updateRecurringTransaction(user, id, dto);
    return ResponseEntity.ok(updated);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteRecurringTransaction(
    @AuthenticationPrincipal User user,
    @PathVariable Long id,
    HttpServletRequest request) {

    log.info("DELETE /api/recurring-transactions/{} - User: {}", id, user.getEmail());

    recurringTransactionService.deleteRecurringTransaction(user, id);
    return ResponseEntity.noContent().build();
  }

  @PostMapping("/{id}/execute")
  public ResponseEntity<Void> executeNow(
    @AuthenticationPrincipal User user,
    @PathVariable Long id,
    HttpServletRequest request) {

    log.info("POST /api/recurring-transactions/{}/execute - User: {} executing transaction now",
      id, user.getEmail());

    recurringTransactionService.executeRecurringTransactionNow(user, id);
    return ResponseEntity.ok().build();
  }
}
