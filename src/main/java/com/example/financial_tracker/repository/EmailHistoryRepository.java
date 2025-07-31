package com.example.financial_tracker.repository;

import com.example.financial_tracker.entity.EmailHistory;
import com.example.financial_tracker.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmailHistoryRepository extends JpaRepository<EmailHistory, Long> {

  List<EmailHistory> findByUserOrderByCreatedAtDesc(User user);

}
