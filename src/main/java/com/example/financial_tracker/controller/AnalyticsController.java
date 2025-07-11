package com.example.financial_tracker.controller;

import com.example.financial_tracker.dto.*;
import com.example.financial_tracker.entity.User;
import com.example.financial_tracker.service.AnalyticsService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/analytics")
public class AnalyticsController {

  private final AnalyticsService analyticsService;

  @GetMapping("/full")
  public ResponseEntity<AnalyticsDTO> getFullAnalytics(
    @AuthenticationPrincipal User user,
    @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
    @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
    HttpServletRequest request) {

    log.info("GET /api/analytics/full - User: {} from IP: {} - Date range: {} to {}",
      user.getEmail(), getClientIpAddress(request), startDate, endDate);

    if (startDate == null) {
      startDate = LocalDate.now().minusMonths(12);
    }
    if (endDate == null) {
      endDate = LocalDate.now();
    }

    AnalyticsDTO analytics = analyticsService.getFullAnalytics(user, startDate, endDate);

    log.info("Generated full analytics for user: {} with {} monthly entries",
      user.getEmail(), analytics.getMonthlyStats().size());

    return ResponseEntity.ok(analytics);
  }

  @GetMapping("/monthly")
  public ResponseEntity<List<MonthlyStatsDTO>> getMonthlyStats(
    @AuthenticationPrincipal User user,
    @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
    @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
    HttpServletRequest request) {

    log.info("GET /api/analytics/monthly - User: {} from IP: {}",
      user.getEmail(), getClientIpAddress(request));

    if (startDate == null) {
      startDate = LocalDate.now().minusMonths(12);
    }
    if (endDate == null) {
      endDate = LocalDate.now();
    }

    List<MonthlyStatsDTO> monthlyStats = analyticsService.getMonthlyStats(user, startDate, endDate);
    return ResponseEntity.ok(monthlyStats);
  }

  @GetMapping("/categories/expenses")
  public ResponseEntity<List<CategoryStatsDTO>> getTopExpenseCategories(
    @AuthenticationPrincipal User user,
    @RequestParam(defaultValue = "10") int limit,
    @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
    @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
    HttpServletRequest request) {

    log.info("GET /api/analytics/categories/expenses - User: {} from IP: {} - Limit: {}",
      user.getEmail(), getClientIpAddress(request), limit);

    List<CategoryStatsDTO> categories = analyticsService.getTopExpenseCategories(user, startDate, endDate, limit);
    return ResponseEntity.ok(categories);
  }

  @GetMapping("/categories/income")
  public ResponseEntity<List<CategoryStatsDTO>> getTopIncomeCategories(
    @AuthenticationPrincipal User user,
    @RequestParam(defaultValue = "10") int limit,
    @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
    @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
    HttpServletRequest request) {

    log.info("GET /api/analytics/categories/income - User: {} from IP: {} - Limit: {}",
      user.getEmail(), getClientIpAddress(request), limit);

    List<CategoryStatsDTO> categories = analyticsService.getTopIncomeCategories(user, startDate, endDate, limit);
    return ResponseEntity.ok(categories);
  }

  @GetMapping("/comparison")
  public ResponseEntity<ComparisonStatsDTO> getComparisonStats(
    @AuthenticationPrincipal User user,
    HttpServletRequest request) {

    log.info("GET /api/analytics/comparison - User: {} from IP: {}",
      user.getEmail(), getClientIpAddress(request));

    ComparisonStatsDTO comparison = analyticsService.getComparisonStats(user);
    return ResponseEntity.ok(comparison);
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
