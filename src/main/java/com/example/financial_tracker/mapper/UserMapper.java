package com.example.financial_tracker.mapper;

import com.example.financial_tracker.dto.UserDTO;
import com.example.financial_tracker.entity.User;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {
  UserDTO toDto(User user);
  List<UserDTO> toDtoList(List<User> users);

  User toEntity(UserDTO dto);
}
