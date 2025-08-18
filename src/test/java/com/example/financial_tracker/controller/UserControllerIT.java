package com.example.financial_tracker.controller;

import com.example.financial_tracker.dto.UserDTO;
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
class UserControllerIT {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Test
  void testGetAllUsers() throws Exception {
    mockMvc.perform(get("/api/v1/users")
        .accept(MediaType.APPLICATION_JSON))
      .andExpect(status().isOk())
      .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
  }

  @Test
  void testGetUserById() throws Exception {
    mockMvc.perform(get("/api/v1/users/1"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.id").value(1));
  }

  @Test
  void testCreateUser() throws Exception {
    UserDTO userDTO = new UserDTO();
    userDTO.setName("Test User");

    mockMvc.perform(post("/api/v1/users")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(userDTO)))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.name").value("Test User"));
  }

  @Test
  void testUpdateUser() throws Exception {
    UserDTO userDTO = new UserDTO();
    userDTO.setName("Updated Name");

    mockMvc.perform(put("/api/v1/users/1")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(userDTO)))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.name").value("Updated Name"));
  }

  @Test
  void testDeleteUser() throws Exception {
    mockMvc.perform(delete("/api/v1/users/7"))
      .andExpect(status().isNoContent());
  }
}
