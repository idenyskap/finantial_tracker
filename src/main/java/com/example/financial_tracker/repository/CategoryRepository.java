package com.example.financial_tracker.repository;

import com.example.financial_tracker.entity.Category;
import com.example.financial_tracker.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
  List<Category> findAllByUser(User user);
}
