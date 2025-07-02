package com.example.financial_tracker.repository;

import com.example.financial_tracker.entity.Transaction;
import com.example.financial_tracker.entity.TransactionType;
import com.example.financial_tracker.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

  List<Transaction> findByUserOrderByDateDesc(User user);

  Page<Transaction> findByUserOrderByDateDesc(User user, Pageable pageable);

  Optional<Transaction> findByIdAndUser(Long id, User user);

  List<Transaction> findByUserAndCategoryNameOrderByDateDesc(User user, String categoryName);

  List<Transaction> findByUserAndTypeOrderByDateDesc(User user, TransactionType type);

  List<Transaction> findByUserAndDateBetweenOrderByDateDesc(User user, LocalDate startDate, LocalDate endDate);


  @Query("SELECT t FROM Transaction t WHERE t.user = :user " +
    "AND (:type IS NULL OR t.type = :type) " +
    "AND (:categoryId IS NULL OR t.category.id = :categoryId) " +
    "AND (:startDate IS NULL OR t.date >= :startDate) " +
    "AND (:endDate IS NULL OR t.date <= :endDate) " +
    "ORDER BY t.date DESC")
  List<Transaction> findByUserWithFilters(@Param("user") User user,
                                          @Param("type") TransactionType type,
                                          @Param("categoryId") Long categoryId,
                                          @Param("startDate") LocalDate startDate,
                                          @Param("endDate") LocalDate endDate);

  @Query("SELECT SUM(CASE WHEN t.type = 'INCOME' THEN t.amount ELSE -t.amount END) " +
    "FROM Transaction t WHERE t.user = :user")
  BigDecimal calculateBalanceByUser(@Param("user") User user);

  @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t " +
    "WHERE t.user = :user AND t.type = 'INCOME'")
  BigDecimal getTotalIncomeByUser(@Param("user") User user);

  @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t " +
    "WHERE t.user = :user AND t.type = 'EXPENSE'")
  BigDecimal getTotalExpenseByUser(@Param("user") User user);

  @Query("SELECT SUM(CASE WHEN t.type = 'INCOME' THEN t.amount ELSE -t.amount END) " +
    "FROM Transaction t WHERE t.user = :user " +
    "AND YEAR(t.date) = :year AND MONTH(t.date) = :month")
  BigDecimal getBalanceByUserAndMonth(@Param("user") User user,
                                      @Param("year") int year,
                                      @Param("month") int month);
}
