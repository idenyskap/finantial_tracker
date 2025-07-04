package com.example.financial_tracker.service;

import com.example.financial_tracker.dto.CategoryDTO;
import com.example.financial_tracker.entity.Category;
import com.example.financial_tracker.entity.User;
import com.example.financial_tracker.mapper.CategoryMapper;
import com.example.financial_tracker.repository.CategoryRepository;
import com.example.financial_tracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CategoryService {

  private final CategoryRepository categoryRepository;
  private final CategoryMapper categoryMapper;
  private final UserRepository userRepository;

  public List<Category> getAllCategories(User user) {
    log.info("Fetching all categories for user: {} (ID: {})", user.getEmail(), user.getId());

    List<Category> categories = categoryRepository.findAllByUser(user);

    log.info("Found {} categories for user: {}", categories.size(), user.getEmail());
    return categories;
  }

  public CategoryDTO createCategory(CategoryDTO dto) {
    log.info("Creating new category '{}' with color '{}' for user ID: {}",
      dto.getName(), dto.getColor(), dto.getUserId());

    Category entity = categoryMapper.toEntity(dto);

    User user = userRepository.findById(dto.getUserId())
      .orElseThrow(() -> {
        log.error("User ID: {} not found when creating category '{}'",
          dto.getUserId(), dto.getName());
        return new RuntimeException("User not found");
      });

    entity.setUser(user);
    Category saved = categoryRepository.save(entity);

    log.info("Successfully created category ID: {} ('{}') for user: {}",
      saved.getId(), saved.getName(), user.getEmail());

    return categoryMapper.toDto(saved);
  }

  public Category getCategoryById(Long id) {
    log.debug("Fetching category by ID: {}", id);

    Category category = categoryRepository.findById(id)
      .orElseThrow(() -> {
        log.error("Category ID: {} not found", id);
        return new RuntimeException("Category not found");
      });

    log.debug("Found category ID: {} - Name: '{}', User: {}",
      id, category.getName(), category.getUser().getEmail());

    return category;
  }

  public void deleteById(Long id) {
    log.info("Deleting category ID: {}", id);

    Category category = categoryRepository.findById(id)
      .orElseThrow(() -> {
        log.error("Category ID: {} not found for deletion", id);
        return new RuntimeException("Category not found");
      });

    log.info("Deleting category '{}' (ID: {}) belonging to user: {}",
      category.getName(), id, category.getUser().getEmail());

    categoryRepository.deleteById(id);

    log.info("Successfully deleted category ID: {}", id);
  }

  public CategoryDTO updateCategory(Long categoryId, CategoryDTO dto) {
    log.info("Updating category ID: {} with new data: Name='{}', Color='{}'",
      categoryId, dto.getName(), dto.getColor());

    Category existing = categoryRepository.findById(categoryId)
      .orElseThrow(() -> {
        log.error("Category ID: {} not found for update", categoryId);
        return new RuntimeException("Category not found");
      });

    log.debug("Original category - Name: '{}', Color: '{}'",
      existing.getName(), existing.getColor());

    existing.setName(dto.getName());
    existing.setColor(dto.getColor());

    if (dto.getUserId() != null) {
      User user = userRepository.findById(dto.getUserId())
        .orElseThrow(() -> {
          log.error("User ID: {} not found during category update", dto.getUserId());
          return new RuntimeException("User not found");
        });
      existing.setUser(user);
    }

    Category saved = categoryRepository.save(existing);

    log.info("Successfully updated category ID: {} - New name: '{}', New color: '{}'",
      categoryId, saved.getName(), saved.getColor());

    return categoryMapper.toDto(saved);
  }
}
