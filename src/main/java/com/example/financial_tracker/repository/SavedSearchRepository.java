package com.example.financial_tracker.repository;

import com.example.financial_tracker.entity.SavedSearch;
import com.example.financial_tracker.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SavedSearchRepository extends JpaRepository<SavedSearch, Long> {
  List<SavedSearch> findByUserOrderByCreatedAtDesc(User user);
  Optional<SavedSearch> findByIdAndUser(Long id, User user);
  boolean existsByNameAndUser(String name, User user);
}
