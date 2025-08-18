package com.example.financial_tracker.controller;

import com.example.financial_tracker.dto.DashboardDTO;
import com.example.financial_tracker.entity.User;
import com.example.financial_tracker.service.DashboardService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
public class DashboardController {

  private final DashboardService dashboardService;

  @GetMapping
  public ResponseEntity<DashboardDTO> getDashboard(
    @AuthenticationPrincipal User user,
    HttpServletRequest request) {

    log.info("GET /api/dashboard - User: {} from IP: {}",
      user.getEmail(), getClientIpAddress(request));

    DashboardDTO dashboard = dashboardService.getDashboard(user);

    log.info("Generated dashboard for user: {} - Balance: {}, Monthly Income: {}, Monthly Expense: {}",
      user.getEmail(), dashboard.getCurrentBalance(),
      dashboard.getMonthlyIncome(), dashboard.getMonthlyExpense());

    return ResponseEntity.ok(dashboard);
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
