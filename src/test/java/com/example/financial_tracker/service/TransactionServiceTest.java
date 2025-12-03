package com.example.financial_tracker.service;

import com.example.financial_tracker.dto.TransactionDTO;
import com.example.financial_tracker.entity.Category;
import com.example.financial_tracker.entity.Transaction;
import com.example.financial_tracker.entity.User;
import com.example.financial_tracker.enumerations.Role;
import com.example.financial_tracker.enumerations.TransactionType;
import com.example.financial_tracker.exception.AccessDeniedException;
import com.example.financial_tracker.exception.BusinessLogicException;
import com.example.financial_tracker.exception.ResourceNotFoundException;
import com.example.financial_tracker.mapper.TransactionMapper;
import com.example.financial_tracker.repository.CategoryRepository;
import com.example.financial_tracker.repository.TransactionRepository;
import com.example.financial_tracker.repository.BudgetRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

  @Mock
  private TransactionRepository transactionRepository;

  @Mock
  private TransactionMapper transactionMapper;

  @Mock
  private CategoryRepository categoryRepository;

  @Mock
  private SavedSearchService savedSearchService;

  @Mock
  private BudgetService budgetService;

  @Mock
  private BudgetRepository budgetRepository;

  @Mock
  private EmailService emailService;

  @InjectMocks
  private TransactionService transactionService;

  private User testUser;
  private User otherUser;
  private Category testCategory;
  private Transaction testTransaction;
  private TransactionDTO testTransactionDTO;

  @BeforeEach
  void setUp() {
    testUser = new User();
    testUser.setId(1L);
    testUser.setEmail("test@example.com");
    testUser.setName("Test User");
    testUser.setRole(Role.USER);

    otherUser = new User();
    otherUser.setId(2L);
    otherUser.setEmail("other@example.com");
    otherUser.setName("Other User");
    otherUser.setRole(Role.USER);

    testCategory = new Category();
    testCategory.setId(1L);
    testCategory.setName("Food");
    testCategory.setUser(testUser);
    testCategory.setType(TransactionType.EXPENSE);

    testTransaction = new Transaction();
    testTransaction.setId(1L);
    testTransaction.setAmount(BigDecimal.valueOf(100.00));
    testTransaction.setDescription("Test transaction");
    testTransaction.setType(TransactionType.EXPENSE);
    testTransaction.setDate(LocalDate.now());
    testTransaction.setUser(testUser);
    testTransaction.setCategory(testCategory);

    testTransactionDTO = new TransactionDTO();
    testTransactionDTO.setId(1L);
    testTransactionDTO.setAmount(BigDecimal.valueOf(100.00));
    testTransactionDTO.setDescription("Test transaction");
    testTransactionDTO.setType("EXPENSE");
    testTransactionDTO.setDate(LocalDate.now());
    testTransactionDTO.setCategoryId(1L);
    testTransactionDTO.setCategoryName("Food");
  }

  @Test
  void testGetTransactionsByUser_Success() {
    List<Transaction> transactions = List.of(testTransaction);
    List<TransactionDTO> expectedDTOs = List.of(testTransactionDTO);

    when(transactionRepository.findByUserOrderByDateDesc(testUser))
        .thenReturn(transactions);
    when(transactionMapper.toDtoList(transactions))
        .thenReturn(expectedDTOs);

    List<TransactionDTO> result = transactionService.getTransactionsByUser(testUser);

    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals(testTransactionDTO.getId(), result.get(0).getId());
    assertEquals(testTransactionDTO.getAmount(), result.get(0).getAmount());

    verify(transactionRepository).findByUserOrderByDateDesc(testUser);
    verify(transactionMapper).toDtoList(transactions);
  }

  @Test
  void testGetTransactionsByUser_EmptyList() {
    when(transactionRepository.findByUserOrderByDateDesc(testUser))
        .thenReturn(List.of());
    when(transactionMapper.toDtoList(any()))
        .thenReturn(List.of());

    List<TransactionDTO> result = transactionService.getTransactionsByUser(testUser);

    assertNotNull(result);
    assertTrue(result.isEmpty());

    verify(transactionRepository).findByUserOrderByDateDesc(testUser);
  }

  @Test
  void testGetTransactionsByUserPaginated_Success() {
    Pageable pageable = PageRequest.of(0, 10);
    List<Transaction> transactions = List.of(testTransaction);
    Page<Transaction> transactionPage = new PageImpl<>(transactions, pageable, 1);

    when(transactionRepository.findByUserOrderByDateDesc(testUser, pageable))
        .thenReturn(transactionPage);
    when(transactionMapper.toDto(testTransaction))
        .thenReturn(testTransactionDTO);

    Page<TransactionDTO> result = transactionService.getTransactionsByUser(testUser, pageable);

    assertNotNull(result);
    assertEquals(1, result.getTotalElements());
    assertEquals(1, result.getContent().size());
    assertEquals(testTransactionDTO.getId(), result.getContent().get(0).getId());

    verify(transactionRepository).findByUserOrderByDateDesc(testUser, pageable);
  }

  @Test
  void testGetTransactionById_Success() {
    when(transactionRepository.findByIdAndUser(1L, testUser))
        .thenReturn(Optional.of(testTransaction));
    when(transactionMapper.toDto(testTransaction))
        .thenReturn(testTransactionDTO);

    TransactionDTO result = transactionService.getTransactionById(1L, testUser);

    assertNotNull(result);
    assertEquals(testTransactionDTO.getId(), result.getId());
    assertEquals(testTransactionDTO.getAmount(), result.getAmount());

    verify(transactionRepository).findByIdAndUser(1L, testUser);
    verify(transactionMapper).toDto(testTransaction);
  }

  @Test
  void testGetTransactionById_NotFound() {
    when(transactionRepository.findByIdAndUser(999L, testUser))
        .thenReturn(Optional.empty());

    ResourceNotFoundException exception = assertThrows(
        ResourceNotFoundException.class,
        () -> transactionService.getTransactionById(999L, testUser)
    );

    assertTrue(exception.getMessage().contains("Transaction not found"));
    verify(transactionRepository).findByIdAndUser(999L, testUser);
    verify(transactionMapper, never()).toDto(any());
  }

  @Test
  void testCreateTransaction_Success() {
    TransactionDTO inputDTO = new TransactionDTO();
    inputDTO.setAmount(BigDecimal.valueOf(100.00));
    inputDTO.setDescription("Test transaction");
    inputDTO.setType("EXPENSE");
    inputDTO.setDate(LocalDate.now());
    inputDTO.setCategoryId(1L);

    when(categoryRepository.findById(1L))
        .thenReturn(Optional.of(testCategory));
    when(transactionMapper.toEntity(inputDTO))
        .thenReturn(testTransaction);
    when(transactionRepository.save(any(Transaction.class)))
        .thenReturn(testTransaction);
    when(transactionMapper.toDto(testTransaction))
        .thenReturn(testTransactionDTO);

    TransactionDTO result = transactionService.createTransaction(inputDTO, testUser);

    assertNotNull(result);
    assertEquals(testTransactionDTO.getId(), result.getId());
    assertEquals(testTransactionDTO.getAmount(), result.getAmount());

    verify(categoryRepository).findById(1L);
    verify(transactionRepository).save(any(Transaction.class));
    verify(transactionMapper).toDto(testTransaction);
  }

  @Test
  void testCreateTransaction_InvalidTransactionType() {
    TransactionDTO inputDTO = new TransactionDTO();
    inputDTO.setType("INVALID_TYPE");
    inputDTO.setCategoryId(1L);

    BusinessLogicException exception = assertThrows(
        BusinessLogicException.class,
        () -> transactionService.createTransaction(inputDTO, testUser)
    );

    assertTrue(exception.getMessage().contains("Invalid transaction type"));
    verify(transactionRepository, never()).save(any());
  }

  @Test
  void testCreateTransaction_CategoryNotFound() {
    TransactionDTO inputDTO = new TransactionDTO();
    inputDTO.setType("EXPENSE");
    inputDTO.setCategoryId(999L);

    when(categoryRepository.findById(999L))
        .thenReturn(Optional.empty());

    ResourceNotFoundException exception = assertThrows(
        ResourceNotFoundException.class,
        () -> transactionService.createTransaction(inputDTO, testUser)
    );

    assertTrue(exception.getMessage().contains("Category not found"));
    verify(transactionRepository, never()).save(any());
  }

  @Test
  void testCreateTransaction_CategoryBelongsToOtherUser() {
    Category otherUserCategory = new Category();
    otherUserCategory.setId(1L);
    otherUserCategory.setUser(otherUser);

    TransactionDTO inputDTO = new TransactionDTO();
    inputDTO.setType("EXPENSE");
    inputDTO.setCategoryId(1L);

    when(categoryRepository.findById(1L))
        .thenReturn(Optional.of(otherUserCategory));

    AccessDeniedException exception = assertThrows(
        AccessDeniedException.class,
        () -> transactionService.createTransaction(inputDTO, testUser)
    );

    assertEquals("Category does not belong to user", exception.getMessage());
    verify(transactionRepository, never()).save(any());
  }

  @Test
  void testCreateTransaction_WithValidUppercaseType() {
    TransactionDTO inputDTO = new TransactionDTO();
    inputDTO.setType("INCOME");
    inputDTO.setCategoryId(1L);

    when(categoryRepository.findById(1L))
        .thenReturn(Optional.of(testCategory));
    when(transactionMapper.toEntity(inputDTO))
        .thenReturn(testTransaction);
    when(transactionRepository.save(any(Transaction.class)))
        .thenReturn(testTransaction);
    when(transactionMapper.toDto(testTransaction))
        .thenReturn(testTransactionDTO);

    TransactionDTO result = transactionService.createTransaction(inputDTO, testUser);

    assertNotNull(result);
    verify(transactionRepository).save(any(Transaction.class));
  }

  @Test
  void testCreateTransaction_WithValidLowercaseType() {
    TransactionDTO inputDTO = new TransactionDTO();
    inputDTO.setType("expense");
    inputDTO.setCategoryId(1L);

    when(categoryRepository.findById(1L))
        .thenReturn(Optional.of(testCategory));
    when(transactionMapper.toEntity(inputDTO))
        .thenReturn(testTransaction);
    when(transactionRepository.save(any(Transaction.class)))
        .thenReturn(testTransaction);
    when(transactionMapper.toDto(testTransaction))
        .thenReturn(testTransactionDTO);

    TransactionDTO result = transactionService.createTransaction(inputDTO, testUser);

    assertNotNull(result);
    verify(transactionRepository).save(any(Transaction.class));
  }

  @Test
  void testCreateTransaction_WithNullType() {
    TransactionDTO inputDTO = new TransactionDTO();
    inputDTO.setType(null);
    inputDTO.setCategoryId(1L);

    when(categoryRepository.findById(1L))
        .thenReturn(Optional.of(testCategory));
    when(transactionMapper.toEntity(inputDTO))
        .thenReturn(testTransaction);
    when(transactionRepository.save(any(Transaction.class)))
        .thenReturn(testTransaction);
    when(transactionMapper.toDto(testTransaction))
        .thenReturn(testTransactionDTO);

    TransactionDTO result = transactionService.createTransaction(inputDTO, testUser);

    assertNotNull(result);
    verify(transactionRepository).save(any(Transaction.class));
  }

  @Test
  void testRepository_InteractionVerification() {
    List<Transaction> transactions = List.of(testTransaction);
    when(transactionRepository.findByUserOrderByDateDesc(testUser))
        .thenReturn(transactions);
    when(transactionMapper.toDtoList(transactions))
        .thenReturn(List.of(testTransactionDTO));

    transactionService.getTransactionsByUser(testUser);

    verify(transactionRepository, times(1)).findByUserOrderByDateDesc(testUser);
    verifyNoMoreInteractions(transactionRepository);
  }

  @Test
  void testMapper_InteractionVerification() {
    when(transactionRepository.findByIdAndUser(1L, testUser))
        .thenReturn(Optional.of(testTransaction));
    when(transactionMapper.toDto(testTransaction))
        .thenReturn(testTransactionDTO);

    transactionService.getTransactionById(1L, testUser);

    verify(transactionMapper, times(1)).toDto(testTransaction);
    verifyNoMoreInteractions(transactionMapper);
  }

  @Test
  void testCreateTransaction_TransactionEntitySetup() {
    TransactionDTO inputDTO = new TransactionDTO();
    inputDTO.setType("EXPENSE");
    inputDTO.setCategoryId(1L);

    Transaction mappedTransaction = new Transaction();
    when(categoryRepository.findById(1L))
        .thenReturn(Optional.of(testCategory));
    when(transactionMapper.toEntity(inputDTO))
        .thenReturn(mappedTransaction);
    when(transactionRepository.save(any(Transaction.class)))
        .thenAnswer(invocation -> {
          Transaction saved = invocation.getArgument(0);
          assertEquals(testUser, saved.getUser());
          assertEquals(testCategory, saved.getCategory());
          return saved;
        });
    when(transactionMapper.toDto(any()))
        .thenReturn(testTransactionDTO);

    transactionService.createTransaction(inputDTO, testUser);

    verify(transactionRepository).save(argThat(transaction ->
        transaction.getUser().equals(testUser) &&
        transaction.getCategory().equals(testCategory)
    ));
  }
}