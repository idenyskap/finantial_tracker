package com.example.financial_tracker.controller;

import com.example.financial_tracker.dto.CategoryDTO;
import com.example.financial_tracker.entity.Category;
import com.example.financial_tracker.entity.User;
import com.example.financial_tracker.mapper.CategoryMapper;
import com.example.financial_tracker.service.CategoryService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/categories")
@Validated
public class CategoryController {

  private final CategoryService categoryService;
  private final CategoryMapper categoryMapper;

  @GetMapping
  public ResponseEntity<List<CategoryDTO>> getCategoriesByCurrentUser(
    @AuthenticationPrincipal User user,
    HttpServletRequest request) {

    log.info("GET /api/categories - User: {} from IP: {}",
      user.getEmail(), getClientIpAddress(request));

    List<Category> categories = categoryService.getAllCategories(user);
    return ResponseEntity.ok(categoryMapper.toDtoList(categories));
  }

  @GetMapping("/{id}")
  public ResponseEntity<CategoryDTO> getCategoryById(
    @PathVariable @Positive(message = "Category ID must be positive") Long id,
    @AuthenticationPrincipal User user,
    HttpServletRequest request) {

    log.info("GET /api/categories/{} - User: {} from IP: {}",
      id, user.getEmail(), getClientIpAddress(request));

    CategoryDTO category = categoryService.getCategoryById(id, user);
    return ResponseEntity.ok(category);
  }

  @PostMapping
  public ResponseEntity<CategoryDTO> createCategory(
    @Valid @RequestBody CategoryDTO dto,
    @AuthenticationPrincipal User user,
    HttpServletRequest request) {

    log.info("POST /api/categories - User: {} from IP: {} creating category: '{}'",
      user.getEmail(), getClientIpAddress(request), dto.getName());

    CategoryDTO created = categoryService.createCategory(dto, user);
    return ResponseEntity.status(HttpStatus.CREATED).body(created);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteCategory(
    @PathVariable @Positive(message = "Category ID must be positive") Long id,
    @AuthenticationPrincipal User user,
    HttpServletRequest request) {

    log.info("DELETE /api/categories/{} - User: {} from IP: {}",
      id, user.getEmail(), getClientIpAddress(request));

    categoryService.deleteById(id, user);
    return ResponseEntity.noContent().build();
  }

  @PutMapping("/{id}")
  public ResponseEntity<CategoryDTO> updateCategory(
    @PathVariable @Positive(message = "Category ID must be positive") Long id,
    @Valid @RequestBody CategoryDTO dto,
    @AuthenticationPrincipal User user,
    HttpServletRequest request) {

    log.info("PUT /api/categories/{} - User: {} from IP: {}",
      id, user.getEmail(), getClientIpAddress(request));

    CategoryDTO updated = categoryService.updateCategory(id, dto, user);
    return ResponseEntity.ok(updated);
  }

  private String getClientIpAddress(HttpServletRequest request) {
    String xForwardedFor = request.getHeader("X-Forwarded-For");
    if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
      return xForwardedFor.split(",")[0].trim();
    }

    String xRealIp = request.getHeader("X-Real-IP");
    if (xRealIp != null && !xRealIp.isEmpty()) {
      return xRealIp;
    }

    return request.getRemoteAddr();
  }
}
