package com.example.financial_tracker.dto;

import lombok.Data;

@Data
public class CategoryDTO {

  private Long id;
  private String name;
  private String color;
  private Long userId;
}
