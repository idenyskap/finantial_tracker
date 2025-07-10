package com.example.financial_tracker.repository;

import com.example.financial_tracker.entity.Category;
import com.example.financial_tracker.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
  List<Category> findByUserOrderByNameAsc(User user);
  Optional<Category> findByIdAndUser(Long id, User user);
  Optional<Category> findByNameAndUser(String name, User user); // Добавьте эту строку
}
