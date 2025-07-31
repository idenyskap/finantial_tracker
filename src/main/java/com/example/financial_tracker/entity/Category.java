package com.example.financial_tracker.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "t_category")
public class Category {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String name;

  private String color;

  @Column(name = "category_type")
  @Enumerated(EnumType.STRING)
  private TransactionType type;

  @ManyToOne
  @JoinColumn(name = "user_id")
  private User user;
}
