package com.example.financial_tracker.repository;

import com.example.financial_tracker.entity.RecurringTransaction;
import com.example.financial_tracker.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface RecurringTransactionRepository extends JpaRepository<RecurringTransaction, Long> {

  List<RecurringTransaction> findByUserOrderByNextExecutionDateAsc(User user);

  Optional<RecurringTransaction> findByIdAndUser(Long id, User user);

  @Query("SELECT rt FROM RecurringTransaction rt " +
    "JOIN FETCH rt.category " +
    "JOIN FETCH rt.user " +
    "WHERE rt.active = true AND rt.nextExecutionDate <= :date")
  List<RecurringTransaction> findDueRecurringTransactions(@Param("date") LocalDate date);

  @Query("SELECT rt FROM RecurringTransaction rt " +
    "JOIN FETCH rt.category " +
    "WHERE rt.user = :user AND rt.active = true")
  List<RecurringTransaction> findActiveByUser(@Param("user") User user);

  @Query("SELECT COUNT(rt) FROM RecurringTransaction rt WHERE rt.user = :user AND rt.active = true")
  Long countActiveByUser(@Param("user") User user);

  @Query("SELECT rt FROM RecurringTransaction rt WHERE rt.user = :user AND rt.active = true AND rt.nextExecutionDate <= :date")
  List<RecurringTransaction> findUpcomingPayments(@Param("user") User user, @Param("date") LocalDate date);

  List<RecurringTransaction> findByUserAndActiveTrue(User user);
}
