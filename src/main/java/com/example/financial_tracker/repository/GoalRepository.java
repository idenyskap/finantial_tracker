package com.example.financial_tracker.repository;

import com.example.financial_tracker.entity.Goal;
import com.example.financial_tracker.entity.GoalStatus;
import com.example.financial_tracker.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface GoalRepository extends JpaRepository<Goal, Long> {

  List<Goal> findByUserOrderByPriorityDescTargetDateAsc(User user);

  List<Goal> findByUserAndStatus(User user, GoalStatus status);

  Optional<Goal> findByIdAndUser(Long id, User user);

  @Query("SELECT g FROM Goal g WHERE g.user = :user AND g.targetDate < :date AND g.status = 'ACTIVE'")
  List<Goal> findOverdueGoals(@Param("user") User user, @Param("date") LocalDate date);

  @Query("SELECT COUNT(g) FROM Goal g WHERE g.user = :user AND g.status = :status")
  Long countByUserAndStatus(@Param("user") User user, @Param("status") GoalStatus status);

  @Query("SELECT g FROM Goal g WHERE g.user = :user AND g.status = 'ACTIVE' ORDER BY " +
    "CASE WHEN g.targetDate < CURRENT_DATE THEN 0 ELSE 1 END, " +
    "g.targetDate ASC")
  List<Goal> findActiveGoalsSortedByUrgency(@Param("user") User user);

  @Query("SELECT g FROM Goal g WHERE g.user = :user AND g.status = 'ACTIVE'")
  List<Goal> findActiveByUser(@Param("user") User user);

  List<Goal> findByUser(User user);
}
