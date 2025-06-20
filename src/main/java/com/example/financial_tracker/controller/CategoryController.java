package com.example.financial_tracker.controller;

import com.example.financial_tracker.dto.CategoryDTO;
import com.example.financial_tracker.entity.Category;
import com.example.financial_tracker.entity.User;
import com.example.financial_tracker.mapper.CategoryMapper;
import com.example.financial_tracker.repository.UserRepository;
import com.example.financial_tracker.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/categories")
public class CategoryController {

  private final CategoryService categoryService;
  private final CategoryMapper categoryMapper;
  private final UserRepository userRepository;

  @GetMapping
  public ResponseEntity<List<CategoryDTO>> getCategoriesByUserId(@RequestParam Long userId) {
    User user = userRepository.findById(userId)
      .orElseThrow(() -> new RuntimeException("User not found"));

    List<Category> categories = categoryService.getAllCategories(user);

    List<CategoryDTO> all = categoryMapper.toDtoList(categories);
    return ResponseEntity.ok(all);
  }

  @GetMapping("/{id}")
  public ResponseEntity<CategoryDTO> getCategoryById(@PathVariable Long id) {
    Category category = categoryService.getCategoryById(id);
    CategoryDTO dto = categoryMapper.toDto(category);
    return ResponseEntity.ok(dto);
  }

  @PostMapping
  public ResponseEntity<CategoryDTO> createCategory(@RequestBody CategoryDTO dto) {
    CategoryDTO created = categoryService.createCategory(dto);
    return ResponseEntity.status(HttpStatus.CREATED).body(created);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
    categoryService.deleteById(id);
    return ResponseEntity.noContent().build();
  }

  @PutMapping("/{id}")
  public ResponseEntity<CategoryDTO> updateCategory(@PathVariable Long id, @RequestBody CategoryDTO dto) {

    CategoryDTO update = categoryService.updateCategory(id, dto);
    return ResponseEntity.ok(update);
  }
}
