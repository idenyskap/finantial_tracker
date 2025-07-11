package com.example.financial_tracker.service;

import com.example.financial_tracker.dto.ChangePasswordDTO;
import com.example.financial_tracker.dto.UserDTO;
import com.example.financial_tracker.entity.User;
import com.example.financial_tracker.exception.BusinessLogicException;
import com.example.financial_tracker.mapper.UserMapper;
import com.example.financial_tracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@Primary
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

  private final UserRepository userRepository;
  private final UserMapper userMapper;
  private final PasswordEncoder passwordEncoder;

  @Override
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    return userRepository.findByEmail(email)
      .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
  }

  public User getUserByEmail(String email) {
    return userRepository.findByEmail(email)
      .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
  }

  public List<UserDTO> getAllUsers() {
    List<User> users = userRepository.findAll();
    return userMapper.toDtoList(users);
  }

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

  public void deleteUser(Long id) {
    userRepository.deleteById(id);
  }

  public UserDTO updateUser(Long id, UserDTO dto) {
    User existing = userRepository.findById(id)
      .orElseThrow(() -> new RuntimeException("User not found"));

    existing.setName(dto.getName());

    User saved = userRepository.save(existing);
    return userMapper.toDto(saved);
  }

  public void changePassword(User user, ChangePasswordDTO dto) {
    if (!passwordEncoder.matches(dto.getCurrentPassword(), user.getPassword())) {
      throw new BusinessLogicException("Current password is incorrect");
    }

    user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
    userRepository.save(user);

    log.info("Password changed successfully for user: {}", user.getEmail());
  }
}
