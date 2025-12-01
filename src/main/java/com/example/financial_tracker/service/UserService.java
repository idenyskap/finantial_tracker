package com.example.financial_tracker.service;

import com.example.financial_tracker.dto.UpdateProfileRequest;
import com.example.financial_tracker.dto.UserCurrencyPreferenceDTO;
import com.example.financial_tracker.dto.UserDTO;
import com.example.financial_tracker.entity.User;
import com.example.financial_tracker.mapper.UserMapper;
import com.example.financial_tracker.repository.UserRepository;
import com.example.financial_tracker.dto.ChangePasswordRequest;
import com.example.financial_tracker.dto.EmailChangeRequest;
import com.example.financial_tracker.entity.EmailHistory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Map;
import java.util.List;

@Slf4j
@Service
@Primary
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

  private final UserRepository userRepository;
  private final UserMapper userMapper;
  private final PasswordEncoder passwordEncoder;
  private final TokenService tokenService;
  private final EmailService emailService;

  @Value("${app.mail.base-url}")
  private String baseUrl;

  @Override
  @Transactional(readOnly = true)
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    return userRepository.findByEmail(email)
      .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
  }

  @Transactional(readOnly = true)
  public User getUserByEmail(String email) {
    return userRepository.findByEmail(email)
      .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
  }

  @Transactional(readOnly = true)
  public List<UserDTO> getAllUsers() {
    List<User> users = userRepository.findAll();
    return userMapper.toDtoList(users);
  }

  @Transactional(readOnly = true)
  public UserDTO getUserById(Long id) {
    return userRepository.findById(id)
      .map(userMapper::toDto)
      .orElse(null);
  }

  public UserDTO createUser(UserDTO userDTO) {
    User user = userMapper.toEntity(userDTO);
    User savedUser = userRepository.save(user);
    return userMapper.toDto(savedUser);
  }

  public void deleteUser(Long id, User currentUser) {
    if (id.equals(currentUser.getId())) {
      throw new IllegalArgumentException("Cannot delete your own account");
    }
    userRepository.deleteById(id);
  }

  public UserDTO updateUser(Long id, UserDTO dto) {
    User existing = userRepository.findById(id)
      .orElseThrow(() -> new RuntimeException("User not found"));

    existing.setName(dto.getName());

    User saved = userRepository.save(existing);
    return userMapper.toDto(saved);
  }

  public User saveUser(User user) {
    return userRepository.save(user);
  }

  public UserCurrencyPreferenceDTO updateCurrencyPreferences(User user, UserCurrencyPreferenceDTO preferences) {
    log.info("Updating currency preferences for user: {}", user.getEmail());

    user.setDefaultCurrency(preferences.getDefaultCurrency());
    user.setDisplaySecondaryCurrency(preferences.isDisplaySecondary());
    user.setSecondaryCurrency(preferences.getSecondaryCurrency());

    userRepository.save(user);

    return preferences;
  }

  public void changePassword(User user, ChangePasswordRequest request) {
    if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
      throw new IllegalArgumentException("Current password is incorrect");
    }

    user.setPassword(passwordEncoder.encode(request.getNewPassword()));
    userRepository.save(user);
  }

  public UserDTO updateProfile(User user, UpdateProfileRequest request) {
    user.setName(request.getName());
    User saved = userRepository.save(user);
    return userMapper.toDto(saved);
  }

  @Transactional
  public void requestEmailChange(User user, EmailChangeRequest request) {
    if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
      throw new IllegalArgumentException("Incorrect password");
    }

    if (userRepository.findByEmail(request.getNewEmail()).isPresent()) {
      throw new IllegalArgumentException("Email is already in use");
    }

    String token = tokenService.generateToken();
    user.setNewEmail(request.getNewEmail());
    user.setNewEmailToken(token);
    user.setNewEmailTokenExpiresAt(tokenService.getExpiryDate());
    userRepository.save(user);

    String confirmLink = baseUrl + "/confirm-email-change?token=" + token;

    Map<String, Object> variables = Map.of(
      "userName", user.getName(),
      "oldEmail", user.getEmail(),
      "newEmail", request.getNewEmail(),
      "confirmLink", confirmLink,
      "expiryHours", 24
    );

    emailService.sendEmail(
      request.getNewEmail(),
      "Confirm your new email address",
      "email-change-confirmation",
      variables,
      EmailHistory.EmailType.EMAIL_CHANGE,
      user
    );

    log.info("Email change request sent from {} to {}", user.getEmail(), request.getNewEmail());
  }

  @Transactional
  public void confirmEmailChange(String token) {
    User user = userRepository.findByNewEmailToken(token)
      .orElseThrow(() -> new IllegalArgumentException("Invalid token"));

    if (tokenService.isTokenExpired(user.getNewEmailTokenExpiresAt())) {
      throw new IllegalArgumentException("Token has expired");
    }

    String oldEmail = user.getEmail();
    user.setEmail(user.getNewEmail());
    user.setNewEmail(null);
    user.setNewEmailToken(null);
    user.setNewEmailTokenExpiresAt(null);
    userRepository.save(user);

    log.info("Email changed from {} to {}", oldEmail, user.getEmail());
  }
}
