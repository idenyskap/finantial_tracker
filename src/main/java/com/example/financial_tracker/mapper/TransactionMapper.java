package com.example.financial_tracker.mapper;

import com.example.financial_tracker.dto.TransactionDTO;
import com.example.financial_tracker.entity.Transaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TransactionMapper {

  @Mapping(source = "category.id", target = "categoryId")
  @Mapping(source = "category.name", target = "categoryName")
  @Mapping(source = "type", target = "type")
  TransactionDTO toDto(Transaction transaction);

  @Mapping(source = "categoryId", target = "category.id")
  @Mapping(source = "type", target = "type")
  Transaction toEntity(TransactionDTO transactionDTO);
}
