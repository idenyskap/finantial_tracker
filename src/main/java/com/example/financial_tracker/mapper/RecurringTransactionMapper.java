package com.example.financial_tracker.mapper;

import com.example.financial_tracker.dto.RecurringTransactionDTO;
import com.example.financial_tracker.entity.RecurringTransaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RecurringTransactionMapper {

  @Mapping(target = "categoryId", source = "category.id")
  @Mapping(target = "categoryName", source = "category.name")
  @Mapping(target = "categoryColor", source = "category.color")
  RecurringTransactionDTO toDto(RecurringTransaction entity);

  @Mapping(target = "category", ignore = true)
  @Mapping(target = "user", ignore = true)
  RecurringTransaction toEntity(RecurringTransactionDTO dto);

  List<RecurringTransactionDTO> toDtoList(List<RecurringTransaction> entities);
}
