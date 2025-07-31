package com.example.financial_tracker.controller;

import com.example.financial_tracker.dto.SavedSearchDTO;
import com.example.financial_tracker.entity.User;
import com.example.financial_tracker.service.SavedSearchService;
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
@RequestMapping("/api/saved-searches")
@RequiredArgsConstructor
public class SavedSearchController {

  private final SavedSearchService savedSearchService;

  @PostMapping
  public ResponseEntity<SavedSearchDTO> createSavedSearch(
    @AuthenticationPrincipal User user,
    @Valid @RequestBody SavedSearchDTO dto,
    HttpServletRequest request) {

    log.info("POST /api/saved-searches - User: {} from IP: {} creating saved search: '{}'",
      user.getEmail(), getClientIpAddress(request), dto.getName());

    SavedSearchDTO created = savedSearchService.createSavedSearch(user, dto);
    return ResponseEntity.status(HttpStatus.CREATED).body(created);
  }

  @GetMapping
  public ResponseEntity<List<SavedSearchDTO>> getUserSavedSearches(
    @AuthenticationPrincipal User user,
    HttpServletRequest request) {

    log.info("GET /api/saved-searches - User: {} from IP: {}",
      user.getEmail(), getClientIpAddress(request));

    List<SavedSearchDTO> searches = savedSearchService.getUserSavedSearches(user);
    return ResponseEntity.ok(searches);
  }

  @GetMapping("/{id}")
  public ResponseEntity<SavedSearchDTO> getSavedSearchById(
    @AuthenticationPrincipal User user,
    @PathVariable Long id,
    HttpServletRequest request) {

    log.info("GET /api/saved-searches/{} - User: {} from IP: {}",
      id, user.getEmail(), getClientIpAddress(request));

    SavedSearchDTO search = savedSearchService.getSavedSearchById(user, id);
    return ResponseEntity.ok(search);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteSavedSearch(
    @AuthenticationPrincipal User user,
    @PathVariable Long id,
    HttpServletRequest request) {

    log.info("DELETE /api/saved-searches/{} - User: {} from IP: {}",
      id, user.getEmail(), getClientIpAddress(request));

    savedSearchService.deleteSavedSearch(user, id);
    return ResponseEntity.noContent().build();
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
