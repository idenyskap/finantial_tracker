package com.example.financial_tracker.controller;

import com.example.financial_tracker.dto.BudgetDTO;
import com.example.financial_tracker.entity.User;
import com.example.financial_tracker.enumerations.Role;
import com.example.financial_tracker.enumerations.BudgetPeriod;
import com.example.financial_tracker.service.BudgetService;
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
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doNothing;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
class BudgetControllerIT {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockBean
  private BudgetService budgetService;

  private User createTestUser() {
    User user = new User();
    user.setId(1L);
    user.setEmail("test@example.com");
    user.setName("Test User");
    user.setRole(Role.USER);
    return user;
  }

  private BudgetDTO createTestBudget() {
    BudgetDTO dto = new BudgetDTO();
    dto.setId(1L);
    dto.setName("Monthly Budget");
    dto.setAmount(BigDecimal.valueOf(1000.00));
    dto.setPeriod(BudgetPeriod.MONTHLY);
    dto.setCategoryId(1L);
    dto.setCategoryName("Food");
    dto.setSpent(BigDecimal.valueOf(200.00));
    dto.setRemaining(BigDecimal.valueOf(800.00));
    dto.setPercentUsed(BigDecimal.valueOf(20.0));
    return dto;
  }

  @Test
  void testGetUserBudgets_Unauthorized() throws Exception {
    mockMvc.perform(get("/api/v1/budgets")
        .accept(MediaType.APPLICATION_JSON))
      .andExpect(status().isForbidden());
  }

  @Test
  @WithMockUser(username = "test@example.com")
  void testGetUserBudgets_Success() throws Exception {
    User user = createTestUser();
    List<BudgetDTO> budgets = List.of(createTestBudget());
    
    when(budgetService.getUserBudgets(any(User.class)))
        .thenReturn(budgets);

    mockMvc.perform(get("/api/v1/budgets")
        .with(user(user))
        .accept(MediaType.APPLICATION_JSON))
      .andExpect(status().isOk())
      .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
      .andExpect(jsonPath("$").isArray())
      .andExpect(jsonPath("$[0].id").value(1))
      .andExpect(jsonPath("$[0].name").value("Monthly Budget"))
      .andExpect(jsonPath("$[0].amount").value(1000.00));
  }

  @Test
  @WithMockUser(username = "test@example.com")
  void testGetBudgetById_Success() throws Exception {
    User user = createTestUser();
    BudgetDTO budget = createTestBudget();
    
    when(budgetService.getBudgetById(any(User.class), eq(1L)))
        .thenReturn(budget);

    mockMvc.perform(get("/api/v1/budgets/1")
        .with(user(user)))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.id").value(1))
      .andExpect(jsonPath("$.name").value("Monthly Budget"));
  }

  @Test
  @WithMockUser(username = "test@example.com")
  void testCreateBudget_Success() throws Exception {
    User user = createTestUser();
    BudgetDTO inputDto = createTestBudget();
    inputDto.setId(null);
    
    BudgetDTO createdDto = createTestBudget();
    
    when(budgetService.createBudget(any(User.class), any(BudgetDTO.class)))
        .thenReturn(createdDto);

    mockMvc.perform(post("/api/v1/budgets")
        .with(user(user))
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(inputDto)))
      .andExpect(status().isCreated())
      .andExpect(jsonPath("$.id").value(1))
      .andExpect(jsonPath("$.name").value("Monthly Budget"));
  }

  @Test
  @WithMockUser(username = "test@example.com")
  void testCreateBudget_InvalidData() throws Exception {
    User user = createTestUser();
    BudgetDTO invalidDto = new BudgetDTO();

    mockMvc.perform(post("/api/v1/budgets")
        .with(user(user))
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(invalidDto)))
      .andExpect(status().isBadRequest());
  }

  @Test
  @WithMockUser(username = "test@example.com")
  void testUpdateBudget_Success() throws Exception {
    User user = createTestUser();
    BudgetDTO inputDto = createTestBudget();
    inputDto.setName("Updated Budget");
    
    BudgetDTO updatedDto = createTestBudget();
    updatedDto.setName("Updated Budget");
    
    when(budgetService.updateBudget(any(User.class), eq(1L), any(BudgetDTO.class)))
        .thenReturn(updatedDto);

    mockMvc.perform(put("/api/v1/budgets/1")
        .with(user(user))
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(inputDto)))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.name").value("Updated Budget"));
  }

  @Test
  @WithMockUser(username = "test@example.com")
  void testDeleteBudget_Success() throws Exception {
    User user = createTestUser();

    doNothing().when(budgetService).deleteBudget(any(User.class), eq(1L));

    mockMvc.perform(delete("/api/v1/budgets/1")
        .with(user(user)))
      .andExpect(status().isNoContent());
  }

  @Test
  @WithMockUser(username = "test@example.com")
  void testCheckCategoryBudget_Success() throws Exception {
    User user = createTestUser();
    BudgetDTO budget = createTestBudget();

    when(budgetService.findBudgetByCategory(any(User.class), eq(1L)))
        .thenReturn(Optional.of(budget));

    mockMvc.perform(get("/api/v1/budgets/check/1")
        .with(user(user)))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.id").value(1))
      .andExpect(jsonPath("$.categoryId").value(1));
  }

  @Test
  @WithMockUser(username = "test@example.com")
  void testCheckCategoryBudget_NotFound() throws Exception {
    User user = createTestUser();

    when(budgetService.findBudgetByCategory(any(User.class), eq(999L)))
        .thenReturn(Optional.empty());

    mockMvc.perform(get("/api/v1/budgets/check/999")
        .with(user(user)))
      .andExpect(status().isNoContent());
  }
}