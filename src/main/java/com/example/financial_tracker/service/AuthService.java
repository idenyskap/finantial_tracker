package com.example.financial_tracker.service;

import com.example.financial_tracker.dto.AuthRequest;
import com.example.financial_tracker.dto.AuthResponse;
import com.example.financial_tracker.entity.Role;
import com.example.financial_tracker.entity.User;
import com.example.financial_tracker.repository.UserRepository;
import com.example.financial_tracker.security.jwt.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;
  private final AuthenticationManager authenticationManager;

  @Transactional
  public AuthResponse register(AuthRequest request) {
    String email = request.getEmail();
    log.info("Registration attempt for email: {}", email);

    if (userRepository.findByEmail(email).isPresent()) {
      log.warn("Registration failed - email already exists: {}", email);
      throw new RuntimeException("User with this email already exists");
    }

    User user = User.builder()
      .name(request.getName())
      .email(email)
      .password(passwordEncoder.encode(request.getPassword()))
      .role(Role.USER)
      .build();

    User savedUser = userRepository.save(user);
    log.info("Successfully registered new user - ID: {}, Email: {}, Name: '{}'",
      savedUser.getId(), email, request.getName());

    String token = jwtService.generateToken(savedUser);
    log.debug("Generated JWT token for new user: {}", email);

    return new AuthResponse(token);
  }

  public AuthResponse login(AuthRequest request) {
    String email = request.getEmail();
    log.info("Login attempt for email: {}", email);

    try {
      authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(email, request.getPassword())
      );

      User user = userRepository.findByEmail(email)
        .orElseThrow(() -> {
          log.error("User not found after successful authentication: {}", email);
          return new RuntimeException("User not found");
        });

      log.info("Successful login for user - ID: {}, Email: {}, Role: {}",
        user.getId(), email, user.getRole());

      String token = jwtService.generateToken(user);
      log.debug("Generated JWT token for user: {}", email);

      return new AuthResponse(token);

    } catch (BadCredentialsException e) {
      log.warn("Failed login attempt - invalid credentials for email: {}", email);
      throw new BadCredentialsException("Invalid email or password");
    } catch (AuthenticationException e) {
      log.error("Authentication error for email: {} - {}", email, e.getMessage());
      throw new RuntimeException("Authentication failed");
    }
  }
}
