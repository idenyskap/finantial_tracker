package com.example.financial_tracker.controller;

import com.example.financial_tracker.dto.CategoryDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class CategoryControllerIT {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Test
  void testGetCategoriesByUserId() throws Exception {
    mockMvc.perform(get("/api/categories?userId=1"))
      .andExpect(status().isOk())
      .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
  }

  @Test
  void testGetCategoryById() throws Exception {
    mockMvc.perform(get("/api/categories/1"))
      .andExpect(status().isOk())
      .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
  }

  @Test
  void testCreateCategory() throws Exception {
    CategoryDTO categoryDTO = new CategoryDTO();
    categoryDTO.setName("Test Category");
    categoryDTO.setColor("Test Color");
    categoryDTO.setUserId(1L);

    mockMvc.perform(post("/api/categories")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(categoryDTO)))
      .andExpect(status().isCreated())
      .andExpect(jsonPath("$.name").value("Test Category"))
      .andExpect(jsonPath("$.color").value("Test Color"))
      .andExpect(jsonPath("$.userId").value(1L));
  }

  @Test
  void testUpdateCategory() throws Exception {
    CategoryDTO categoryDTO = new CategoryDTO();
    categoryDTO.setName("Updated Category");
    categoryDTO.setColor("Updated Color");
    categoryDTO.setUserId(1L);

    mockMvc.perform(put("/api/categories/1")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(categoryDTO)))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.name").value("Updated Category"))
      .andExpect(jsonPath("$.color").value("Updated Color"))
      .andExpect(jsonPath("$.userId").value(1L));
  }

  @Test
  void testDeleteCategory() throws Exception {
    mockMvc.perform(delete("/api/categories/1"))
      .andExpect(status().isNoContent());
  }
}
