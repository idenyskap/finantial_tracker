package com.example.financial_tracker.mapper;

import com.example.financial_tracker.dto.UserDTO;
import com.example.financial_tracker.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

  @Mapping(source = "emailVerified", target = "emailVerified")
  @Mapping(source = "newEmail", target = "newEmail")
  UserDTO toDto(User entity);

  @Mapping(target = "password", ignore = true)
  @Mapping(target = "verificationToken", ignore = true)
  @Mapping(target = "verificationTokenExpiresAt", ignore = true)
  @Mapping(target = "resetPasswordToken", ignore = true)
  @Mapping(target = "resetPasswordTokenExpiresAt", ignore = true)
  @Mapping(target = "newEmailToken", ignore = true)
  @Mapping(target = "newEmailTokenExpiresAt", ignore = true)
  @Mapping(target = "notificationSettings", ignore = true)
  User toEntity(UserDTO dto);

  List<UserDTO> toDtoList(List<User> entities);
}
