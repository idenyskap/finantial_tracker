package com.example.financial_tracker.repository;

import com.example.financial_tracker.entity.Budget;
import com.example.financial_tracker.entity.Category;
import com.example.financial_tracker.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BudgetRepository extends JpaRepository<Budget, Long> {
  List<Budget> findByUserAndActiveOrderByCreatedAtDesc(User user, boolean active);

  Optional<Budget> findByIdAndUser(Long id, User user);

  @Query("SELECT b FROM Budget b WHERE b.user = :user " +
    "AND b.active = true " +
    "AND (b.category = :category OR b.category IS NULL) " +
    "ORDER BY b.category DESC") // сначала специфичные для категории
  List<Budget> findActiveBudgetsForCategory(@Param("user") User user,
                                            @Param("category") Category category);

  boolean existsByUserAndCategoryAndActiveTrue(User user, Category category);
}
