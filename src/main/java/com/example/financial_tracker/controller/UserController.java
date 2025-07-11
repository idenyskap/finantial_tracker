package com.example.financial_tracker.controller;

import com.example.financial_tracker.dto.ChangePasswordDTO;
import com.example.financial_tracker.dto.UserDTO;
import com.example.financial_tracker.entity.User;
import com.example.financial_tracker.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;

  @GetMapping
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<List<UserDTO>> getAllUsers(
    @AuthenticationPrincipal User currentUser,
    HttpServletRequest request) {

    log.info("GET /api/users - Admin: {} from IP: {} requesting all users",
      currentUser.getEmail(), getClientIpAddress(request));

    List<UserDTO> users = userService.getAllUsers();

    log.info("Returning {} users to admin: {}", users.size(), currentUser.getEmail());
    return ResponseEntity.ok(users);
  }

  @GetMapping("/me")
  public ResponseEntity<UserDTO> getCurrentUser(
    @AuthenticationPrincipal User user,
    HttpServletRequest request) {

    log.info("GET /api/users/me - User: {} from IP: {}",
      user.getEmail(), getClientIpAddress(request));

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
      id, currentUser.getEmail(), getClientIpAddress(request));

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
      user.getEmail(), getClientIpAddress(request));

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
      currentUser.getEmail(), getClientIpAddress(request));

    return ResponseEntity.ok(userService.createUser(userDTO));
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<Void> deleteUser(
    @PathVariable Long id,
    @AuthenticationPrincipal User currentUser,
    HttpServletRequest request) {

    log.warn("DELETE /api/users/{} - Admin: {} from IP: {} attempting to delete user",
      id, currentUser.getEmail(), getClientIpAddress(request));

    if (id.equals(currentUser.getId())) {
      log.warn("Admin: {} attempted to delete their own account", currentUser.getEmail());
      return ResponseEntity.badRequest().build();
    }

    userService.deleteUser(id);

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
      id, currentUser.getEmail(), getClientIpAddress(request));

    UserDTO updated = userService.updateUser(id, dto);
    return ResponseEntity.ok(updated);
  }

  private String getClientIpAddress(HttpServletRequest request) {
    String xForwardedFor = request.getHeader("X-Forwarded-For");
    if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
      return xForwardedFor.split(",")[0].trim();
    }

    String xRealIp = request.getHeader("X-Real-IP");
    if (xRealIp != null && !xRealIp.isEmpty()) {
      return xRealIp;
    }

    return request.getRemoteAddr();
  }

  @PutMapping("/me/password")
  public ResponseEntity<Void> changePassword(
    @AuthenticationPrincipal User user,
    @RequestBody @Valid ChangePasswordDTO dto,
    HttpServletRequest request) {

    log.info("PUT /api/users/me/password - User: {} from IP: {} changing password",
      user.getEmail(), getClientIpAddress(request));

    userService.changePassword(user, dto);
    return ResponseEntity.ok().build();
  }
}
