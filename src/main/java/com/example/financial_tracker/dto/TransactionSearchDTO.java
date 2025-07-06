package com.example.financial_tracker.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionSearchDTO {

  // Текстовый поиск
  private String searchText;

  // Фильтры по суммам
  private BigDecimal minAmount;
  private BigDecimal maxAmount;

  // Фильтры по датам
  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
  private LocalDate dateFrom;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
  private LocalDate dateTo;

  // Быстрые фильтры по периодам
  private QuickDateFilter quickDateFilter;

  // Фильтры по типу и категориям
  private String type; // "INCOME" или "EXPENSE"
  private List<Long> categoryIds;

  // Пагинация
  @Min(0)
  private Integer page = 0;

  @Positive
  private Integer size = 20;

  // Сортировка
  private String sortBy = "date";
  private SortDirection sortDirection = SortDirection.DESC;

  public enum QuickDateFilter {
    TODAY,
    LAST_7_DAYS,
    LAST_30_DAYS,
    LAST_90_DAYS,
    THIS_MONTH,
    LAST_MONTH,
    THIS_YEAR,
    CUSTOM
  }

  public enum SortDirection {
    ASC, DESC
  }
}
