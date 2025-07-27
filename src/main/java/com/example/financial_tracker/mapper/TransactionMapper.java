package com.example.financial_tracker.mapper;

import com.example.financial_tracker.dto.TransactionDTO;
import com.example.financial_tracker.entity.Transaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TransactionMapper {

  @Mapping(source = "category.id", target = "categoryId")
  @Mapping(source = "category.name", target = "categoryName")
  @Mapping(source = "category.color", target = "categoryColor")
  @Mapping(source = "user.id", target = "userId")
  TransactionDTO toDto(Transaction entity);

  @Mapping(target = "user", ignore = true)
  @Mapping(target = "category", ignore = true)
  Transaction toEntity(TransactionDTO dto);

  List<TransactionDTO> toDtoList(List<Transaction> entities);
}
