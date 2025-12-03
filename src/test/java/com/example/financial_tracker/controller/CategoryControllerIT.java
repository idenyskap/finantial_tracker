package com.example.financial_tracker.controller;

import com.example.financial_tracker.dto.CategoryDTO;
import com.example.financial_tracker.entity.Category;
import com.example.financial_tracker.entity.User;
import com.example.financial_tracker.enumerations.Role;
import com.example.financial_tracker.enumerations.TransactionType;
import com.example.financial_tracker.mapper.CategoryMapper;
import com.example.financial_tracker.service.CategoryService;
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

import java.util.List;

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
class CategoryControllerIT {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockBean
  private CategoryService categoryService;

  @MockBean
  private CategoryMapper categoryMapper;

  private User createTestUser() {
    User user = new User();
    user.setId(1L);
    user.setEmail("test@example.com");
    user.setName("Test User");
    user.setRole(Role.USER);
    return user;
  }

  private CategoryDTO createTestCategoryDTO() {
    CategoryDTO dto = new CategoryDTO();
    dto.setId(1L);
    dto.setName("Food & Dining");
    dto.setColor("#FF5733");
    dto.setType(TransactionType.EXPENSE);
    dto.setUserId(1L);
    return dto;
  }

  private Category createTestCategory() {
    Category category = new Category();
    category.setId(1L);
    category.setName("Food & Dining");
    category.setColor("#FF5733");
    return category;
  }

  @Test
  void testGetCategoriesByCurrentUser_Unauthorized() throws Exception {
    mockMvc.perform(get("/api/v1/categories")
        .accept(MediaType.APPLICATION_JSON))
      .andExpect(status().isForbidden());
  }

  @Test
  @WithMockUser(username = "test@example.com")
  void testGetCategoriesByCurrentUser_Success() throws Exception {
    User user = createTestUser();
    List<Category> categories = List.of(createTestCategory());
    List<CategoryDTO> categoryDTOs = List.of(createTestCategoryDTO());
    
    when(categoryService.getAllCategories(any(User.class)))
        .thenReturn(categories);
    when(categoryMapper.toDtoList(categories))
        .thenReturn(categoryDTOs);

    mockMvc.perform(get("/api/v1/categories")
        .with(user(user))
        .accept(MediaType.APPLICATION_JSON))
      .andExpect(status().isOk())
      .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
      .andExpect(jsonPath("$").isArray())
      .andExpect(jsonPath("$[0].id").value(1))
      .andExpect(jsonPath("$[0].name").value("Food & Dining"))
      .andExpect(jsonPath("$[0].color").value("#FF5733"));
  }

  @Test
  @WithMockUser(username = "test@example.com")
  void testGetCategoryById_Success() throws Exception {
    User user = createTestUser();
    CategoryDTO category = createTestCategoryDTO();
    
    when(categoryService.getCategoryById(eq(1L), any(User.class)))
        .thenReturn(category);

    mockMvc.perform(get("/api/v1/categories/1")
        .with(user(user)))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.id").value(1))
      .andExpect(jsonPath("$.name").value("Food & Dining"));
  }

  @Test
  @WithMockUser(username = "test@example.com")
  void testGetCategoryById_InvalidId() throws Exception {
    User user = createTestUser();

    mockMvc.perform(get("/api/v1/categories/0")
        .with(user(user)))
      .andExpect(status().isInternalServerError());
  }

  @Test
  @WithMockUser(username = "test@example.com")
  void testCreateCategory_Success() throws Exception {
    User user = createTestUser();
    CategoryDTO inputDto = createTestCategoryDTO();
    inputDto.setId(null);
    
    CategoryDTO createdDto = createTestCategoryDTO();
    
    when(categoryService.createCategory(any(CategoryDTO.class), any(User.class)))
        .thenReturn(createdDto);

    mockMvc.perform(post("/api/v1/categories")
        .with(user(user))
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(inputDto)))
      .andExpect(status().isCreated())
      .andExpect(jsonPath("$.id").value(1))
      .andExpect(jsonPath("$.name").value("Food & Dining"));
  }

  @Test
  @WithMockUser(username = "test@example.com")
  void testCreateCategory_InvalidData() throws Exception {
    User user = createTestUser();
    CategoryDTO invalidDto = new CategoryDTO();

    mockMvc.perform(post("/api/v1/categories")
        .with(user(user))
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(invalidDto)))
      .andExpect(status().isBadRequest());
  }

  @Test
  @WithMockUser(username = "test@example.com")
  void testUpdateCategory_Success() throws Exception {
    User user = createTestUser();
    CategoryDTO inputDto = createTestCategoryDTO();
    inputDto.setName("Updated Food Category");
    
    CategoryDTO updatedDto = createTestCategoryDTO();
    updatedDto.setName("Updated Food Category");
    
    when(categoryService.updateCategory(eq(1L), any(CategoryDTO.class), any(User.class)))
        .thenReturn(updatedDto);

    mockMvc.perform(put("/api/v1/categories/1")
        .with(user(user))
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(inputDto)))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.name").value("Updated Food Category"));
  }

  @Test
  @WithMockUser(username = "test@example.com")
  void testUpdateCategory_InvalidId() throws Exception {
    User user = createTestUser();
    CategoryDTO inputDto = createTestCategoryDTO();

    mockMvc.perform(put("/api/v1/categories/0")
        .with(user(user))
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(inputDto)))
      .andExpect(status().isInternalServerError());
  }

  @Test
  @WithMockUser(username = "test@example.com")
  void testDeleteCategory_Success() throws Exception {
    User user = createTestUser();

    doNothing().when(categoryService).deleteById(eq(1L), any(User.class));

    mockMvc.perform(delete("/api/v1/categories/1")
        .with(user(user)))
      .andExpect(status().isNoContent());
  }

  @Test
  @WithMockUser(username = "test@example.com")
  void testDeleteCategory_InvalidId() throws Exception {
    User user = createTestUser();

    mockMvc.perform(delete("/api/v1/categories/0")
        .with(user(user)))
      .andExpect(status().isInternalServerError());
  }
}