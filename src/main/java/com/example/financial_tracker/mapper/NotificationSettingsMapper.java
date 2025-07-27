package com.example.financial_tracker.mapper;

import com.example.financial_tracker.dto.NotificationSettingsDTO;
import com.example.financial_tracker.entity.NotificationSettings;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface NotificationSettingsMapper {
  NotificationSettingsDTO toDto(NotificationSettings entity);
  NotificationSettings toEntity(NotificationSettingsDTO dto);
}
