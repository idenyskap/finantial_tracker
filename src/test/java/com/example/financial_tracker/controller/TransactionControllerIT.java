package com.example.financial_tracker.controller;

import com.example.financial_tracker.dto.TransactionDTO;
import com.example.financial_tracker.enumerations.TransactionType;
import com.example.financial_tracker.entity.User;
import com.example.financial_tracker.enumerations.Role;
import com.example.financial_tracker.service.TransactionService;
import com.example.financial_tracker.service.SavedSearchService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
class TransactionControllerIT {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockBean
  private TransactionService transactionService;

  @MockBean
  private SavedSearchService savedSearchService;

  private User createTestUser() {
    User user = new User();
    user.setId(1L);
    user.setEmail("test@example.com");
    user.setName("Test User");
    user.setRole(Role.USER);
    return user;
  }

  private TransactionDTO createTestTransaction() {
    TransactionDTO dto = new TransactionDTO();
    dto.setId(1L);
    dto.setAmount(BigDecimal.valueOf(100.00));
    dto.setDescription("Test transaction");
    dto.setType("EXPENSE");
    dto.setDate(LocalDate.now());
    dto.setCategoryId(1L);
    dto.setCategoryName("Food");
    return dto;
  }

  @Test
  @WithMockUser(username = "test@example.com")
  void testGetAllTransactions_Success() throws Exception {
    User user = createTestUser();
    List<TransactionDTO> transactions = List.of(createTestTransaction());
    
    when(transactionService.getTransactionsByUser(any(User.class)))
        .thenReturn(transactions);

    mockMvc.perform(get("/api/v1/transactions")
        .with(user(user))
        .accept(MediaType.APPLICATION_JSON))
      .andExpect(status().isOk())
      .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
      .andExpect(jsonPath("$").isArray())
      .andExpect(jsonPath("$[0].id").value(1))
      .andExpect(jsonPath("$[0].amount").value(100.00))
      .andExpect(jsonPath("$[0].description").value("Test transaction"));
  }

  @Test
  void testGetAllTransactions_Unauthorized() throws Exception {
    mockMvc.perform(get("/api/v1/transactions")
        .accept(MediaType.APPLICATION_JSON))
      .andExpect(status().isForbidden());
  }

  @Test
  @WithMockUser(username = "test@example.com")
  void testGetTransactionById_Success() throws Exception {
    User user = createTestUser();
    TransactionDTO transaction = createTestTransaction();
    
    when(transactionService.getTransactionById(eq(1L), any(User.class)))
        .thenReturn(transaction);

    mockMvc.perform(get("/api/v1/transactions/1")
        .with(user(user)))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.id").value(1))
      .andExpect(jsonPath("$.amount").value(100.00));
  }

  @Test
  @WithMockUser(username = "test@example.com")
  void testGetTransactionById_NotFound() throws Exception {
    User user = createTestUser();

    when(transactionService.getTransactionById(eq(999L), any(User.class)))
        .thenThrow(new com.example.financial_tracker.exception.ResourceNotFoundException("Transaction not found"));

    mockMvc.perform(get("/api/v1/transactions/999")
        .with(user(user)))
      .andExpect(status().isNotFound());
  }

  @Test
  @WithMockUser(username = "test@example.com")
  void testGetTransactionById_InvalidId() throws Exception {
    User user = createTestUser();

    mockMvc.perform(get("/api/v1/transactions/0")
        .with(user(user)))
      .andExpect(status().isInternalServerError());
  }

  @Test
  @WithMockUser(username = "test@example.com")
  void testCreateTransaction_Success() throws Exception {
    User user = createTestUser();
    TransactionDTO inputDto = createTestTransaction();
    inputDto.setId(null);
    
    TransactionDTO createdDto = createTestTransaction();
    
    when(transactionService.createTransaction(any(TransactionDTO.class), any(User.class)))
        .thenReturn(createdDto);

    mockMvc.perform(post("/api/v1/transactions")
        .with(user(user))
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(inputDto)))
      .andExpect(status().isCreated())
      .andExpect(jsonPath("$.id").value(1))
      .andExpect(jsonPath("$.amount").value(100.00));
  }

  @Test
  @WithMockUser(username = "test@example.com")
  void testCreateTransaction_InvalidData() throws Exception {
    User user = createTestUser();
    TransactionDTO invalidDto = new TransactionDTO();

    mockMvc.perform(post("/api/v1/transactions")
        .with(user(user))
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(invalidDto)))
      .andExpect(status().isBadRequest());
  }

  @Test
  @WithMockUser(username = "test@example.com")
  void testUpdateTransaction_Success() throws Exception {
    User user = createTestUser();
    TransactionDTO inputDto = createTestTransaction();
    inputDto.setDescription("Updated description");
    
    TransactionDTO updatedDto = createTestTransaction();
    updatedDto.setDescription("Updated description");
    
    when(transactionService.updateTransaction(eq(1L), any(TransactionDTO.class), any(User.class)))
        .thenReturn(updatedDto);

    mockMvc.perform(put("/api/v1/transactions/1")
        .with(user(user))
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(inputDto)))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.description").value("Updated description"));
  }

  @Test
  @WithMockUser(username = "test@example.com")
  void testDeleteTransaction_Success() throws Exception {
    User user = createTestUser();

    mockMvc.perform(delete("/api/v1/transactions/1")
        .with(user(user)))
      .andExpect(status().isNoContent());
  }

  @Test
  @WithMockUser(username = "test@example.com")
  void testGetBalance_Success() throws Exception {
    User user = createTestUser();
    BigDecimal balance = BigDecimal.valueOf(1500.00);
    
    when(transactionService.getBalanceByUser(any(User.class)))
        .thenReturn(balance);

    mockMvc.perform(get("/api/v1/transactions/balance")
        .with(user(user)))
      .andExpect(status().isOk())
      .andExpect(content().string("1500.0"));
  }

  @Test
  @WithMockUser(username = "test@example.com")
  void testGetStats_Success() throws Exception {
    User user = createTestUser();
    BigDecimal income = BigDecimal.valueOf(2000.00);
    BigDecimal expense = BigDecimal.valueOf(500.00);
    BigDecimal balance = BigDecimal.valueOf(1500.00);
    
    when(transactionService.getTotalIncomeByUser(any(User.class))).thenReturn(income);
    when(transactionService.getTotalExpenseByUser(any(User.class))).thenReturn(expense);
    when(transactionService.getBalanceByUser(any(User.class))).thenReturn(balance);

    mockMvc.perform(get("/api/v1/transactions/stats")
        .with(user(user)))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.income").value(2000.0))
      .andExpect(jsonPath("$.expense").value(500.0))
      .andExpect(jsonPath("$.balance").value(1500.0));
  }

  @Test
  @WithMockUser(username = "test@example.com")
  void testGetTransactionsWithFilters_Success() throws Exception {
    User user = createTestUser();
    List<TransactionDTO> filteredTransactions = List.of(createTestTransaction());
    
    when(transactionService.getTransactionsWithFilters(
        any(User.class), 
        eq(TransactionType.EXPENSE), 
        eq(1L), 
        any(LocalDate.class), 
        any(LocalDate.class)))
        .thenReturn(filteredTransactions);

    mockMvc.perform(get("/api/v1/transactions/filter")
        .with(user(user))
        .param("type", "EXPENSE")
        .param("categoryId", "1")
        .param("startDate", "2024-01-01")
        .param("endDate", "2024-12-31"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$").isArray())
      .andExpect(jsonPath("$[0].type").value("EXPENSE"));
  }

  @Test
  @WithMockUser(username = "test@example.com")
  void testGetTransactionsWithFilters_InvalidCategoryId() throws Exception {
    User user = createTestUser();

    mockMvc.perform(get("/api/v1/transactions/filter")
        .with(user(user))
        .param("categoryId", "0"))
      .andExpect(status().isInternalServerError());
  }

  @Test
  @WithMockUser(username = "test@example.com")
  void testGetIncomeTransactions_Success() throws Exception {
    User user = createTestUser();
    TransactionDTO incomeTransaction = createTestTransaction();
    incomeTransaction.setType("INCOME");
    List<TransactionDTO> transactions = List.of(incomeTransaction);
    
    when(transactionService.getTransactionsByType(any(User.class), eq(TransactionType.INCOME)))
        .thenReturn(transactions);

    mockMvc.perform(get("/api/v1/transactions/income")
        .with(user(user)))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$[0].type").value("INCOME"));
  }

  @Test
  @WithMockUser(username = "test@example.com")
  void testGetExpenseTransactions_Success() throws Exception {
    User user = createTestUser();
    List<TransactionDTO> transactions = List.of(createTestTransaction());
    
    when(transactionService.getTransactionsByType(any(User.class), eq(TransactionType.EXPENSE)))
        .thenReturn(transactions);

    mockMvc.perform(get("/api/v1/transactions/expense")
        .with(user(user)))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$[0].type").value("EXPENSE"));
  }
}