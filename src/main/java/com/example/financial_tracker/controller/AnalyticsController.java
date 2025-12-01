package com.example.financial_tracker.controller;

import com.example.financial_tracker.dto.*;
import com.example.financial_tracker.entity.User;
import com.example.financial_tracker.service.AnalyticsService;
import com.example.financial_tracker.util.RequestUtils;
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
@RequestMapping("/api/v1/analytics")
public class AnalyticsController {

  private final AnalyticsService analyticsService;

  @GetMapping("/full")
  public ResponseEntity<AnalyticsDTO> getFullAnalytics(
    @AuthenticationPrincipal User user,
    @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
    @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
    HttpServletRequest request) {

    log.info("GET /api/analytics/full - User: {} from IP: {} - Date range: {} to {}",
      user.getEmail(), RequestUtils.getClientIpAddress(request), startDate, endDate);

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
      user.getEmail(), RequestUtils.getClientIpAddress(request));

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
      user.getEmail(), RequestUtils.getClientIpAddress(request), limit);

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
      user.getEmail(), RequestUtils.getClientIpAddress(request), limit);

    List<CategoryStatsDTO> categories = analyticsService.getTopIncomeCategories(user, startDate, endDate, limit);
    return ResponseEntity.ok(categories);
  }

  @GetMapping("/comparison")
  public ResponseEntity<ComparisonStatsDTO> getComparisonStats(
    @AuthenticationPrincipal User user,
    HttpServletRequest request) {

    log.info("GET /api/analytics/comparison - User: {} from IP: {}",
      user.getEmail(), RequestUtils.getClientIpAddress(request));

    ComparisonStatsDTO comparison = analyticsService.getComparisonStats(user);
    return ResponseEntity.ok(comparison);
  }

  @GetMapping("/categories/monthly")
  public ResponseEntity<List<CategoryMonthlyStatsDTO>> getCategoryMonthlyStats(
    @AuthenticationPrincipal User user,
    @RequestParam(defaultValue = "10") int limit,
    @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
    @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
    HttpServletRequest request) {

    log.info("GET /api/analytics/categories/monthly - User: {} from IP: {} - Limit: {}",
      user.getEmail(), RequestUtils.getClientIpAddress(request), limit);

    List<CategoryMonthlyStatsDTO> categoryStats = analyticsService.getCategoryMonthlyStats(user, startDate, endDate, limit);
    return ResponseEntity.ok(categoryStats);
  }
}
