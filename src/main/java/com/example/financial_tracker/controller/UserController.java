package com.example.financial_tracker.controller;

import com.example.financial_tracker.dto.*;
import com.example.financial_tracker.entity.User;
import com.example.financial_tracker.service.UserService;
import com.example.financial_tracker.util.RequestUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;

  @GetMapping
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<List<UserDTO>> getAllUsers(
    @AuthenticationPrincipal User currentUser,
    HttpServletRequest request) {

    log.info("GET /api/users - Admin: {} from IP: {} requesting all users",
      currentUser.getEmail(), RequestUtils.getClientIpAddress(request));

    List<UserDTO> users = userService.getAllUsers();

    log.info("Returning {} users to admin: {}", users.size(), currentUser.getEmail());
    return ResponseEntity.ok(users);
  }

  @GetMapping("/me")
  public ResponseEntity<UserDTO> getCurrentUser(
    @AuthenticationPrincipal User user,
    HttpServletRequest request) {

    log.info("GET /api/users/me - User: {} from IP: {}",
      user.getEmail(), RequestUtils.getClientIpAddress(request));

    UserDTO userDTO = userService.getUserById(user.getId());
    return ResponseEntity.ok(userDTO);
  }

  @GetMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<UserDTO> getUserById(
    @PathVariable Long id,
    @AuthenticationPrincipal User currentUser,
    HttpServletRequest request) {

    log.info("GET /api/users/{} - Admin: {} from IP: {}",
      id, currentUser.getEmail(), RequestUtils.getClientIpAddress(request));

    UserDTO user = userService.getUserById(id);
    if (user == null) {
      return ResponseEntity.notFound().build();
    }
    return ResponseEntity.ok(user);
  }

  @PutMapping("/me")
  public ResponseEntity<UserDTO> updateCurrentUser(
    @RequestBody UserDTO userDTO,
    @AuthenticationPrincipal User user,
    HttpServletRequest request) {

    log.info("PUT /api/users/me - User: {} from IP: {} updating profile",
      user.getEmail(), RequestUtils.getClientIpAddress(request));

    UserDTO updated = userService.updateUser(user.getId(), userDTO);
    return ResponseEntity.ok(updated);
  }

  @PostMapping
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<UserDTO> createUser(
    @RequestBody UserDTO userDTO,
    @AuthenticationPrincipal User currentUser,
    HttpServletRequest request) {

    log.info("POST /api/users - Admin: {} from IP: {} creating user",
      currentUser.getEmail(), RequestUtils.getClientIpAddress(request));

    return ResponseEntity.ok(userService.createUser(userDTO));
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<Void> deleteUser(
    @PathVariable Long id,
    @AuthenticationPrincipal User currentUser,
    HttpServletRequest request) {

    log.warn("DELETE /api/users/{} - Admin: {} from IP: {} attempting to delete user",
      id, currentUser.getEmail(), RequestUtils.getClientIpAddress(request));

    userService.deleteUser(id, currentUser);

    log.warn("User ID: {} successfully deleted by admin: {}", id, currentUser.getEmail());
    return ResponseEntity.noContent().build();
  }

  @PutMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<UserDTO> updateUser(
    @PathVariable Long id,
    @RequestBody UserDTO dto,
    @AuthenticationPrincipal User currentUser,
    HttpServletRequest request) {

    log.info("PUT /api/users/{} - Admin: {} from IP: {}",
      id, currentUser.getEmail(), RequestUtils.getClientIpAddress(request));

    UserDTO updated = userService.updateUser(id, dto);
    return ResponseEntity.ok(updated);
  }

  @PutMapping("/change-password")
  public ResponseEntity<Map<String, String>> changePassword(
    @AuthenticationPrincipal User user,
    @RequestBody @Valid ChangePasswordRequest request,
    HttpServletRequest httpRequest) {

    log.info("PUT /api/users/change-password - User: {} from IP: {} changing password",
      user.getEmail(), RequestUtils.getClientIpAddress(httpRequest));

    userService.changePassword(user, request);

    log.info("Password successfully changed for user: {}", user.getEmail());

    return ResponseEntity.ok(Map.of("message", "Password changed successfully"));
  }

  @PostMapping("/request-email-change")
  public ResponseEntity<Map<String, String>> requestEmailChange(
    @AuthenticationPrincipal User user,
    @RequestBody @Valid EmailChangeRequest request,
    HttpServletRequest httpRequest) {

    log.info("POST /api/users/request-email-change - User: {} from IP: {} requesting email change to {}",
      user.getEmail(), RequestUtils.getClientIpAddress(httpRequest), request.getNewEmail());

    userService.requestEmailChange(user, request);

    return ResponseEntity.ok(Map.of("message", "Confirmation email sent to new address"));
  }

  @GetMapping("/profile")
  public ResponseEntity<UserDTO> getProfile(
    @AuthenticationPrincipal User user,
    HttpServletRequest request) {

    log.info("GET /api/users/profile - User: {} from IP: {}",
      user.getEmail(), RequestUtils.getClientIpAddress(request));

    UserDTO userDTO = userService.getUserById(user.getId());
    return ResponseEntity.ok(userDTO);
  }

  @PutMapping("/profile")
  public ResponseEntity<UserDTO> updateProfile(
    @RequestBody @Valid UpdateProfileRequest request,
    @AuthenticationPrincipal User user,
    HttpServletRequest httpRequest) {

    log.info("PUT /api/users/profile - User: {} from IP: {} updating profile",
      user.getEmail(), RequestUtils.getClientIpAddress(httpRequest));

    UserDTO updated = userService.updateProfile(user, request);
    return ResponseEntity.ok(updated);
  }
}
