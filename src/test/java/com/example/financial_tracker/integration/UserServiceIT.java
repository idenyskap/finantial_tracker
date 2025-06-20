//package com.example.financial_tracker.integration;
//
//import com.example.financial_tracker.dto.UserDTO;
//import com.example.financial_tracker.entity.User;
//import com.example.financial_tracker.repository.UserRepository;
//import com.example.financial_tracker.service.UserService;
//import org.junit.jupiter.api.*;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//@SpringBootTest
//@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
//class UserServiceIT {
//
//  @Autowired
//  private UserService userService;
//
//  @Autowired
//  private UserRepository userRepository;
//
//  @Test
//  @Order(1)
//  void getAllUsers_ShouldReturnExistingUsers() {
//    List<UserDTO> users = userService.getAllUsers();
//    assertFalse(users.isEmpty(), "Users list should not be empty");
//  }
//
//  @Test
//  @Order(2)
//  void createUser_ShouldPersistAndReturnUserDTO() {
//    UserDTO newUser = new UserDTO();
//    newUser.setName("IntegrationTestUser");
//
//    UserDTO savedUser = userService.createUser(newUser);
//
//    assertNotNull(savedUser.getId());
//    assertEquals("IntegrationTestUser", savedUser.getName());
//
//
//  }
//
//  @Test
//  @Order(3)
//  void getUserById_ShouldReturnUser_WhenExists() {
//    User existing = userRepository.save(new User(null, "ExistingUser"));
//    UserDTO found = userService.getUserById(existing.getId());
//
//    assertNotNull(found);
//    assertEquals("ExistingUser", found.getName());
//
//    // cleanup
//    userRepository.delete(existing);
//  }
//
//  @Test
//  @Order(4)
//  void deleteUser_ShouldRemoveUser() {
//    User user = userRepository.save(new User(null, "ToDeleteUser"));
//
//    userService.deleteUser(user.getId());
//
//    assertFalse(userRepository.findById(user.getId()).isPresent());
//  }
//}
