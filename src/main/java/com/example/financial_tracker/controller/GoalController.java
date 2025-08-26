package com.example.financial_tracker.controller;

import com.example.financial_tracker.dto.GoalContributionDTO;
import com.example.financial_tracker.dto.GoalDTO;
import com.example.financial_tracker.entity.User;
import com.example.financial_tracker.service.GoalService;
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
@RequestMapping("/api/v1/goals")
@RequiredArgsConstructor
public class GoalController {

  private final GoalService goalService;

  @GetMapping
  public ResponseEntity<List<GoalDTO>> getUserGoals(
    @AuthenticationPrincipal User user,
    @RequestParam(required = false, defaultValue = "false") boolean activeOnly) {

    log.info("GET /api/goals - User: {}, activeOnly: {}", user.getEmail(), activeOnly);

    List<GoalDTO> goals = activeOnly ?
      goalService.getActiveGoals(user) :
      goalService.getUserGoals(user);

    return ResponseEntity.ok(goals);
  }

  @GetMapping("/{id}")
  public ResponseEntity<GoalDTO> getGoal(
    @AuthenticationPrincipal User user,
    @PathVariable Long id) {

    log.info("GET /api/goals/{} - User: {}", id, user.getEmail());

    GoalDTO goal = goalService.getGoalById(user, id);
    return ResponseEntity.ok(goal);
  }

  @PostMapping
  public ResponseEntity<GoalDTO> createGoal(
    @AuthenticationPrincipal User user,
    @Valid @RequestBody GoalDTO dto) {

    log.info("POST /api/goals - User: {} creating goal: '{}'",
      user.getEmail(), dto.getName());

    GoalDTO created = goalService.createGoal(user, dto);
    return ResponseEntity.status(HttpStatus.CREATED).body(created);
  }

  @PutMapping("/{id}")
  public ResponseEntity<GoalDTO> updateGoal(
    @AuthenticationPrincipal User user,
    @PathVariable Long id,
    @Valid @RequestBody GoalDTO dto) {

    log.info("PUT /api/goals/{} - User: {}", id, user.getEmail());

    GoalDTO updated = goalService.updateGoal(user, id, dto);
    return ResponseEntity.ok(updated);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteGoal(
    @AuthenticationPrincipal User user,
    @PathVariable Long id) {

    log.info("DELETE /api/goals/{} - User: {}", id, user.getEmail());

    goalService.deleteGoal(user, id);
    return ResponseEntity.noContent().build();
  }

  @PostMapping("/contribute")
  public ResponseEntity<GoalDTO> contributeToGoal(
    @AuthenticationPrincipal User user,
    @Valid @RequestBody GoalContributionDTO contribution) {

    log.info("POST /api/goals/contribute - User: {} contributing {} to goal ID: {}",
      user.getEmail(), contribution.getAmount(), contribution.getGoalId());

    GoalDTO updated = goalService.contributeToGoal(user, contribution);
    return ResponseEntity.ok(updated);
  }

  @PatchMapping("/{id}/pause")
  public ResponseEntity<Void> pauseGoal(
    @AuthenticationPrincipal User user,
    @PathVariable Long id) {

    log.info("PATCH /api/goals/{}/pause - User: {}", id, user.getEmail());

    goalService.pauseGoal(user, id);
    return ResponseEntity.ok().build();
  }

  @PatchMapping("/{id}/resume")
  public ResponseEntity<Void> resumeGoal(
    @AuthenticationPrincipal User user,
    @PathVariable Long id) {

    log.info("PATCH /api/goals/{}/resume - User: {}", id, user.getEmail());

    goalService.resumeGoal(user, id);
    return ResponseEntity.ok().build();
  }

  @PatchMapping("/{id}/cancel")
  public ResponseEntity<Void> cancelGoal(
    @AuthenticationPrincipal User user,
    @PathVariable Long id) {

    log.info("PATCH /api/goals/{}/cancel - User: {}", id, user.getEmail());

    goalService.cancelGoal(user, id);
    return ResponseEntity.ok().build();
  }
}
