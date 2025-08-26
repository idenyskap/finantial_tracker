package com.example.financial_tracker.controller;

import com.example.financial_tracker.dto.BudgetDTO;
import com.example.financial_tracker.entity.User;
import com.example.financial_tracker.service.BudgetService;
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
@RequestMapping("/api/v1/budgets")
@RequiredArgsConstructor
public class BudgetController {

  private final BudgetService budgetService;

  @PostMapping
  public ResponseEntity<BudgetDTO> createBudget(
    @AuthenticationPrincipal User user,
    @Valid @RequestBody BudgetDTO dto,
    HttpServletRequest request) {

    log.info("POST /api/budgets - User: {} from IP: {} creating budget: '{}'",
      user.getEmail(), getClientIpAddress(request), dto.getName());

    BudgetDTO created = budgetService.createBudget(user, dto);
    return ResponseEntity.status(HttpStatus.CREATED).body(created);
  }

  @GetMapping
  public ResponseEntity<List<BudgetDTO>> getUserBudgets(
    @AuthenticationPrincipal User user,
    HttpServletRequest request) {

    log.info("GET /api/budgets - User: {} from IP: {}",
      user.getEmail(), getClientIpAddress(request));

    List<BudgetDTO> budgets = budgetService.getUserBudgets(user);
    return ResponseEntity.ok(budgets);
  }

  @GetMapping("/{id}")
  public ResponseEntity<BudgetDTO> getBudgetById(
    @AuthenticationPrincipal User user,
    @PathVariable Long id,
    HttpServletRequest request) {

    log.info("GET /api/budgets/{} - User: {} from IP: {}",
      id, user.getEmail(), getClientIpAddress(request));

    BudgetDTO budget = budgetService.getBudgetById(user, id);
    return ResponseEntity.ok(budget);
  }

  @PutMapping("/{id}")
  public ResponseEntity<BudgetDTO> updateBudget(
    @AuthenticationPrincipal User user,
    @PathVariable Long id,
    @Valid @RequestBody BudgetDTO dto,
    HttpServletRequest request) {

    log.info("PUT /api/budgets/{} - User: {} from IP: {}",
      id, user.getEmail(), getClientIpAddress(request));

    BudgetDTO updated = budgetService.updateBudget(user, id, dto);
    return ResponseEntity.ok(updated);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteBudget(
    @AuthenticationPrincipal User user,
    @PathVariable Long id,
    HttpServletRequest request) {

    log.info("DELETE /api/budgets/{} - User: {} from IP: {}",
      id, user.getEmail(), getClientIpAddress(request));

    budgetService.deleteBudget(user, id);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/check/{categoryId}")
  public ResponseEntity<BudgetDTO> checkCategoryBudget(
    @AuthenticationPrincipal User user,
    @PathVariable Long categoryId,
    HttpServletRequest request) {

    log.info("GET /api/budgets/check/{} - User: {} from IP: {}",
      categoryId, user.getEmail(), getClientIpAddress(request));

    List<BudgetDTO> budgets = budgetService.getUserBudgets(user);

    BudgetDTO relevantBudget = budgets.stream()
      .filter(b -> b.getCategoryId() != null && b.getCategoryId().equals(categoryId))
      .findFirst()
      .orElse(budgets.stream()
        .filter(b -> b.getCategoryId() == null)
        .findFirst()
        .orElse(null));

    if (relevantBudget == null) {
      return ResponseEntity.noContent().build();
    }

    return ResponseEntity.ok(relevantBudget);
  }

  private String getClientIpAddress(HttpServletRequest request) {
    String xForwardedFor = request.getHeader("X-Forwarded-For");
    if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
      return xForwardedFor.split(",")[0].trim();
    }

    String xRealIp = request.getHeader("X-Real-IP");
    if (xRealIp != null && !xRealIp.isEmpty()) {
      return xRealIp;
    }

    return request.getRemoteAddr();
  }
}
