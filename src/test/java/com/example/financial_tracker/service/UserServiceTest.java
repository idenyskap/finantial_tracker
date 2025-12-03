package com.example.financial_tracker.service;

import com.example.financial_tracker.dto.ChangePasswordRequest;
import com.example.financial_tracker.dto.EmailChangeRequest;
import com.example.financial_tracker.dto.UpdateProfileRequest;
import com.example.financial_tracker.dto.UserDTO;
import com.example.financial_tracker.enumerations.Role;
import com.example.financial_tracker.entity.User;
import com.example.financial_tracker.mapper.UserMapper;
import com.example.financial_tracker.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

  @Mock
  private UserRepository userRepository;

  @Mock
  private UserMapper userMapper;

  @Mock
  private PasswordEncoder passwordEncoder;

  @Mock
  private TokenService tokenService;

  @Mock
  private EmailService emailService;

  @InjectMocks
  private UserService userService;

  private User testUser;
  private UserDTO testUserDTO;

  @BeforeEach
  void setUp() {
    testUser = new User();
    testUser.setId(1L);
    testUser.setEmail("test@example.com");
    testUser.setName("Test User");
    testUser.setPassword("encodedPassword");
    testUser.setRole(Role.USER);

    testUserDTO = new UserDTO();
    testUserDTO.setId(1L);
    testUserDTO.setEmail("test@example.com");
    testUserDTO.setName("Test User");

    ReflectionTestUtils.setField(userService, "baseUrl", "http://localhost:3000");
  }

  @Test
  void testLoadUserByUsername_Success() {
    when(userRepository.findByEmail("test@example.com"))
        .thenReturn(Optional.of(testUser));

    UserDetails result = userService.loadUserByUsername("test@example.com");

    assertNotNull(result);
    assertEquals("test@example.com", result.getUsername());
    verify(userRepository).findByEmail("test@example.com");
  }

  @Test
  void testLoadUserByUsername_NotFound() {
    when(userRepository.findByEmail("notfound@example.com"))
        .thenReturn(Optional.empty());

    UsernameNotFoundException exception = assertThrows(
        UsernameNotFoundException.class,
        () -> userService.loadUserByUsername("notfound@example.com")
    );

    assertTrue(exception.getMessage().contains("User not found with email"));
    verify(userRepository).findByEmail("notfound@example.com");
  }

  @Test
  void testGetUserByEmail_Success() {
    when(userRepository.findByEmail("test@example.com"))
        .thenReturn(Optional.of(testUser));

    User result = userService.getUserByEmail("test@example.com");

    assertNotNull(result);
    assertEquals(testUser.getId(), result.getId());
    assertEquals(testUser.getEmail(), result.getEmail());
    verify(userRepository).findByEmail("test@example.com");
  }

  @Test
  void testGetUserByEmail_NotFound() {
    when(userRepository.findByEmail("notfound@example.com"))
        .thenReturn(Optional.empty());

    RuntimeException exception = assertThrows(
        RuntimeException.class,
        () -> userService.getUserByEmail("notfound@example.com")
    );

    assertTrue(exception.getMessage().contains("User not found with email"));
  }

  @Test
  void testGetAllUsers_Success() {
    List<User> users = List.of(testUser);
    List<UserDTO> userDTOs = List.of(testUserDTO);

    when(userRepository.findAll()).thenReturn(users);
    when(userMapper.toDtoList(users)).thenReturn(userDTOs);

    List<UserDTO> result = userService.getAllUsers();

    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals(testUserDTO.getId(), result.get(0).getId());

    verify(userRepository).findAll();
    verify(userMapper).toDtoList(users);
  }

  @Test
  void testGetAllUsers_EmptyList() {
    when(userRepository.findAll()).thenReturn(List.of());
    when(userMapper.toDtoList(List.of())).thenReturn(List.of());

    List<UserDTO> result = userService.getAllUsers();

    assertNotNull(result);
    assertTrue(result.isEmpty());
  }

  @Test
  void testGetUserById_Success() {
    when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
    when(userMapper.toDto(testUser)).thenReturn(testUserDTO);

    UserDTO result = userService.getUserById(1L);

    assertNotNull(result);
    assertEquals(testUserDTO.getId(), result.getId());
    assertEquals(testUserDTO.getEmail(), result.getEmail());

    verify(userRepository).findById(1L);
    verify(userMapper).toDto(testUser);
  }

  @Test
  void testGetUserById_NotFound() {
    when(userRepository.findById(999L)).thenReturn(Optional.empty());

    UserDTO result = userService.getUserById(999L);

    assertNull(result);
    verify(userRepository).findById(999L);
    verify(userMapper, never()).toDto(any());
  }

  @Test
  void testCreateUser_Success() {
    UserDTO inputDTO = new UserDTO();
    inputDTO.setEmail("new@example.com");
    inputDTO.setName("New User");

    User mappedUser = new User();
    mappedUser.setEmail("new@example.com");
    mappedUser.setName("New User");

    User savedUser = new User();
    savedUser.setId(2L);
    savedUser.setEmail("new@example.com");
    savedUser.setName("New User");

    UserDTO expectedDTO = new UserDTO();
    expectedDTO.setId(2L);
    expectedDTO.setEmail("new@example.com");
    expectedDTO.setName("New User");

    when(userMapper.toEntity(inputDTO)).thenReturn(mappedUser);
    when(userRepository.save(mappedUser)).thenReturn(savedUser);
    when(userMapper.toDto(savedUser)).thenReturn(expectedDTO);

    UserDTO result = userService.createUser(inputDTO);

    assertNotNull(result);
    assertEquals(expectedDTO.getId(), result.getId());
    assertEquals(expectedDTO.getEmail(), result.getEmail());

    verify(userMapper).toEntity(inputDTO);
    verify(userRepository).save(mappedUser);
    verify(userMapper).toDto(savedUser);
  }

  @Test
  void testDeleteUser_Success() {
    User admin = new User();
    admin.setId(2L);
    admin.setRole(Role.ADMIN);

    userService.deleteUser(1L, admin);

    verify(userRepository).deleteById(1L);
  }

  @Test
  void testUpdateUser_Success() {
    UserDTO updateDTO = new UserDTO();
    updateDTO.setName("Updated Name");

    User updatedUser = new User();
    updatedUser.setId(1L);
    updatedUser.setName("Updated Name");

    UserDTO expectedDTO = new UserDTO();
    expectedDTO.setId(1L);
    expectedDTO.setName("Updated Name");

    when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
    when(userRepository.save(any(User.class))).thenReturn(updatedUser);
    when(userMapper.toDto(updatedUser)).thenReturn(expectedDTO);

    UserDTO result = userService.updateUser(1L, updateDTO);

    assertNotNull(result);
    assertEquals(expectedDTO.getName(), result.getName());

    verify(userRepository).findById(1L);
    verify(userRepository).save(argThat(user ->
        user.getName().equals("Updated Name")
    ));
    verify(userMapper).toDto(updatedUser);
  }

  @Test
  void testUpdateUser_NotFound() {
    UserDTO updateDTO = new UserDTO();
    updateDTO.setName("Updated Name");

    when(userRepository.findById(999L)).thenReturn(Optional.empty());

    RuntimeException exception = assertThrows(
        RuntimeException.class,
        () -> userService.updateUser(999L, updateDTO)
    );

    assertTrue(exception.getMessage().contains("User not found"));
    verify(userRepository).findById(999L);
    verify(userRepository, never()).save(any());
  }

  @Test
  void testSaveUser_Success() {
    User savedUser = new User();
    savedUser.setId(1L);
    when(userRepository.save(testUser)).thenReturn(savedUser);

    User result = userService.saveUser(testUser);

    assertNotNull(result);
    assertEquals(savedUser.getId(), result.getId());
    verify(userRepository).save(testUser);
  }

  @Test
  void testChangePassword_Success() {
    ChangePasswordRequest request = new ChangePasswordRequest();
    request.setCurrentPassword("currentPassword");
    request.setNewPassword("newPassword");

    when(passwordEncoder.matches("currentPassword", "encodedPassword"))
        .thenReturn(true);
    when(passwordEncoder.encode("newPassword"))
        .thenReturn("encodedNewPassword");

    userService.changePassword(testUser, request);

    verify(passwordEncoder).matches("currentPassword", "encodedPassword");
    verify(passwordEncoder).encode("newPassword");
    verify(userRepository).save(argThat(user ->
        user.getPassword().equals("encodedNewPassword")
    ));
  }

  @Test
  void testChangePassword_IncorrectCurrentPassword() {
    ChangePasswordRequest request = new ChangePasswordRequest();
    request.setCurrentPassword("wrongPassword");
    request.setNewPassword("newPassword");

    when(passwordEncoder.matches("wrongPassword", "encodedPassword"))
        .thenReturn(false);

    IllegalArgumentException exception = assertThrows(
        IllegalArgumentException.class,
        () -> userService.changePassword(testUser, request)
    );

    assertEquals("Current password is incorrect", exception.getMessage());
    verify(passwordEncoder).matches("wrongPassword", "encodedPassword");
    verify(passwordEncoder, never()).encode(any());
    verify(userRepository, never()).save(any());
  }

  @Test
  void testUpdateProfile_Success() {
    UpdateProfileRequest request = new UpdateProfileRequest();
    request.setName("Updated Profile Name");

    User updatedUser = new User();
    updatedUser.setId(1L);
    updatedUser.setName("Updated Profile Name");

    UserDTO expectedDTO = new UserDTO();
    expectedDTO.setId(1L);
    expectedDTO.setName("Updated Profile Name");

    when(userRepository.save(any(User.class))).thenReturn(updatedUser);
    when(userMapper.toDto(updatedUser)).thenReturn(expectedDTO);

    UserDTO result = userService.updateProfile(testUser, request);

    assertNotNull(result);
    assertEquals(expectedDTO.getName(), result.getName());

    verify(userRepository).save(argThat(user ->
        user.getName().equals("Updated Profile Name")
    ));
    verify(userMapper).toDto(updatedUser);
  }

  @Test
  void testRequestEmailChange_Success() {
    EmailChangeRequest request = new EmailChangeRequest();
    request.setNewEmail("new@example.com");
    request.setPassword("currentPassword");

    when(passwordEncoder.matches("currentPassword", "encodedPassword"))
        .thenReturn(true);
    when(userRepository.findByEmail("new@example.com"))
        .thenReturn(Optional.empty());
    when(tokenService.generateToken()).thenReturn("generated-token");
    when(tokenService.getExpiryDate()).thenReturn(LocalDateTime.now().plusHours(24));

    userService.requestEmailChange(testUser, request);

    verify(passwordEncoder).matches("currentPassword", "encodedPassword");
    verify(userRepository).findByEmail("new@example.com");
    verify(tokenService).generateToken();
    verify(tokenService).getExpiryDate();
    verify(userRepository).save(argThat(user ->
        user.getNewEmail().equals("new@example.com") &&
        user.getNewEmailToken().equals("generated-token")
    ));
    verify(emailService).sendEmail(eq("new@example.com"), anyString(), anyString(), anyMap(), any(), eq(testUser));
  }

  @Test
  void testRequestEmailChange_IncorrectPassword() {
    EmailChangeRequest request = new EmailChangeRequest();
    request.setNewEmail("new@example.com");
    request.setPassword("wrongPassword");

    when(passwordEncoder.matches("wrongPassword", "encodedPassword"))
        .thenReturn(false);

    IllegalArgumentException exception = assertThrows(
        IllegalArgumentException.class,
        () -> userService.requestEmailChange(testUser, request)
    );

    assertEquals("Incorrect password", exception.getMessage());
    verify(passwordEncoder).matches("wrongPassword", "encodedPassword");
    verify(userRepository, never()).findByEmail(any());
    verify(userRepository, never()).save(any());
  }

  @Test
  void testRequestEmailChange_EmailAlreadyExists() {
    EmailChangeRequest request = new EmailChangeRequest();
    request.setNewEmail("existing@example.com");
    request.setPassword("currentPassword");

    User existingUser = new User();
    existingUser.setEmail("existing@example.com");

    when(passwordEncoder.matches("currentPassword", "encodedPassword"))
        .thenReturn(true);
    when(userRepository.findByEmail("existing@example.com"))
        .thenReturn(Optional.of(existingUser));

    IllegalArgumentException exception = assertThrows(
        IllegalArgumentException.class,
        () -> userService.requestEmailChange(testUser, request)
    );

    assertEquals("Email is already in use", exception.getMessage());
    verify(passwordEncoder).matches("currentPassword", "encodedPassword");
    verify(userRepository).findByEmail("existing@example.com");
    verify(tokenService, never()).generateToken();
    verify(userRepository, never()).save(any());
  }

  @Test
  void testRepository_InteractionVerification() {
    when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
    when(userMapper.toDto(testUser)).thenReturn(testUserDTO);

    userService.getUserById(1L);

    verify(userRepository, times(1)).findById(1L);
    verifyNoMoreInteractions(userRepository);
  }

  @Test
  void testMapper_InteractionVerification() {
    when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
    when(userMapper.toDto(testUser)).thenReturn(testUserDTO);

    userService.getUserById(1L);

    verify(userMapper, times(1)).toDto(testUser);
    verifyNoMoreInteractions(userMapper);
  }
}