package com.example.financial_tracker.controller;

import com.example.financial_tracker.dto.TransactionDTO;
import com.example.financial_tracker.service.TransactionService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/transactions")
public class TransactionController {

  private final TransactionService transactionService;

  @GetMapping
  public ResponseEntity<List<TransactionDTO>> getAllTransactions() {
    List<TransactionDTO> all = transactionService.getAllTransactions();
    return ResponseEntity.ok(all);
  }

  @GetMapping("/{id}")
  public ResponseEntity<TransactionDTO> getTransactionById(@PathVariable Long id) {
    TransactionDTO dto = transactionService.getTransactionById(id);
    if (dto == null) {
      return ResponseEntity.notFound().build();
    }
    return ResponseEntity.ok(dto);
  }

  @PostMapping
  public ResponseEntity<TransactionDTO> createTransaction(@RequestBody TransactionDTO dto) {
    TransactionDTO created = transactionService.createTransaction(dto);
    return ResponseEntity.status(HttpStatus.CREATED).body(created);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteTransaction(@PathVariable Long id) {
    transactionService.deleteTransaction(id);
    return ResponseEntity.noContent().build();
  }

  @PutMapping("/{id}")
  public ResponseEntity<TransactionDTO> updateTransaction(@PathVariable Long id, @RequestBody TransactionDTO dto) {
    TransactionDTO updated = transactionService.updateTransaction(id, dto);
    return ResponseEntity.ok(updated);
  }

  @GetMapping("/balance")
  public ResponseEntity<BigDecimal> getBalance() {
    BigDecimal balance = transactionService.getBalance();
    return ResponseEntity.ok(balance);
  }
}
