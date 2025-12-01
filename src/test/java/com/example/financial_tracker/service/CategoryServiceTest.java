package com.example.financial_tracker.service;

import com.example.financial_tracker.dto.CategoryDTO;
import com.example.financial_tracker.entity.Category;
import com.example.financial_tracker.enumerations.Role;
import com.example.financial_tracker.enumerations.TransactionType;
import com.example.financial_tracker.entity.User;
import com.example.financial_tracker.exception.AccessDeniedException;
import com.example.financial_tracker.exception.ResourceNotFoundException;
import com.example.financial_tracker.mapper.CategoryMapper;
import com.example.financial_tracker.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

  @Mock
  private CategoryRepository categoryRepository;

  @Mock
  private CategoryMapper categoryMapper;

  @InjectMocks
  private CategoryService categoryService;

  private User testUser;
  private User otherUser;
  private Category testCategory;
  private CategoryDTO testCategoryDTO;

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
    testCategory.setName("Food & Dining");
    testCategory.setColor("#FF5733");
    testCategory.setType(TransactionType.EXPENSE);
    testCategory.setUser(testUser);

    testCategoryDTO = new CategoryDTO();
    testCategoryDTO.setId(1L);
    testCategoryDTO.setName("Food & Dining");
    testCategoryDTO.setColor("#FF5733");
    testCategoryDTO.setType(TransactionType.EXPENSE);
    testCategoryDTO.setUserId(1L);
  }

  @Test
  void testGetAllCategories_Success() {
    // Given
    List<Category> categories = List.of(testCategory);
    when(categoryRepository.findByUserOrderByNameAsc(testUser))
        .thenReturn(categories);

    // When
    List<Category> result = categoryService.getAllCategories(testUser);

    // Then
    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals(testCategory.getId(), result.get(0).getId());
    assertEquals(testCategory.getName(), result.get(0).getName());

    verify(categoryRepository).findByUserOrderByNameAsc(testUser);
  }

  @Test
  void testGetAllCategories_EmptyList() {
    // Given
    when(categoryRepository.findByUserOrderByNameAsc(testUser))
        .thenReturn(List.of());

    // When
    List<Category> result = categoryService.getAllCategories(testUser);

    // Then
    assertNotNull(result);
    assertTrue(result.isEmpty());

    verify(categoryRepository).findByUserOrderByNameAsc(testUser);
  }

  @Test
  void testCreateCategory_Success() {
    // Given
    CategoryDTO inputDTO = new CategoryDTO();
    inputDTO.setName("New Category");
    inputDTO.setColor("#00FF00");
    inputDTO.setType(TransactionType.INCOME);

    Category mappedCategory = new Category();
    mappedCategory.setName("New Category");
    mappedCategory.setColor("#00FF00");
    mappedCategory.setType(TransactionType.INCOME);

    Category savedCategory = new Category();
    savedCategory.setId(2L);
    savedCategory.setName("New Category");
    savedCategory.setColor("#00FF00");
    savedCategory.setType(TransactionType.INCOME);
    savedCategory.setUser(testUser);

    CategoryDTO expectedDTO = new CategoryDTO();
    expectedDTO.setId(2L);
    expectedDTO.setName("New Category");
    expectedDTO.setColor("#00FF00");
    expectedDTO.setType(TransactionType.INCOME);
    expectedDTO.setUserId(1L);

    when(categoryMapper.toEntity(inputDTO)).thenReturn(mappedCategory);
    when(categoryRepository.save(any(Category.class))).thenReturn(savedCategory);
    when(categoryMapper.toDto(savedCategory)).thenReturn(expectedDTO);

    // When
    CategoryDTO result = categoryService.createCategory(inputDTO, testUser);

    // Then
    assertNotNull(result);
    assertEquals(expectedDTO.getId(), result.getId());
    assertEquals(expectedDTO.getName(), result.getName());
    assertEquals(expectedDTO.getColor(), result.getColor());

    verify(categoryMapper).toEntity(inputDTO);
    verify(categoryRepository).save(argThat(category ->
        category.getUser().equals(testUser) &&
        category.getName().equals("New Category")
    ));
    verify(categoryMapper).toDto(savedCategory);
  }

  @Test
  void testGetCategoryById_Success() {
    // Given
    when(categoryRepository.findById(1L))
        .thenReturn(Optional.of(testCategory));
    when(categoryMapper.toDto(testCategory))
        .thenReturn(testCategoryDTO);

    // When
    CategoryDTO result = categoryService.getCategoryById(1L, testUser);

    // Then
    assertNotNull(result);
    assertEquals(testCategoryDTO.getId(), result.getId());
    assertEquals(testCategoryDTO.getName(), result.getName());

    verify(categoryRepository).findById(1L);
    verify(categoryMapper).toDto(testCategory);
  }

  @Test
  void testGetCategoryById_NotFound() {
    // Given
    when(categoryRepository.findById(999L))
        .thenReturn(Optional.empty());

    // When & Then
    ResourceNotFoundException exception = assertThrows(
        ResourceNotFoundException.class,
        () -> categoryService.getCategoryById(999L, testUser)
    );

    assertTrue(exception.getMessage().contains("Category"));
    verify(categoryRepository).findById(999L);
    verify(categoryMapper, never()).toDto(any());
  }

  @Test
  void testGetCategoryById_AccessDenied() {
    // Given
    Category otherUserCategory = new Category();
    otherUserCategory.setId(1L);
    otherUserCategory.setUser(otherUser);

    when(categoryRepository.findById(1L))
        .thenReturn(Optional.of(otherUserCategory));

    // When & Then
    AccessDeniedException exception = assertThrows(
        AccessDeniedException.class,
        () -> categoryService.getCategoryById(1L, testUser)
    );

    assertEquals("You don't have permission to access this category", exception.getMessage());
    verify(categoryRepository).findById(1L);
    verify(categoryMapper, never()).toDto(any());
  }

  @Test
  void testDeleteById_Success() {
    // Given
    when(categoryRepository.findById(1L))
        .thenReturn(Optional.of(testCategory));

    // When
    categoryService.deleteById(1L, testUser);

    // Then
    verify(categoryRepository).findById(1L);
    verify(categoryRepository).delete(testCategory);
  }

  @Test
  void testDeleteById_NotFound() {
    // Given
    when(categoryRepository.findById(999L))
        .thenReturn(Optional.empty());

    // When & Then
    ResourceNotFoundException exception = assertThrows(
        ResourceNotFoundException.class,
        () -> categoryService.deleteById(999L, testUser)
    );

    assertTrue(exception.getMessage().contains("Category"));
    verify(categoryRepository).findById(999L);
    verify(categoryRepository, never()).delete(any());
  }

  @Test
  void testDeleteById_AccessDenied() {
    // Given
    Category otherUserCategory = new Category();
    otherUserCategory.setId(1L);
    otherUserCategory.setUser(otherUser);

    when(categoryRepository.findById(1L))
        .thenReturn(Optional.of(otherUserCategory));

    // When & Then
    AccessDeniedException exception = assertThrows(
        AccessDeniedException.class,
        () -> categoryService.deleteById(1L, testUser)
    );

    assertEquals("You don't have permission to delete this category", exception.getMessage());
    verify(categoryRepository).findById(1L);
    verify(categoryRepository, never()).delete(any());
  }

  @Test
  void testUpdateCategory_Success() {
    // Given
    CategoryDTO updateDTO = new CategoryDTO();
    updateDTO.setName("Updated Food");
    updateDTO.setColor("#FFFF00");
    updateDTO.setType(TransactionType.EXPENSE);

    Category updatedCategory = new Category();
    updatedCategory.setId(1L);
    updatedCategory.setName("Updated Food");
    updatedCategory.setColor("#FFFF00");
    updatedCategory.setType(TransactionType.EXPENSE);
    updatedCategory.setUser(testUser);

    CategoryDTO expectedDTO = new CategoryDTO();
    expectedDTO.setId(1L);
    expectedDTO.setName("Updated Food");
    expectedDTO.setColor("#FFFF00");
    expectedDTO.setType(TransactionType.EXPENSE);
    expectedDTO.setUserId(1L);

    when(categoryRepository.findById(1L))
        .thenReturn(Optional.of(testCategory));
    when(categoryRepository.save(any(Category.class)))
        .thenReturn(updatedCategory);
    when(categoryMapper.toDto(updatedCategory))
        .thenReturn(expectedDTO);

    // When
    CategoryDTO result = categoryService.updateCategory(1L, updateDTO, testUser);

    // Then
    assertNotNull(result);
    assertEquals(expectedDTO.getName(), result.getName());
    assertEquals(expectedDTO.getColor(), result.getColor());

    verify(categoryRepository).findById(1L);
    verify(categoryRepository).save(argThat(category ->
        category.getName().equals("Updated Food") &&
        category.getColor().equals("#FFFF00")
    ));
    verify(categoryMapper).toDto(updatedCategory);
  }

  @Test
  void testUpdateCategory_NotFound() {
    // Given
    CategoryDTO updateDTO = new CategoryDTO();
    updateDTO.setName("Updated Food");

    when(categoryRepository.findById(999L))
        .thenReturn(Optional.empty());

    // When & Then
    ResourceNotFoundException exception = assertThrows(
        ResourceNotFoundException.class,
        () -> categoryService.updateCategory(999L, updateDTO, testUser)
    );

    assertTrue(exception.getMessage().contains("Category"));
    verify(categoryRepository).findById(999L);
    verify(categoryRepository, never()).save(any());
  }

  @Test
  void testUpdateCategory_AccessDenied() {
    // Given
    Category otherUserCategory = new Category();
    otherUserCategory.setId(1L);
    otherUserCategory.setUser(otherUser);

    CategoryDTO updateDTO = new CategoryDTO();
    updateDTO.setName("Updated Food");

    when(categoryRepository.findById(1L))
        .thenReturn(Optional.of(otherUserCategory));

    // When & Then
    AccessDeniedException exception = assertThrows(
        AccessDeniedException.class,
        () -> categoryService.updateCategory(1L, updateDTO, testUser)
    );

    assertEquals("You don't have permission to update this category", exception.getMessage());
    verify(categoryRepository).findById(1L);
    verify(categoryRepository, never()).save(any());
  }

  @Test
  void testRepository_InteractionVerification() {
    // Given
    List<Category> categories = List.of(testCategory);
    when(categoryRepository.findByUserOrderByNameAsc(testUser))
        .thenReturn(categories);

    // When
    categoryService.getAllCategories(testUser);

    // Then
    verify(categoryRepository, times(1)).findByUserOrderByNameAsc(testUser);
    verifyNoMoreInteractions(categoryRepository);
  }

  @Test
  void testMapper_InteractionVerification() {
    // Given
    when(categoryRepository.findById(1L))
        .thenReturn(Optional.of(testCategory));
    when(categoryMapper.toDto(testCategory))
        .thenReturn(testCategoryDTO);

    // When
    categoryService.getCategoryById(1L, testUser);

    // Then
    verify(categoryMapper, times(1)).toDto(testCategory);
    verifyNoMoreInteractions(categoryMapper);
  }

  @Test
  void testCreateCategory_EntitySetup() {
    // Given
    CategoryDTO inputDTO = new CategoryDTO();
    inputDTO.setName("Test Category");

    Category mappedCategory = new Category();
    when(categoryMapper.toEntity(inputDTO)).thenReturn(mappedCategory);
    when(categoryRepository.save(any(Category.class)))
        .thenAnswer(invocation -> {
          Category saved = invocation.getArgument(0);
          assertEquals(testUser, saved.getUser());
          return saved;
        });
    when(categoryMapper.toDto(any())).thenReturn(testCategoryDTO);

    // When
    categoryService.createCategory(inputDTO, testUser);

    // Then
    verify(categoryRepository).save(argThat(category ->
        category.getUser().equals(testUser)
    ));
  }
}