package com.example.financial_tracker.repository;

import com.example.financial_tracker.entity.EmailQuota;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.Optional;

public interface EmailQuotaRepository extends JpaRepository<EmailQuota, Long> {
  Optional<EmailQuota> findByDate(LocalDate date);

  @Query("SELECT COALESCE(SUM(e.dailyCount), 0) FROM EmailQuota e WHERE e.date >= :startDate AND e.date <= :endDate")
  Integer getMonthlyCount(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}
