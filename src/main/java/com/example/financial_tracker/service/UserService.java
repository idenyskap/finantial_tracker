package com.example.financial_tracker.service;

import com.example.financial_tracker.dto.UserDTO;
import com.example.financial_tracker.entity.User;
import com.example.financial_tracker.mapper.UserMapper;
import com.example.financial_tracker.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

  private final UserRepository userRepository;
  private final UserMapper userMapper;

  public UserService(UserRepository userRepository, UserMapper userMapper) {
    this.userRepository = userRepository;
    this.userMapper = userMapper;
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
}
