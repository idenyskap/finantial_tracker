package com.example.financial_tracker.controller;

import com.example.financial_tracker.dto.AuthRequest;
import com.example.financial_tracker.dto.AuthResponse;
import com.example.financial_tracker.service.AuthService;
import com.example.financial_tracker.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
class AuthControllerIT {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockBean
  private AuthService authService;

  @MockBean
  private UserService userService;

  @Test
  void testRegister_Success() throws Exception {
    AuthRequest request = new AuthRequest();
    request.setEmail("test@example.com");
    request.setPassword("Password123");
    request.setName("Test User");

    when(authService.register(any(AuthRequest.class)))
        .thenReturn(new AuthResponse("test-token"));

    mockMvc.perform(post("/api/v1/auth/register")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
      .andExpect(status().isOk());
  }

  @Test
  void testRegister_InvalidEmail() throws Exception {
    AuthRequest request = new AuthRequest();
    request.setEmail("invalid-email");
    request.setPassword("Password123");
    request.setName("Test User");

    mockMvc.perform(post("/api/v1/auth/register")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
      .andExpect(status().isBadRequest());
  }

  @Test
  void testLogin_Success() throws Exception {
    AuthRequest request = new AuthRequest();
    request.setEmail("test@example.com");
    request.setPassword("Password123");

    when(authService.login(any(AuthRequest.class)))
        .thenReturn(new AuthResponse("test-token"));

    mockMvc.perform(post("/api/v1/auth/login")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
      .andExpect(status().isOk());
  }

  @Test
  void testLogin_EmptyCredentials() throws Exception {
    AuthRequest request = new AuthRequest();

    mockMvc.perform(post("/api/v1/auth/login")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
      .andExpect(status().isBadRequest());
  }

  @Test
  void testConfirmEmailChange_Success() throws Exception {
    doNothing().when(authService).confirmEmailChange(anyString());

    mockMvc.perform(post("/api/v1/auth/confirm-email-change")
        .param("token", "valid-token"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.message").value("Email changed successfully"));
  }

  @Test
  void testConfirmEmailChange_InvalidToken() throws Exception {
    doThrow(new IllegalArgumentException("Invalid token"))
      .when(authService).confirmEmailChange(anyString());

    mockMvc.perform(post("/api/v1/auth/confirm-email-change")
        .param("token", "invalid-token"))
      .andExpect(status().isBadRequest())
      .andExpect(jsonPath("$.message").value("Invalid token"));
  }

  @Test
  void testRequestPasswordReset_Success() throws Exception {
    Map<String, String> request = Map.of("email", "test@example.com");

    doNothing().when(authService).requestPasswordReset(anyString());

    mockMvc.perform(post("/api/v1/auth/request-password-reset")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.message").exists());
  }

  @Test
  void testRequestPasswordReset_EmptyEmail() throws Exception {
    Map<String, String> request = Map.of("email", "");

    mockMvc.perform(post("/api/v1/auth/request-password-reset")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
      .andExpect(status().isBadRequest())
      .andExpect(jsonPath("$.fieldErrors.email").value("Email is required"));
  }

  @Test
  void testResetPassword_Success() throws Exception {
    Map<String, String> request = Map.of(
      "token", "valid-token",
      "newPassword", "newpassword123"
    );

    doNothing().when(authService).resetPassword(anyString(), anyString());

    mockMvc.perform(post("/api/v1/auth/reset-password")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.message").value("Password has been reset successfully"));
  }

  @Test
  void testResetPassword_ShortPassword() throws Exception {
    Map<String, String> request = Map.of(
      "token", "valid-token",
      "newPassword", "123"
    );

    mockMvc.perform(post("/api/v1/auth/reset-password")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
      .andExpect(status().isBadRequest())
      .andExpect(jsonPath("$.fieldErrors.newPassword").value("Password must be at least 6 characters long"));
  }

  @Test
  void testResetPassword_MissingToken() throws Exception {
    Map<String, String> request = Map.of("newPassword", "newpassword123");

    mockMvc.perform(post("/api/v1/auth/reset-password")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
      .andExpect(status().isBadRequest())
      .andExpect(jsonPath("$.fieldErrors.token").value("Reset token is required"));
  }

  @Test
  void testVerifyEmail_Success() throws Exception {
    doNothing().when(authService).verifyEmail(anyString());

    mockMvc.perform(post("/api/v1/auth/verify-email")
        .param("token", "valid-token"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.message").value("Email verified successfully"));
  }

  @Test
  void testVerifyEmail_InvalidToken() throws Exception {
    doThrow(new IllegalArgumentException("Invalid verification token"))
      .when(authService).verifyEmail(anyString());

    mockMvc.perform(post("/api/v1/auth/verify-email")
        .param("token", "invalid-token"))
      .andExpect(status().isBadRequest())
      .andExpect(jsonPath("$.message").value("Invalid verification token"));
  }

  @Test
  void testResendVerificationEmail_Success() throws Exception {
    Map<String, String> request = Map.of("email", "test@example.com");

    doNothing().when(authService).resendVerificationEmail(anyString());

    mockMvc.perform(post("/api/v1/auth/resend-verification")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.message").value("Verification email sent"));
  }

  @Test
  void testResendVerificationEmail_EmptyEmail() throws Exception {
    Map<String, String> request = Map.of("email", "");

    mockMvc.perform(post("/api/v1/auth/resend-verification")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
      .andExpect(status().isBadRequest())
      .andExpect(jsonPath("$.fieldErrors.email").value("Email is required"));
  }

  @Test
  void testResendVerificationEmail_UserNotFound() throws Exception {
    Map<String, String> request = Map.of("email", "notfound@example.com");

    doThrow(new IllegalArgumentException("User not found"))
      .when(authService).resendVerificationEmail(anyString());

    mockMvc.perform(post("/api/v1/auth/resend-verification")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
      .andExpect(status().isBadRequest())
      .andExpect(jsonPath("$.message").value("User not found"));
  }
}