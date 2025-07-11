package com.example.financial_tracker.service;

import com.example.financial_tracker.dto.CategoryDTO;
import com.example.financial_tracker.entity.Category;
import com.example.financial_tracker.entity.User;
import com.example.financial_tracker.exception.AccessDeniedException;
import com.example.financial_tracker.exception.ResourceNotFoundException;
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

  public List<Category> getAllCategories(User user) {
    log.info("Fetching all categories for user: {} (ID: {})", user.getEmail(), user.getId());

    List<Category> categories = categoryRepository.findByUserOrderByNameAsc(user); // Изменено

    log.info("Found {} categories for user: {}", categories.size(), user.getEmail());
    return categories;
  }

  public CategoryDTO createCategory(CategoryDTO dto, User user) {
    log.info("Creating new category '{}' with color '{}' for user: {}",
      dto.getName(), dto.getColor(), user.getEmail());

    Category entity = categoryMapper.toEntity(dto);
    entity.setUser(user);

    Category saved = categoryRepository.save(entity);

    log.info("Successfully created category ID: {} ('{}') for user: {}",
      saved.getId(), saved.getName(), user.getEmail());

    return categoryMapper.toDto(saved);
  }

  public CategoryDTO getCategoryById(Long id, User user) {
    log.debug("Fetching category ID: {} for user: {}", id, user.getEmail());

    Category category = categoryRepository.findById(id)
      .orElseThrow(() -> {
        log.error("Category ID: {} not found", id);
        return new ResourceNotFoundException("Category", "id", id);
      });

    if (!category.getUser().getId().equals(user.getId())) {
      log.warn("User: {} attempted to access category ID: {} belonging to user: {}",
        user.getEmail(), id, category.getUser().getEmail());
      throw new AccessDeniedException("You don't have permission to access this category");
    }

    log.debug("Found category ID: {} - Name: '{}' for user: {}",
      id, category.getName(), user.getEmail());

    return categoryMapper.toDto(category);
  }

  public void deleteById(Long id, User user) {
    log.info("Deleting category ID: {} for user: {}", id, user.getEmail());

    Category category = categoryRepository.findById(id)
      .orElseThrow(() -> {
        log.error("Category ID: {} not found for deletion", id);
        return new ResourceNotFoundException("Category", "id", id);
      });

    if (!category.getUser().getId().equals(user.getId())) {
      log.warn("User: {} attempted to delete category ID: {} belonging to user: {}",
        user.getEmail(), id, category.getUser().getEmail());
      throw new AccessDeniedException("You don't have permission to delete this category");
    }

    log.info("Deleting category '{}' (ID: {}) for user: {}",
      category.getName(), id, user.getEmail());

    categoryRepository.delete(category);

    log.info("Successfully deleted category ID: {}", id);
  }

  public CategoryDTO updateCategory(Long categoryId, CategoryDTO dto, User user) {
    log.info("Updating category ID: {} for user: {} with new data: Name='{}', Color='{}'",
      categoryId, user.getEmail(), dto.getName(), dto.getColor());

    Category existing = categoryRepository.findById(categoryId)
      .orElseThrow(() -> {
        log.error("Category ID: {} not found for update", categoryId);
        return new ResourceNotFoundException("Category", "id", categoryId);
      });

    if (!existing.getUser().getId().equals(user.getId())) {
      log.warn("User: {} attempted to update category ID: {} belonging to user: {}",
        user.getEmail(), categoryId, existing.getUser().getEmail());
      throw new AccessDeniedException("You don't have permission to update this category");
    }

    log.debug("Original category - Name: '{}', Color: '{}'",
      existing.getName(), existing.getColor());

    existing.setName(dto.getName());
    existing.setColor(dto.getColor());

    Category saved = categoryRepository.save(existing);

    log.info("Successfully updated category ID: {} - New name: '{}', New color: '{}'",
      categoryId, saved.getName(), saved.getColor());

    return categoryMapper.toDto(saved);
  }
}
