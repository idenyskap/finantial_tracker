package com.example.financial_tracker.mapper;

import com.example.financial_tracker.dto.TransactionDTO;
import com.example.financial_tracker.entity.Transaction;
import com.example.financial_tracker.entity.TransactionType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TransactionMapper {

  @Mapping(source = "category.id", target = "categoryId")
  @Mapping(source = "category.name", target = "categoryName")
  @Mapping(source = "category.color", target = "categoryColor")
  @Mapping(source = "user.id", target = "userId")
  @Mapping(source = "type", target = "type", qualifiedByName = "transactionTypeToString")
  TransactionDTO toDto(Transaction transaction);

  @Mapping(source = "categoryId", target = "category.id")
  @Mapping(source = "userId", target = "user.id")
  @Mapping(source = "type", target = "type", qualifiedByName = "stringToTransactionType")
  Transaction toEntity(TransactionDTO transactionDTO);

  List<TransactionDTO> toDtoList(List<Transaction> transactions);

  @Named("transactionTypeToString")
  default String transactionTypeToString(TransactionType type) {
    return type != null ? type.name() : null;
  }

  @Named("stringToTransactionType")
  default TransactionType stringToTransactionType(String type) {
    if (type == null || type.trim().isEmpty()) {
      return null;
    }

    try {
      return TransactionType.valueOf(type.trim().toUpperCase());
    } catch (IllegalArgumentException e) {
      return null;
    }
  }
}
