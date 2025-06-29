package com.example.financial_tracker.service;

import com.example.financial_tracker.dto.AuthRequest;
import com.example.financial_tracker.dto.AuthResponse;
import com.example.financial_tracker.entity.Role;
import com.example.financial_tracker.entity.User;
import com.example.financial_tracker.repository.UserRepository;
import com.example.financial_tracker.security.jwt.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;
  private final AuthenticationManager authenticationManager;

  public AuthResponse register(AuthRequest request) {
    User user = User.builder()
      .email(request.getEmail())
      .password(passwordEncoder.encode(request.getPassword()))
      .role(Role.USER)
      .name(request.getName())
      .build();

    userRepository.save(user);
    String token = jwtService.generateToken(user);
    return new AuthResponse(token);
  }


  public AuthResponse login(AuthRequest request) {
    authenticationManager.authenticate(
      new UsernamePasswordAuthenticationToken(
        request.getEmail(),
        request.getPassword()
      )
    );

    User user = userRepository.findByEmail(request.getEmail()).orElseThrow();
    String token = jwtService.generateToken(user);
    return new AuthResponse(token);
  }
}
