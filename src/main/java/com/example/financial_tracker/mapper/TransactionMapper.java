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
  @Mapping(source = "type", target = "type")
  TransactionDTO toDto(Transaction transaction);

  @Mapping(source = "categoryId", target = "category.id")
  @Mapping(source = "userId", target = "user.id")
  @Mapping(source = "type", target = "type")
  Transaction toEntity(TransactionDTO transactionDTO);

  List<TransactionDTO> toDtoList(List<Transaction> transactions);
}
