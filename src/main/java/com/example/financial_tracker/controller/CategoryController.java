package com.example.financial_tracker.controller;

import com.example.financial_tracker.dto.CategoryDTO;
import com.example.financial_tracker.entity.Category;
import com.example.financial_tracker.entity.User;
import com.example.financial_tracker.mapper.CategoryMapper;
import com.example.financial_tracker.service.CategoryService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/categories")
@Validated
public class CategoryController {

  private final CategoryService categoryService;
  private final CategoryMapper categoryMapper;

  @GetMapping
  public ResponseEntity<List<CategoryDTO>> getCategoriesByCurrentUser(@AuthenticationPrincipal User user) {
    List<Category> categories = categoryService.getAllCategories(user);
    return ResponseEntity.ok(categoryMapper.toDtoList(categories));
  }

  @GetMapping("/{id}")
  public ResponseEntity<CategoryDTO> getCategoryById(
    @PathVariable @Positive(message = "Category ID must be positive") Long id) {
    Category category = categoryService.getCategoryById(id);
    CategoryDTO dto = categoryMapper.toDto(category);
    return ResponseEntity.ok(dto);
  }

  @PostMapping
  public ResponseEntity<CategoryDTO> createCategory(
    @Valid @RequestBody CategoryDTO dto,
    @AuthenticationPrincipal User user) {
    dto.setUserId(user.getId());
    CategoryDTO created = categoryService.createCategory(dto);
    return ResponseEntity.status(HttpStatus.CREATED).body(created);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteCategory(
    @PathVariable @Positive(message = "Category ID must be positive") Long id) {
    categoryService.deleteById(id);
    return ResponseEntity.noContent().build();
  }

  @PutMapping("/{id}")
  public ResponseEntity<CategoryDTO> updateCategory(
    @PathVariable @Positive(message = "Category ID must be positive") Long id,
    @Valid @RequestBody CategoryDTO dto,
    @AuthenticationPrincipal User user) {
    dto.setUserId(user.getId());
    CategoryDTO updated = categoryService.updateCategory(id, dto);
    return ResponseEntity.ok(updated);
  }
}
