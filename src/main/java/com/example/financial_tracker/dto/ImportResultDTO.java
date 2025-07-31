package com.example.financial_tracker.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImportResultDTO {
  private int totalRows;
  private int successfulImports;
  private int failedImports;
  private List<String> errors = new ArrayList<>();
  private List<TransactionDTO> importedTransactions = new ArrayList<>();
}
