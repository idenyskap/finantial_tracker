package com.example.financial_tracker.service;

import com.example.financial_tracker.dto.BudgetDTO;
import com.example.financial_tracker.entity.Budget;
import com.example.financial_tracker.entity.Category;
import com.example.financial_tracker.entity.User;
import com.example.financial_tracker.enumerations.BudgetPeriod;
import com.example.financial_tracker.enumerations.Role;
import com.example.financial_tracker.exception.ResourceNotFoundException;
import com.example.financial_tracker.mapper.BudgetMapper;
import com.example.financial_tracker.repository.BudgetRepository;
import com.example.financial_tracker.repository.CategoryRepository;
import com.example.financial_tracker.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BudgetServiceTest {

  @Mock
  private BudgetRepository budgetRepository;

  @Mock
  private CategoryRepository categoryRepository;

  @Mock
  private TransactionRepository transactionRepository;

  @Mock
  private BudgetMapper budgetMapper;

  @InjectMocks
  private BudgetService budgetService;

  private User testUser;
  private Category testCategory;
  private Budget testBudget;
  private BudgetDTO testBudgetDTO;

  @BeforeEach
  void setUp() {
    testUser = new User();
    testUser.setId(1L);
    testUser.setEmail("test@example.com");
    testUser.setName("Test User");
    testUser.setRole(Role.USER);

    testCategory = new Category();
    testCategory.setId(1L);
    testCategory.setName("Food");
    testCategory.setUser(testUser);

    testBudget = new Budget();
    testBudget.setId(1L);
    testBudget.setName("Monthly Food Budget");
    testBudget.setAmount(BigDecimal.valueOf(500.00));
    testBudget.setPeriod(BudgetPeriod.MONTHLY);
    testBudget.setUser(testUser);
    testBudget.setCategory(testCategory);
    testBudget.setActive(true);
    testBudget.setNotifyThreshold(80);
    testBudget.setStartDate(LocalDate.of(2024, 1, 1));
    testBudget.setEndDate(LocalDate.of(2024, 1, 31));

    testBudgetDTO = new BudgetDTO();
    testBudgetDTO.setId(1L);
    testBudgetDTO.setName("Monthly Food Budget");
    testBudgetDTO.setAmount(BigDecimal.valueOf(500.00));
    testBudgetDTO.setPeriod(BudgetPeriod.MONTHLY);
    testBudgetDTO.setCategoryId(1L);
    testBudgetDTO.setCategoryName("Food");
    testBudgetDTO.setActive(true);
    testBudgetDTO.setNotifyThreshold(80);
  }

  @Test
  void testCreateBudget_WithCategory_Success() {
    BudgetDTO inputDTO = new BudgetDTO();
    inputDTO.setName("Test Budget");
    inputDTO.setAmount(BigDecimal.valueOf(1000.00));
    inputDTO.setCategoryId(1L);

    when(budgetMapper.toEntity(inputDTO)).thenReturn(testBudget);
    when(categoryRepository.findByIdAndUser(1L, testUser))
        .thenReturn(Optional.of(testCategory));
    when(budgetRepository.save(any(Budget.class))).thenReturn(testBudget);
    when(budgetMapper.toDto(testBudget)).thenReturn(testBudgetDTO);
    when(transactionRepository.getTotalExpenseByUserAndCategoryAndDateRange(
        eq(testUser), eq(testCategory), any(LocalDate.class), any(LocalDate.class)))
        .thenReturn(BigDecimal.valueOf(200.00));

    BudgetDTO result = budgetService.createBudget(testUser, inputDTO);

    assertNotNull(result);
    assertEquals(testBudgetDTO.getId(), result.getId());
    assertEquals(testBudgetDTO.getName(), result.getName());

    verify(categoryRepository).findByIdAndUser(1L, testUser);
    verify(budgetRepository).save(argThat(budget ->
        budget.getUser().equals(testUser) &&
        budget.getCategory().equals(testCategory)
    ));
  }

  @Test
  void testCreateBudget_WithoutCategory_Success() {
    BudgetDTO inputDTO = new BudgetDTO();
    inputDTO.setName("General Budget");
    inputDTO.setAmount(BigDecimal.valueOf(1000.00));
    inputDTO.setCategoryId(null);

    Budget generalBudget = new Budget();
    generalBudget.setUser(testUser);
    generalBudget.setCategory(null);
    generalBudget.setAmount(BigDecimal.valueOf(1000.00));
    generalBudget.setStartDate(LocalDate.of(2024, 1, 1));
    generalBudget.setEndDate(LocalDate.of(2024, 1, 31));

    when(budgetMapper.toEntity(inputDTO)).thenReturn(generalBudget);
    when(budgetRepository.save(any(Budget.class))).thenReturn(generalBudget);
    when(budgetMapper.toDto(generalBudget)).thenReturn(testBudgetDTO);
    when(transactionRepository.getTotalExpenseByUserAndDateRange(
        eq(testUser), any(LocalDate.class), any(LocalDate.class)))
        .thenReturn(BigDecimal.valueOf(300.00));

    BudgetDTO result = budgetService.createBudget(testUser, inputDTO);

    assertNotNull(result);
    verify(categoryRepository, never()).findByIdAndUser(any(), any());
    verify(budgetRepository).save(argThat(budget ->
        budget.getUser().equals(testUser) &&
        budget.getCategory() == null
    ));
  }

  @Test
  void testCreateBudget_CategoryNotFound() {
    BudgetDTO inputDTO = new BudgetDTO();
    inputDTO.setCategoryId(999L);

    when(budgetMapper.toEntity(inputDTO)).thenReturn(testBudget);
    when(categoryRepository.findByIdAndUser(999L, testUser))
        .thenReturn(Optional.empty());

    ResourceNotFoundException exception = assertThrows(
        ResourceNotFoundException.class,
        () -> budgetService.createBudget(testUser, inputDTO)
    );

    assertTrue(exception.getMessage().contains("Category not found"));
    verify(budgetRepository, never()).save(any());
  }

  @Test
  void testGetUserBudgets_Success() {
    List<Budget> budgets = List.of(testBudget);
    when(budgetRepository.findByUserAndActiveOrderByCreatedAtDesc(testUser, true))
        .thenReturn(budgets);
    when(budgetMapper.toDto(testBudget)).thenReturn(testBudgetDTO);
    when(transactionRepository.getTotalExpenseByUserAndCategoryAndDateRange(
        eq(testUser), eq(testCategory), any(LocalDate.class), any(LocalDate.class)))
        .thenReturn(BigDecimal.valueOf(150.00));

    List<BudgetDTO> result = budgetService.getUserBudgets(testUser);

    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals(testBudgetDTO.getId(), result.get(0).getId());

    verify(budgetRepository).findByUserAndActiveOrderByCreatedAtDesc(testUser, true);
  }

  @Test
  void testGetUserBudgets_EmptyList() {
    when(budgetRepository.findByUserAndActiveOrderByCreatedAtDesc(testUser, true))
        .thenReturn(List.of());

    List<BudgetDTO> result = budgetService.getUserBudgets(testUser);

    assertNotNull(result);
    assertTrue(result.isEmpty());
  }

  @Test
  void testGetBudgetById_Success() {
    when(budgetRepository.findByIdAndUser(1L, testUser))
        .thenReturn(Optional.of(testBudget));
    when(budgetMapper.toDto(testBudget)).thenReturn(testBudgetDTO);
    when(transactionRepository.getTotalExpenseByUserAndCategoryAndDateRange(
        eq(testUser), eq(testCategory), any(LocalDate.class), any(LocalDate.class)))
        .thenReturn(BigDecimal.valueOf(100.00));

    BudgetDTO result = budgetService.getBudgetById(testUser, 1L);

    assertNotNull(result);
    assertEquals(testBudgetDTO.getId(), result.getId());
    assertEquals(testBudgetDTO.getName(), result.getName());

    verify(budgetRepository).findByIdAndUser(1L, testUser);
  }

  @Test
  void testGetBudgetById_NotFound() {
    when(budgetRepository.findByIdAndUser(999L, testUser))
        .thenReturn(Optional.empty());

    ResourceNotFoundException exception = assertThrows(
        ResourceNotFoundException.class,
        () -> budgetService.getBudgetById(testUser, 999L)
    );

    assertTrue(exception.getMessage().contains("Budget not found"));
  }

  @Test
  void testUpdateBudget_Success() {
    BudgetDTO updateDTO = new BudgetDTO();
    updateDTO.setName("Updated Budget");
    updateDTO.setAmount(BigDecimal.valueOf(800.00));
    updateDTO.setPeriod(BudgetPeriod.WEEKLY);
    updateDTO.setNotifyThreshold(90);
    updateDTO.setActive(false);
    updateDTO.setCategoryId(1L);

    when(budgetRepository.findByIdAndUser(1L, testUser))
        .thenReturn(Optional.of(testBudget));
    when(categoryRepository.findByIdAndUser(1L, testUser))
        .thenReturn(Optional.of(testCategory));
    when(budgetRepository.save(any(Budget.class))).thenReturn(testBudget);
    when(budgetMapper.toDto(testBudget)).thenReturn(testBudgetDTO);
    when(transactionRepository.getTotalExpenseByUserAndCategoryAndDateRange(
        eq(testUser), eq(testCategory), any(LocalDate.class), any(LocalDate.class)))
        .thenReturn(BigDecimal.valueOf(250.00));

    BudgetDTO result = budgetService.updateBudget(testUser, 1L, updateDTO);

    assertNotNull(result);
    verify(budgetRepository).save(argThat(budget ->
        budget.getName().equals("Updated Budget") &&
        budget.getAmount().equals(BigDecimal.valueOf(800.00)) &&
        budget.getPeriod().equals(BudgetPeriod.WEEKLY) &&
        budget.getNotifyThreshold() == 90 &&
        !budget.isActive()
    ));
  }

  @Test
  void testUpdateBudget_RemoveCategory() {
    BudgetDTO updateDTO = new BudgetDTO();
    updateDTO.setName("General Budget");
    updateDTO.setAmount(BigDecimal.valueOf(1000.00));
    updateDTO.setCategoryId(null);
    updateDTO.setActive(true);

    when(budgetRepository.findByIdAndUser(1L, testUser))
        .thenReturn(Optional.of(testBudget));
    when(budgetRepository.save(any(Budget.class))).thenReturn(testBudget);
    when(budgetMapper.toDto(testBudget)).thenReturn(testBudgetDTO);
    when(transactionRepository.getTotalExpenseByUserAndDateRange(
        eq(testUser), any(LocalDate.class), any(LocalDate.class)))
        .thenReturn(BigDecimal.valueOf(400.00));

    BudgetDTO result = budgetService.updateBudget(testUser, 1L, updateDTO);

    assertNotNull(result);
    verify(budgetRepository).save(argThat(budget ->
        budget.getCategory() == null
    ));
    verify(categoryRepository, never()).findByIdAndUser(any(), any());
  }

  @Test
  void testUpdateBudget_BudgetNotFound() {
    BudgetDTO updateDTO = new BudgetDTO();
    when(budgetRepository.findByIdAndUser(999L, testUser))
        .thenReturn(Optional.empty());

    ResourceNotFoundException exception = assertThrows(
        ResourceNotFoundException.class,
        () -> budgetService.updateBudget(testUser, 999L, updateDTO)
    );

    assertTrue(exception.getMessage().contains("Budget not found"));
    verify(budgetRepository, never()).save(any());
  }

  @Test
  void testUpdateBudget_CategoryNotFound() {
    BudgetDTO updateDTO = new BudgetDTO();
    updateDTO.setCategoryId(999L);

    when(budgetRepository.findByIdAndUser(1L, testUser))
        .thenReturn(Optional.of(testBudget));
    when(categoryRepository.findByIdAndUser(999L, testUser))
        .thenReturn(Optional.empty());

    ResourceNotFoundException exception = assertThrows(
        ResourceNotFoundException.class,
        () -> budgetService.updateBudget(testUser, 1L, updateDTO)
    );

    assertTrue(exception.getMessage().contains("Category not found"));
    verify(budgetRepository, never()).save(any());
  }

  @Test
  void testDeleteBudget_Success() {
    when(budgetRepository.findByIdAndUser(1L, testUser))
        .thenReturn(Optional.of(testBudget));

    budgetService.deleteBudget(testUser, 1L);

    verify(budgetRepository).findByIdAndUser(1L, testUser);
    verify(budgetRepository).delete(testBudget);
  }

  @Test
  void testDeleteBudget_NotFound() {
    when(budgetRepository.findByIdAndUser(999L, testUser))
        .thenReturn(Optional.empty());

    ResourceNotFoundException exception = assertThrows(
        ResourceNotFoundException.class,
        () -> budgetService.deleteBudget(testUser, 999L)
    );

    assertTrue(exception.getMessage().contains("Budget not found"));
    verify(budgetRepository, never()).delete(any());
  }

  @Test
  void testMapBudgetWithSpent_CategoryBudget() {
    testBudget.setStartDate(null);
    testBudget.setEndDate(null);

    when(budgetRepository.findByIdAndUser(1L, testUser))
        .thenReturn(Optional.of(testBudget));
    when(budgetMapper.toDto(testBudget)).thenReturn(testBudgetDTO);
    when(transactionRepository.getTotalExpenseByUserAndCategoryAndDateRange(
        eq(testUser), eq(testCategory), any(LocalDate.class), any(LocalDate.class)))
        .thenReturn(BigDecimal.valueOf(150.00));

    BudgetDTO result = budgetService.getBudgetById(testUser, 1L);

    assertNotNull(result);
    verify(transactionRepository).getTotalExpenseByUserAndCategoryAndDateRange(
        eq(testUser), eq(testCategory), any(LocalDate.class), any(LocalDate.class)
    );
  }

  @Test
  void testMapBudgetWithSpent_GeneralBudget() {
    testBudget.setCategory(null);
    when(budgetRepository.findByIdAndUser(1L, testUser))
        .thenReturn(Optional.of(testBudget));
    when(budgetMapper.toDto(testBudget)).thenReturn(testBudgetDTO);
    when(transactionRepository.getTotalExpenseByUserAndDateRange(
        eq(testUser), any(LocalDate.class), any(LocalDate.class)))
        .thenReturn(BigDecimal.valueOf(200.00));

    BudgetDTO result = budgetService.getBudgetById(testUser, 1L);

    assertNotNull(result);
    verify(transactionRepository).getTotalExpenseByUserAndDateRange(
        eq(testUser), any(LocalDate.class), any(LocalDate.class)
    );
    verify(transactionRepository, never()).getTotalExpenseByUserAndCategoryAndDateRange(
        any(), any(), any(), any()
    );
  }

  @Test
  void testRepository_InteractionVerification() {
    List<Budget> budgets = List.of(testBudget);
    when(budgetRepository.findByUserAndActiveOrderByCreatedAtDesc(testUser, true))
        .thenReturn(budgets);
    when(budgetMapper.toDto(testBudget)).thenReturn(testBudgetDTO);
    when(transactionRepository.getTotalExpenseByUserAndCategoryAndDateRange(
        eq(testUser), eq(testCategory), any(LocalDate.class), any(LocalDate.class)))
        .thenReturn(BigDecimal.ZERO);

    budgetService.getUserBudgets(testUser);

    verify(budgetRepository, times(1)).findByUserAndActiveOrderByCreatedAtDesc(testUser, true);
    verifyNoMoreInteractions(budgetRepository);
  }
}