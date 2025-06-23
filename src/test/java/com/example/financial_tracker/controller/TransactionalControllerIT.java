package com.example.financial_tracker.controller;

import com.example.financial_tracker.dto.TransactionDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class TransactionalControllerIT {

  @Autowired
  MockMvc mockMvc;

  @Autowired
  ObjectMapper objectMapper;

  @Test
  void testGetAllTransactions() throws Exception {
    mockMvc.perform(get("/api/transactions"))
      .andExpect(status().isOk())
      .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
  }

  @Test
  void testGetTransactionById() throws Exception {
    mockMvc.perform(get("/api/transactions/1"))
      .andExpect(status().isOk())
      .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
  }

  @Test
  void testCreateTransaction() throws Exception {
    TransactionDTO transactionDTO = new TransactionDTO();
    transactionDTO.setAmount(BigDecimal.valueOf(10.00));
    transactionDTO.setType("EXPENSE");
    transactionDTO.setCategoryId(1L);
    transactionDTO.setCategoryName("Food");
    transactionDTO.setDate(LocalDate.of(2025, 6, 18));
    transactionDTO.setDescription("Phone");

    mockMvc.perform(post("/api/transactions")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(transactionDTO)))
      .andExpect(status().isCreated())
      .andExpect(jsonPath("$.amount").value(BigDecimal.valueOf(10.00)))
      .andExpect(jsonPath("$.type").value("EXPENSE"))
      .andExpect(jsonPath("$.categoryId").value(1L))
      .andExpect(jsonPath("$.categoryName").value("Food"))
      .andExpect(jsonPath("$.date").value("2025-06-18"))
      .andExpect(jsonPath("$.description").value("Phone"));
  }

  @Test
  void testUpdateTransaction() throws Exception {
    TransactionDTO transactionDTO = new TransactionDTO();
    transactionDTO.setAmount(BigDecimal.valueOf(99.99));
    transactionDTO.setType("EXPENSE");
    transactionDTO.setCategoryId(1L);
    transactionDTO.setCategoryName("Food");
    transactionDTO.setDate(LocalDate.of(2025, 6, 20));
    transactionDTO.setDescription("Updated description");

    mockMvc.perform(put("/api/transactions/1") // предполагается, что транзакция с ID 1 существует
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(transactionDTO)))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.amount").value(99.99))
      .andExpect(jsonPath("$.type").value("EXPENSE"))
      .andExpect(jsonPath("$.categoryId").value(1))
      .andExpect(jsonPath("$.categoryName").value("Food"))
      .andExpect(jsonPath("$.date").value("2025-06-20"))
      .andExpect(jsonPath("$.description").value("Updated description"));
  }

  @Test
  void testDeleteTransaction() throws Exception {
    mockMvc.perform(delete("/api/transactions/2"))
      .andExpect(status().isNoContent());
  }

  @Test
  void testGetBalance() throws Exception {
    mockMvc.perform(get("/api/transactions/balance"))
      .andExpect(status().isOk())
      .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
  }
}
