package com.example.financial_tracker.repository;

import com.example.financial_tracker.entity.Transaction;
import com.example.financial_tracker.entity.TransactionType;
import com.example.financial_tracker.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long>, JpaSpecificationExecutor<Transaction> {

  // Existing methods...
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

  // FIXED: PostgreSQL-compatible queries using TO_CHAR instead of DATE_FORMAT
  @Query("SELECT " +
    "TO_CHAR(t.date, 'YYYY-MM') as month, " +
    "COALESCE(SUM(CASE WHEN t.type = 'INCOME' THEN t.amount ELSE 0 END), 0) as income, " +
    "COALESCE(SUM(CASE WHEN t.type = 'EXPENSE' THEN t.amount ELSE 0 END), 0) as expense, " +
    "COUNT(t.id) as transactionCount " +
    "FROM Transaction t " +
    "WHERE t.user = :user " +
    "AND t.date >= :startDate " +
    "AND t.date <= :endDate " +
    "GROUP BY TO_CHAR(t.date, 'YYYY-MM') " +
    "ORDER BY month DESC")
  List<Object[]> getMonthlyStatsByUser(@Param("user") User user,
                                       @Param("startDate") LocalDate startDate,
                                       @Param("endDate") LocalDate endDate);

  // FIXED: Remove problematic IS NULL checks and use method-level logic instead
  @Query("SELECT " +
    "t.category.id, " +
    "t.category.name, " +
    "t.category.color, " +
    "SUM(t.amount) as totalAmount, " +
    "COUNT(t.id) as transactionCount, " +
    "AVG(t.amount) as averageAmount " +
    "FROM Transaction t " +
    "WHERE t.user = :user " +
    "AND t.type = 'EXPENSE' " +
    "AND t.date >= :startDate " +
    "AND t.date <= :endDate " +
    "GROUP BY t.category.id, t.category.name, t.category.color " +
    "ORDER BY totalAmount DESC")
  List<Object[]> getExpenseCategoryStats(@Param("user") User user,
                                         @Param("startDate") LocalDate startDate,
                                         @Param("endDate") LocalDate endDate);

  @Query("SELECT " +
    "t.category.id, " +
    "t.category.name, " +
    "t.category.color, " +
    "SUM(t.amount) as totalAmount, " +
    "COUNT(t.id) as transactionCount, " +
    "AVG(t.amount) as averageAmount " +
    "FROM Transaction t " +
    "WHERE t.user = :user " +
    "AND t.type = 'INCOME' " +
    "AND t.date >= :startDate " +
    "AND t.date <= :endDate " +
    "GROUP BY t.category.id, t.category.name, t.category.color " +
    "ORDER BY totalAmount DESC")
  List<Object[]> getIncomeCategoryStats(@Param("user") User user,
                                        @Param("startDate") LocalDate startDate,
                                        @Param("endDate") LocalDate endDate);

  @Query("SELECT " +
    "COALESCE(SUM(CASE WHEN t.type = 'INCOME' THEN t.amount ELSE 0 END), 0) as income, " +
    "COALESCE(SUM(CASE WHEN t.type = 'EXPENSE' THEN t.amount ELSE 0 END), 0) as expense, " +
    "COUNT(t.id) as transactionCount " +
    "FROM Transaction t " +
    "WHERE t.user = :user " +
    "AND t.date >= :startDate " +
    "AND t.date <= :endDate")
  List<Object[]> getPeriodStats(@Param("user") User user,
                                @Param("startDate") LocalDate startDate,
                                @Param("endDate") LocalDate endDate);

  @Query("SELECT t FROM Transaction t " +
    "WHERE t.user = :user " +
    "AND LOWER(t.description) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
    "ORDER BY t.date DESC")
  List<Transaction> searchTransactionsByDescription(@Param("user") User user,
                                                    @Param("searchTerm") String searchTerm);

  @Query("SELECT t FROM Transaction t " +
    "WHERE t.user = :user " +
    "AND EXTRACT(YEAR FROM t.date) = :year " +
    "AND EXTRACT(MONTH FROM t.date) = :month " +
    "ORDER BY t.date DESC")
  List<Transaction> getTransactionsByMonth(@Param("user") User user,
                                           @Param("year") int year,
                                           @Param("month") int month);

  @Query("SELECT t FROM Transaction t " +
    "WHERE t.user = :user " +
    "ORDER BY t.date DESC, t.id DESC")
  List<Transaction> getRecentTransactions(@Param("user") User user, Pageable pageable);

  // Дополнительный метод для поиска по описанию с пагинацией
  @Query("SELECT t FROM Transaction t " +
    "WHERE t.user = :user " +
    "AND LOWER(t.description) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
    "ORDER BY t.date DESC")
  Page<Transaction> searchByDescription(@Param("user") User user,
                                        @Param("searchTerm") String searchTerm,
                                        Pageable pageable);
}
