package com.example.financial_tracker.service;

import com.example.financial_tracker.dto.CategoryDTO;
import com.example.financial_tracker.entity.Category;
import com.example.financial_tracker.entity.User;
import com.example.financial_tracker.mapper.CategoryMapper;
import com.example.financial_tracker.repository.CategoryRepository;
import com.example.financial_tracker.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {

  private final CategoryRepository categoryRepository;
  private final CategoryMapper categoryMapper;
  private final UserRepository userRepository;

  public CategoryService(CategoryRepository categoryRepository, CategoryMapper categoryMapper, UserRepository userRepository) {
    this.categoryRepository = categoryRepository;
    this.categoryMapper = categoryMapper;
    this.userRepository = userRepository;
  }

  public List<Category> getAllCategories(User user) {
    return categoryRepository.findAllByUser(user);
  }

  public CategoryDTO createCategory(CategoryDTO dto) {
    Category entity = categoryMapper.toEntity(dto);

    User user = userRepository.findById(dto.getUserId())
      .orElseThrow(() -> new RuntimeException("User not found"));
    entity.setUser(user);

    Category saved = categoryRepository.save(entity);

    return categoryMapper.toDto(saved);
  }

  public Category getCategoryById(Long id) {
    return categoryRepository.findById(id)
      .orElseThrow(() -> new RuntimeException("Category not found"));
  }

  public void deleteById(Long id) {
    categoryRepository.deleteById(id);
  }

  public CategoryDTO updateCategory(Long categoryId, CategoryDTO dto) {
    Category existing = categoryRepository.findById(categoryId)
      .orElseThrow(() -> new RuntimeException("Category not found"));

    existing.setName(dto.getName());
    existing.setColor(dto.getColor());

    if (dto.getUserId() != null) {
      User user = userRepository.findById(dto.getUserId())
        .orElseThrow(() -> new RuntimeException("User not found"));
      existing.setUser(user);
    }

    Category saved = categoryRepository.save(existing);

    return categoryMapper.toDto(saved);

  }
}
