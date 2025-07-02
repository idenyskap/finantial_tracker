package com.example.financial_tracker.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class CategoryDTO {
  private Long id;

  @NotBlank(message = "Category name is required")
  @Size(min = 2, max = 50, message = "Category name must be between 2 and 50 characters")
  @Pattern(regexp = "^[a-zA-Z0-9\\s\\-_]+$", message = "Category name can only contain letters, numbers, spaces, hyphens and underscores")
  private String name;

  @NotBlank(message = "Color is required")
  @Pattern(regexp = "^#[0-9A-Fa-f]{6}$", message = "Color must be a valid hex color (e.g., #FF5733)")
  private String color;

  // User ID will be set automatically from Authentication
  private Long userId;
}
