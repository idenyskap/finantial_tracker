package com.example.financial_tracker.mapper;

import com.example.financial_tracker.dto.BudgetDTO;
import com.example.financial_tracker.entity.Budget;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface BudgetMapper {

  @Mapping(source = "category.id", target = "categoryId")
  @Mapping(source = "category.name", target = "categoryName")
  @Mapping(source = "category.color", target = "categoryColor")
  BudgetDTO toDto(Budget entity);

  @Mapping(target = "user", ignore = true)
  @Mapping(target = "category", ignore = true)
  @Mapping(target = "startDate", ignore = true)
  @Mapping(target = "endDate", ignore = true)
  Budget toEntity(BudgetDTO dto);

  List<BudgetDTO> toDtoList(List<Budget> entities);
}
